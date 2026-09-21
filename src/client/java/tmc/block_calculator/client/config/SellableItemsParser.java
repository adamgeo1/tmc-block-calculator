package tmc.block_calculator.client.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import tmc.block_calculator.client.TrappedMCBlockCalculatorClient;
import tmc.block_calculator.client.pricing.SellableItemKey;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses {@link ModConfig#sellableItems} lines into a priced-item map. Each line is
 * "namespace:item_id=price", or "namespace:item_id|Custom Name=price" to match only stacks
 * carrying that exact custom name (e.g. a server-side "Purified" variant that reuses a vanilla
 * item id) - a plain "id=price" line matches only stacks with no custom name of their own, so the
 * two never collide. Every failure mode (bad format, unknown item, unparsable price) logs a
 * warning naming the offending line and skips just that line, so a typo in the config can never
 * crash the client.
 */
public final class SellableItemsParser {
	private SellableItemsParser() {
	}

	public static Map<SellableItemKey, Double> parse(List<String> lines) {
		Map<SellableItemKey, Double> result = new LinkedHashMap<>();
		for (String rawLine : lines) {
			String line = rawLine.trim();
			if (line.isEmpty()) {
				continue;
			}

			int priceSplitIndex = line.lastIndexOf('=');
			if (priceSplitIndex < 0) {
				TrappedMCBlockCalculatorClient.LOGGER.warn("Ignoring invalid sellable-item line (missing '='): {}", rawLine);
				continue;
			}
			String idAndName = line.substring(0, priceSplitIndex).trim();
			String pricePart = line.substring(priceSplitIndex + 1).trim();

			int nameSplitIndex = idAndName.indexOf('|');
			String idPart = nameSplitIndex < 0 ? idAndName : idAndName.substring(0, nameSplitIndex).trim();
			String customName = nameSplitIndex < 0 ? "" : idAndName.substring(nameSplitIndex + 1).trim();

			Identifier id = Identifier.tryParse(idPart);
			if (id == null) {
				TrappedMCBlockCalculatorClient.LOGGER.warn("Ignoring sellable-item line with invalid item id: {}", rawLine);
				continue;
			}
			Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
			if (item == null) {
				TrappedMCBlockCalculatorClient.LOGGER.warn("Ignoring sellable-item line with unknown item id: {}", rawLine);
				continue;
			}

			double price;
			try {
				price = Double.parseDouble(pricePart);
			} catch (NumberFormatException e) {
				TrappedMCBlockCalculatorClient.LOGGER.warn("Ignoring sellable-item line with invalid price: {}", rawLine);
				continue;
			}
			if (price <= 0) {
				TrappedMCBlockCalculatorClient.LOGGER.warn("Ignoring sellable-item line with non-positive price: {}", rawLine);
				continue;
			}

			SellableItemKey key = new SellableItemKey(item, customName);
			if (result.put(key, price) != null) {
				TrappedMCBlockCalculatorClient.LOGGER.warn("Duplicate sellable-item entry for {}, using the later value", idAndName);
			}
		}
		return result;
	}
}
