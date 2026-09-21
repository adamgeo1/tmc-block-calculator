package tmc.block_calculator.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tmc.block_calculator.client.config.ModConfig;
import tmc.block_calculator.client.session.ServerSessionState;

public class TrappedMCBlockCalculatorClient implements ClientModInitializer {
	public static final String MOD_ID = "trappedmc-block-calculator";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ServerSessionState.onJoin(client));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ServerSessionState.onDisconnect());
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
