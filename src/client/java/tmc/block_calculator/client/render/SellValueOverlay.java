package tmc.block_calculator.client.render;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import tmc.block_calculator.client.config.ModConfig;
import tmc.block_calculator.client.config.SellableItemsCache;
import tmc.block_calculator.client.config.TooltipPosition;
import tmc.block_calculator.client.inventory.InventoryScope;
import tmc.block_calculator.client.mixin.AbstractContainerScreenAccessor;
import tmc.block_calculator.client.pricing.PriceCalculator;
import tmc.block_calculator.client.pricing.SellableItemKey;
import tmc.block_calculator.client.session.ServerSessionState;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Draws the sell-value tooltip next to any open container/inventory screen, flush against the
 * vanilla GUI panel via {@link AbstractContainerScreenAccessor}. Recomputes from live slot
 * contents every frame - trivial cost at the small slot counts involved.
 */
public final class SellValueOverlay {
	private static final int PANEL_MARGIN = 4;
	private static final int LINE_HEIGHT = 10;
	// Full ARGB (opaque alpha byte set) - 0xFFFFFF alone has a zero alpha byte and renders invisible.
	private static final int TEXT_COLOR = 0xFFFFFFFF;
	private static final int BACKGROUND_COLOR = 0xC0101010;

	private SellValueOverlay() {
	}

	public static void register() {
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
				return;
			}
			ScreenEvents.afterExtract(screen).register((s, graphics, mouseX, mouseY, tickDelta) ->
					render(client, containerScreen, graphics));
		});
	}

	private static void render(Minecraft client, AbstractContainerScreen<?> screen, GuiGraphicsExtractor graphics) {
		if (!ServerSessionState.isEnabled()) {
			return;
		}
		LocalPlayer player = client.player;
		if (player == null) {
			return;
		}

		ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		List<ItemStack> stacks = InventoryScope.collectStacks(screen, player);
		PriceCalculator.PricingResult result = PriceCalculator.calculate(stacks, SellableItemsCache.get(), config.priceMode.multiplier);

		Font font = client.font;
		NumberFormat currencyFormat = NumberFormat.getIntegerInstance(Locale.US);
		int panelWidth = computeWidth(font, result, config, currencyFormat);
		int lineCount = result.lines().size() + 2; // header + total
		int panelHeight = lineCount * LINE_HEIGHT + PANEL_MARGIN * 2;

		AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
		int x = config.tooltipPosition == TooltipPosition.RIGHT
				? accessor.getLeftPos() + accessor.getImageWidth() + PANEL_MARGIN
				: accessor.getLeftPos() - panelWidth - PANEL_MARGIN;
		int y = accessor.getTopPos();

		graphics.fill(x, y, x + panelWidth, y + panelHeight, BACKGROUND_COLOR);

		int textX = x + PANEL_MARGIN;
		int textY = y + PANEL_MARGIN;

		graphics.text(font, "Mode: " + config.priceMode.label, textX, textY, TEXT_COLOR);
		textY += LINE_HEIGHT;

		for (PriceCalculator.LineItem line : result.lines()) {
			graphics.text(font, formatLine(line, currencyFormat), textX, textY, TEXT_COLOR);
			textY += LINE_HEIGHT;
		}

		graphics.text(font, "Total - $" + currencyFormat.format(result.grandTotal()), textX, textY, TEXT_COLOR);
	}

	private static int computeWidth(Font font, PriceCalculator.PricingResult result, ModConfig config, NumberFormat currencyFormat) {
		int width = font.width("Mode: " + config.priceMode.label);
		width = Math.max(width, font.width("Total - $" + currencyFormat.format(result.grandTotal())));
		for (PriceCalculator.LineItem line : result.lines()) {
			width = Math.max(width, font.width(formatLine(line, currencyFormat)));
		}
		return width + PANEL_MARGIN * 2;
	}

	private static String formatLine(PriceCalculator.LineItem line, NumberFormat currencyFormat) {
		return line.count() + "x " + displayName(line.key()) + " - $" + currencyFormat.format(line.totalValue());
	}

	private static String displayName(SellableItemKey key) {
		if (!key.customName().isEmpty()) {
			return key.customName();
		}
		return key.item().getName(new ItemStack(key.item())).getString();
	}
}
