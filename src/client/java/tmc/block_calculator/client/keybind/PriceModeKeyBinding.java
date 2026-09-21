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
 * A single rebindable key (default unbound) that cycles the active {@link tmc.block_calculator.client.config.PriceMode}.
 * Only responds while {@link ServerSessionState} is enabled. Each press persists the new mode via
 * Cloth Config's config holder; a small synchronous JSON write per press, negligible given human
 * key-press frequency.
 *
 * <p>Vanilla suppresses every {@link KeyMapping}'s click/held state entirely while any screen has
 * focus (the same mechanism that stops WASD movement while a chest is open) - only a couple of
 * hardcoded vanilla keys are exempt. So {@link #tick} (which polls {@code consumeClick()}) only
 * ever fires with no screen open; while a screen IS open, {@link #register} listens for the raw
 * key event via Fabric's screen-scoped keyboard hook instead.
 */
public final class PriceModeKeyBinding {
	private static KeyMapping keyMapping;

	private PriceModeKeyBinding() {
	}

	public static void register() {
		keyMapping = new KeyMapping(
				"key.trappedmc-block-calculator.cycle_price_mode",
				GLFW.GLFW_KEY_UNKNOWN,
				TrappedMCBlockCalculatorClient.KEY_CATEGORY
		);
		KeyMappingHelper.registerKeyMapping(keyMapping);

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
				ScreenKeyboardEvents.afterKeyPress(screen).register((s, keyEvent) -> {
					if (ServerSessionState.isEnabled() && keyMapping.matches(keyEvent)) {
						cycleMode();
					}
				}));
	}

	public static void tick(Minecraft client) {
		if (!ServerSessionState.isEnabled()) {
			return;
		}
		while (keyMapping.consumeClick()) {
			cycleMode();
		}
	}

	private static void cycleMode() {
		ConfigHolder<ModConfig> holder = AutoConfig.getConfigHolder(ModConfig.class);
		ModConfig config = holder.getConfig();
		config.priceMode = config.priceMode.next();
		holder.save();
	}
}
