package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.noahsarch.deeperdark.duck.CraftingPanelHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Routes ghost-recipe display so that 2×2 (and smaller) recipes show ghost items
 * in the vanilla 2×2 inventory crafting grid, while 3×3 recipes show in the 3×3
 * collar crafting panel grid.
 *
 * The CraftingPanelHolder is accessed via Minecraft.player.inventoryMenu rather
 * than @Shadow because the `menu` field is protected final T (type-erased) in the
 * parent RecipeBookComponent and cannot be shadowed from this child class.
 */
@Environment(EnvType.CLIENT)
@Mixin(CraftingRecipeBookComponent.class)
public abstract class CraftingRecipeBookMixin {

    @Inject(method = "fillGhostRecipe", at = @At("HEAD"))
    private void deeperdark$routeGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipe,
                                              ContextMap context, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!(mc.player.inventoryMenu instanceof CraftingPanelHolder holder)
                || !holder.deeperdark$isPanelOpen()) return;

        boolean needs3x3 = switch (recipe) {
            case ShapedCraftingRecipeDisplay shaped      -> shaped.width() > 2 || shaped.height() > 2;
            case ShapelessCraftingRecipeDisplay shapeless -> shapeless.ingredients().size() > 4;
            default                                       -> false;
        };
        holder.deeperdark$setUseVanillaSlots(!needs3x3);
    }

    @Inject(method = "fillGhostRecipe", at = @At("RETURN"))
    private void deeperdark$resetGhostRoute(GhostSlots ghostSlots, RecipeDisplay recipe,
                                             ContextMap context, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.player.inventoryMenu instanceof CraftingPanelHolder holder) {
            holder.deeperdark$setUseVanillaSlots(false);
        }
    }
}
