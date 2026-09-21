package tmc.block_calculator.client.render;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
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
 * vanilla GUI panel via {@link AbstractContainerScreenAccessor}, clamped so it always stays fully
 * on-screen (matters at large GUI scales, where a naive flush position can run off the edge).
 * Recomputes from live slot contents every frame - trivial cost at the small slot counts involved.
 */
public final class SellValueOverlay {
	private static final int PANEL_MARGIN = 4;
	private static final int LINE_HEIGHT = 10;
	private static final int COLUMN_GAP_SPACES = 4;
	// Full ARGB (opaque alpha byte set) - 0xFFFFFF alone has a zero alpha byte and renders invisible.
	private static final int TEXT_COLOR = 0xFFFFFFFF;
	private static final int PRICE_COLOR = 0xFF55FF55;
	private static final int BACKGROUND_RGB = 0x101010;

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
		ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		if (!config.tooltipEnabled) {
			return;
		}
		LocalPlayer player = client.player;
		if (player == null) {
			return;
		}

		List<ItemStack> stacks = InventoryScope.collectStacks(screen, player);
		PriceCalculator.PricingResult result = PriceCalculator.calculate(stacks, SellableItemsCache.get(), config.priceMode.multiplier);

		Font font = client.font;
		NumberFormat currencyFormat = NumberFormat.getIntegerInstance(Locale.US);
		String modeText = "Mode: " + config.priceMode.label;
		String totalName = "Total";
		String totalPrice = "$" + currencyFormat.format(result.grandTotal());

		int nameColumnWidth = font.width(totalName);
		int priceColumnWidth = font.width(totalPrice);
		for (PriceCalculator.LineItem line : result.lines()) {
			nameColumnWidth = Math.max(nameColumnWidth, font.width(rowName(line)));
			priceColumnWidth = Math.max(priceColumnWidth, font.width(rowPrice(line, currencyFormat)));
		}
		int gapWidth = font.width(" ".repeat(COLUMN_GAP_SPACES));
		int contentWidth = Math.max(font.width(modeText), nameColumnWidth + gapWidth + priceColumnWidth);
		int panelWidth = contentWidth + PANEL_MARGIN * 2;

		int lineCount = result.lines().size() + 4; // mode + blank + items + blank + total
		int panelHeight = lineCount * LINE_HEIGHT + PANEL_MARGIN * 2;

		AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
		int x = config.tooltipPosition == TooltipPosition.RIGHT
				? accessor.getLeftPos() + accessor.getImageWidth() + PANEL_MARGIN
				: accessor.getLeftPos() - panelWidth - PANEL_MARGIN;
		int y = accessor.getTopPos();
		x = clamp(x, 0, Math.max(0, screen.width - panelWidth));
		y = clamp(y, 0, Math.max(0, screen.height - panelHeight));

		int backgroundColor = (opacityToAlphaByte(config.backgroundOpacityPercent) << 24) | BACKGROUND_RGB;
		graphics.fill(x, y, x + panelWidth, y + panelHeight, backgroundColor);

		int textX = x + PANEL_MARGIN;
		int priceRightEdge = textX + nameColumnWidth + gapWidth + priceColumnWidth;
		int textY = y + PANEL_MARGIN;

		graphics.text(font, Component.literal("Mode").withStyle(ChatFormatting.BOLD), textX, textY, TEXT_COLOR);
		graphics.text(font, ": " + config.priceMode.label, textX + font.width("Mode"), textY, TEXT_COLOR);
		textY += LINE_HEIGHT * 2; // header line + blank line

		for (PriceCalculator.LineItem line : result.lines()) {
			String price = rowPrice(line, currencyFormat);
			graphics.text(font, rowName(line), textX, textY, TEXT_COLOR);
			graphics.text(font, price, priceRightEdge - font.width(price), textY, PRICE_COLOR);
			textY += LINE_HEIGHT;
		}

		textY += LINE_HEIGHT; // blank line before total
		graphics.text(font, Component.literal(totalName).withStyle(ChatFormatting.BOLD), textX, textY, TEXT_COLOR);
		graphics.text(font, totalPrice, priceRightEdge - font.width(totalPrice), textY, PRICE_COLOR);
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(value, max));
	}

	private static int opacityToAlphaByte(int percent) {
		return Math.round(percent / 100.0f * 255) & 0xFF;
	}

	private static String rowName(PriceCalculator.LineItem line) {
		return line.count() + "x " + displayName(line.key());
	}

	private static String rowPrice(PriceCalculator.LineItem line, NumberFormat currencyFormat) {
		return "$" + currencyFormat.format(line.totalValue());
	}

	private static String displayName(SellableItemKey key) {
		if (!key.customName().isEmpty()) {
			return key.customName();
		}
		return key.item().getName(new ItemStack(key.item())).getString();
	}
}
