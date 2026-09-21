package tmc.block_calculator.client.session;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import tmc.block_calculator.client.TrappedMCBlockCalculatorClient;
import tmc.block_calculator.client.config.ModConfig;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Tracks whether the mod should be active for the current connection: enabled only when the
 * current server's address matches {@link ModConfig#serverAllowlistRegex}, never in singleplayer
 * or on a non-matching server. Recomputed once per join/disconnect, not every frame.
 */
public final class ServerSessionState {
	private static volatile boolean enabled = false;

	private ServerSessionState() {
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void onJoin(Minecraft client) {
		enabled = computeEnabled(client);
	}

	public static void onDisconnect() {
		enabled = false;
	}

	private static boolean computeEnabled(Minecraft client) {
		ServerData server = client.getCurrentServer();
		if (server == null || server.ip == null) {
			return false;
		}
		String regex = AutoConfig.getConfigHolder(ModConfig.class).getConfig().serverAllowlistRegex;
		try {
			return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(server.ip).find();
		} catch (PatternSyntaxException e) {
			TrappedMCBlockCalculatorClient.LOGGER.warn("Invalid serverAllowlistRegex '{}', disabling mod for this session", regex, e);
			return false;
		}
	}
}
