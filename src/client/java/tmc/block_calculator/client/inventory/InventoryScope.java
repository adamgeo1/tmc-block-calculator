package tmc.block_calculator.client.inventory;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Decides which item stacks should be priced for a given open screen: the player's own inventory
 * screen prices exactly main inventory + hotbar (no armor/offhand/crafting grid/output); any other
 * container screen prices only its own slots, generically identified as the slots not backed by
 * the player's inventory container (works for chests, barrels, shulkers, furnaces, hoppers, etc.
 * with no per-screen special-casing).
 */
public final class InventoryScope {
	private InventoryScope() {
	}

	public static List<ItemStack> collectStacks(AbstractContainerScreen<?> screen, Player player) {
		if (screen instanceof InventoryScreen) {
			return player.getInventory().getNonEquipmentItems();
		}

		Inventory playerInventory = player.getInventory();
		List<ItemStack> stacks = new ArrayList<>();
		for (Slot slot : screen.getMenu().slots) {
			if (slot.container != playerInventory) {
				stacks.add(slot.getItem());
			}
		}
		return stacks;
	}
}
