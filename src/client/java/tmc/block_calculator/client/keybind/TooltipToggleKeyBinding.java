package tmc.block_calculator.client.keybind;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import tmc.block_calculator.client.TrappedMCBlockCalculatorClient;
import tmc.block_calculator.client.config.ModConfig;
import tmc.block_calculator.client.session.ServerSessionState;

/**
 * A single rebindable key (default unbound) that toggles the tooltip on/off entirely. Mirrors
 * {@link PriceModeKeyBinding}'s dual tick+screen-scoped registration so it also works while a
 * screen has focus.
 */
public final class TooltipToggleKeyBinding {
	private static KeyMapping keyMapping;

	private TooltipToggleKeyBinding() {
	}

	public static void register() {
		keyMapping = new KeyMapping(
				"key.trappedmc-block-calculator.toggle_tooltip",
				GLFW.GLFW_KEY_UNKNOWN,
				TrappedMCBlockCalculatorClient.KEY_CATEGORY
		);
		KeyMappingHelper.registerKeyMapping(keyMapping);

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
				ScreenKeyboardEvents.afterKeyPress(screen).register((s, keyEvent) -> {
					if (ServerSessionState.isEnabled() && keyMapping.matches(keyEvent)) {
						toggle();
					}
				}));
	}

	public static void tick(Minecraft client) {
		if (!ServerSessionState.isEnabled()) {
			return;
		}
		while (keyMapping.consumeClick()) {
			toggle();
		}
	}

	private static void toggle() {
		ConfigHolder<ModConfig> holder = AutoConfig.getConfigHolder(ModConfig.class);
		ModConfig config = holder.getConfig();
		config.tooltipEnabled = !config.tooltipEnabled;
		holder.save();
	}
}
