package tmc.block_calculator.client.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import tmc.block_calculator.client.TrappedMCBlockCalculatorClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses the "namespace:item_id=price" lines from {@link ModConfig#sellableItems} into an
 * Item -> price map. Every failure mode (bad format, unknown item, unparsable price) logs a
 * warning naming the offending line and skips just that line, so a typo in the config can never
 * crash the client.
 */
public final class SellableItemsParser {
	private SellableItemsParser() {
	}

	public static Map<Item, Double> parse(List<String> lines) {
		Map<Item, Double> result = new LinkedHashMap<>();
		for (String rawLine : lines) {
			String line = rawLine.trim();
			if (line.isEmpty()) {
				continue;
			}

			int splitIndex = line.lastIndexOf('=');
			if (splitIndex < 0) {
				TrappedMCBlockCalculatorClient.LOGGER.warn("Ignoring invalid sellable-item line (missing '='): {}", rawLine);
				continue;
			}
			String idPart = line.substring(0, splitIndex).trim();
			String pricePart = line.substring(splitIndex + 1).trim();

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

			if (result.put(item, price) != null) {
				TrappedMCBlockCalculatorClient.LOGGER.warn("Duplicate sellable-item entry for {}, using the later value", idPart);
			}
		}
		return result;
	}
}
