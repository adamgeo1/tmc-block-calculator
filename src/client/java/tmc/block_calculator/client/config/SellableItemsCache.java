package tmc.block_calculator.client.config;

import me.shedaniel.autoconfig.AutoConfig;
import tmc.block_calculator.client.pricing.SellableItemKey;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Caches the parsed sellable-items map, re-parsing only when the underlying config list has
 * actually changed since the last read. Cheap to call every frame from the render hook.
 */
public final class SellableItemsCache {
	private static List<String> cachedSourceLines = Collections.emptyList();
	private static Map<SellableItemKey, Double> cached = Collections.emptyMap();

	private SellableItemsCache() {
	}

	public static Map<SellableItemKey, Double> get() {
		List<String> currentLines = AutoConfig.getConfigHolder(ModConfig.class).getConfig().sellableItems;
		if (!currentLines.equals(cachedSourceLines)) {
			cachedSourceLines = List.copyOf(currentLines);
			cached = SellableItemsParser.parse(cachedSourceLines);
		}
		return cached;
	}
}
