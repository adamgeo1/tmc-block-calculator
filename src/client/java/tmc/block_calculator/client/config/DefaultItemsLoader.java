package tmc.block_calculator.client.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tmc.block_calculator.client.TrappedMCBlockCalculatorClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads the mod's bundled {@code default_items.json} (namespace:item_id -> price) and converts it
 * to the "namespace:item_id=price" line format used by {@link ModConfig#sellableItems}. This is the
 * seed for a fresh config and the source Cloth Config's per-field reset restores.
 */
public final class DefaultItemsLoader {
	private static final String RESOURCE_PATH = "/default_items.json";

	private DefaultItemsLoader() {
	}

	public static List<String> loadAsEntries() {
		List<String> entries = new ArrayList<>();
		try (InputStream stream = DefaultItemsLoader.class.getResourceAsStream(RESOURCE_PATH)) {
			if (stream == null) {
				TrappedMCBlockCalculatorClient.LOGGER.warn("Bundled {} not found; starting with an empty sellable-items list", RESOURCE_PATH);
				return entries;
			}
			Type type = new TypeToken<LinkedHashMap<String, Double>>() {
			}.getType();
			Map<String, Double> defaults = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), type);
			if (defaults != null) {
				for (Map.Entry<String, Double> entry : defaults.entrySet()) {
					entries.add(entry.getKey() + "=" + entry.getValue());
				}
			}
		} catch (IOException e) {
			TrappedMCBlockCalculatorClient.LOGGER.warn("Failed to read bundled {}", RESOURCE_PATH, e);
		}
		return entries;
	}
}
