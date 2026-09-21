package tmc.block_calculator.client.pricing;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Totals the sellable items among a set of stacks. Items not present in {@code sellable} are
 * ignored entirely. Display order follows {@code sellable}'s own iteration order (the sellable
 * items config list order), not value or name.
 */
public final class PriceCalculator {
	private PriceCalculator() {
	}

	public record LineItem(Item item, int count, double totalValue) {
	}

	public record PricingResult(List<LineItem> lines, double grandTotal) {
	}

	public static PricingResult calculate(List<ItemStack> stacks, Map<Item, Double> sellable, double multiplier) {
		Map<Item, Integer> counts = new HashMap<>();
		for (ItemStack stack : stacks) {
			if (stack.isEmpty()) {
				continue;
			}
			Item item = stack.getItem();
			if (!sellable.containsKey(item)) {
				continue;
			}
			counts.merge(item, stack.getCount(), Integer::sum);
		}

		List<LineItem> lines = new ArrayList<>();
		double total = 0;
		for (Map.Entry<Item, Double> entry : sellable.entrySet()) {
			int count = counts.getOrDefault(entry.getKey(), 0);
			if (count == 0) {
				continue;
			}
			double value = count * entry.getValue() * multiplier;
			lines.add(new LineItem(entry.getKey(), count, value));
			total += value;
		}
		return new PricingResult(lines, total);
	}
}
