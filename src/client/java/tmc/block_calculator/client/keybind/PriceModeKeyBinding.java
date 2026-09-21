package tmc.block_calculator.client.keybind;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
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
 */
public final class PriceModeKeyBinding {
	private static KeyMapping keyMapping;

	private PriceModeKeyBinding() {
	}

	public static void register() {
		KeyMapping.Category category = KeyMapping.Category.register(TrappedMCBlockCalculatorClient.id("main"));
		keyMapping = new KeyMapping(
				"key.trappedmc-block-calculator.cycle_price_mode",
				GLFW.GLFW_KEY_UNKNOWN,
				category
		);
		KeyMappingHelper.registerKeyMapping(keyMapping);
	}

	public static void tick(Minecraft client) {
		if (!ServerSessionState.isEnabled()) {
			return;
		}
		while (keyMapping.consumeClick()) {
			ConfigHolder<ModConfig> holder = AutoConfig.getConfigHolder(ModConfig.class);
			ModConfig config = holder.getConfig();
			config.priceMode = config.priceMode.next();
			holder.save();
		}
	}
}
