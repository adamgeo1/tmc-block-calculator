package tmc.block_calculator.client.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes the vanilla GUI panel's position/size (all protected fields with no public Fabric API
 * accessor) so the sell-value overlay can sit flush next to it. Purely a synthetic getter for an
 * existing field - no injected logic - so it carries none of the fragility of an @Inject mixin.
 */
@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
	@Accessor("leftPos")
	int getLeftPos();

	@Accessor("topPos")
	int getTopPos();

	@Accessor("imageWidth")
	int getImageWidth();

	@Accessor("imageHeight")
	int getImageHeight();
}
