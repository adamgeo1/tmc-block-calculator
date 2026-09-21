package tmc.block_calculator.client.pricing;

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

	public record LineItem(SellableItemKey key, int count, double totalValue) {
	}

	public record PricingResult(List<LineItem> lines, double grandTotal) {
	}

	public static PricingResult calculate(List<ItemStack> stacks, Map<SellableItemKey, Double> sellable, double multiplier) {
		Map<SellableItemKey, Integer> counts = new HashMap<>();
		for (ItemStack stack : stacks) {
			if (stack.isEmpty()) {
				continue;
			}
			SellableItemKey key = SellableItemKey.of(stack);
			if (!sellable.containsKey(key)) {
				continue;
			}
			counts.merge(key, stack.getCount(), Integer::sum);
		}

		List<LineItem> lines = new ArrayList<>();
		double total = 0;
		for (Map.Entry<SellableItemKey, Double> entry : sellable.entrySet()) {
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
