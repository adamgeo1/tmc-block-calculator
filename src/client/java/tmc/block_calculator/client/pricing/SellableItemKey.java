package tmc.block_calculator.client.pricing;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Identifies a priceable item: the underlying registry item, plus an optional exact custom-name
 * match. Some servers reuse a vanilla item (e.g. minecraft:gold_block) with a custom display name
 * and no distinct registry id for special variants (e.g. a "Purified" version) - custom name is
 * the only client-visible signal that distinguishes them. {@code customName} is empty for a plain
 * entry, which matches only stacks with no explicit custom name of their own.
 */
public record SellableItemKey(Item item, String customName) {
	public static SellableItemKey of(ItemStack stack) {
		Component customName = stack.getCustomName();
		return new SellableItemKey(stack.getItem(), customName == null ? "" : customName.getString());
	}
}
