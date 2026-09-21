package tmc.block_calculator.client.pricing;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Totals the sellable items among a set of stacks. Items not present in {@code sellable} are
 * ignored entirely. Lines are sorted by item registry id, then by custom name (plain entries -
 * empty custom name - sort first) - this keeps every variant of the same item (e.g. a plain item
 * and its named "Purified" counterpart) adjacent, and gives a stable, predictable order that
 * doesn't depend on how entries happen to be arranged in the sellable-items config list.
 */
public final class PriceCalculator {
	private static final Comparator<SellableItemKey> ORDER = Comparator
			.comparing((SellableItemKey key) -> BuiltInRegistries.ITEM.getKey(key.item()).toString())
			.thenComparing(SellableItemKey::customName);

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

		List<SellableItemKey> orderedKeys = new ArrayList<>(sellable.keySet());
		orderedKeys.sort(ORDER);

		List<LineItem> lines = new ArrayList<>();
		double total = 0;
		for (SellableItemKey key : orderedKeys) {
			int count = counts.getOrDefault(key, 0);
			if (count == 0) {
				continue;
			}
			double value = count * sellable.get(key) * multiplier;
			lines.add(new LineItem(key, count, value));
			total += value;
		}
		return new PricingResult(lines, total);
	}
}
