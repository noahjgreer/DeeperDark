package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.noahsarch.deeperdark.duck.CraftingPanelHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents the "clicked outside" drop behaviour when the player clicks inside
 * the collar crafting panel area (x ≥ leftPos + imageWidth).
 *
 * hasClickedOutside is overridden in AbstractRecipeBookScreen (not in InventoryScreen),
 * so this is the correct class to target.  The instanceof CraftingPanelHolder guard
 * ensures it only activates for InventoryMenu; all other recipe-book screens
 * (CraftingScreen, FurnaceScreen, etc.) are unaffected.
 */
@Environment(EnvType.CLIENT)
@Mixin(AbstractRecipeBookScreen.class)
public abstract class AbstractRecipeBookScreenMixin<T extends RecipeBookMenu>
        extends AbstractContainerScreen<T> {

    protected AbstractRecipeBookScreenMixin(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Inject(method = "hasClickedOutside", at = @At("RETURN"), cancellable = true)
    private void deeperdark$allowPanelArea(double mx, double my, int xo, int yo,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return; // already inside → nothing to do
        if (!(this.menu instanceof CraftingPanelHolder holder) || !holder.deeperdark$isPanelOpen()) return;

        // Panel occupies [xo + imageWidth, xo + imageWidth + 68) × [yo, yo + 130)
        if (mx >= xo + this.imageWidth && mx < xo + this.imageWidth + 68
                && my >= yo && my < yo + 130) {
            cir.setReturnValue(false);
        }
    }
}
