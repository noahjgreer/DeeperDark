package net.noahsarch.deeperdark.mixin;

import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.noahsarch.deeperdark.duck.CraftingPanelHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Targets AbstractCraftingMenu (parent of both InventoryMenu and CraftingMenu) so we can:
 *  - Override getGridWidth/Height for the panel-open state (both are declared here, not in InventoryMenu)
 *  - Override handlePlacement so 3×3 recipes route to the panel grid instead of the 2×2 inventory grid
 *
 * All injections guard on `this instanceof CraftingPanelHolder && isPanelOpen()` so CraftingMenu
 * (which also extends AbstractCraftingMenu) is completely unaffected.
 */
@Mixin(AbstractCraftingMenu.class)
public abstract class AbstractCraftingMenuMixin {

    @Shadow protected abstract void beginPlacingRecipe();
    @Shadow protected abstract void finishPlacingRecipe(ServerLevel level, RecipeHolder<CraftingRecipe> recipe);
    @Shadow public abstract List<Slot> getInputGridSlots();
    @Shadow protected abstract Player owner();

    // ── Recipe-book grid-size ─────────────────────────────────────────────────

    @Inject(method = "getGridWidth", at = @At("HEAD"), cancellable = true)
    private void deeperdark$overrideGridWidth(CallbackInfoReturnable<Integer> cir) {
        if (this instanceof CraftingPanelHolder holder
                && holder.deeperdark$isPanelOpen()
                && !holder.deeperdark$isUseVanillaSlots()) {
            cir.setReturnValue(3);
        }
    }

    @Inject(method = "getGridHeight", at = @At("HEAD"), cancellable = true)
    private void deeperdark$overrideGridHeight(CallbackInfoReturnable<Integer> cir) {
        if (this instanceof CraftingPanelHolder holder
                && holder.deeperdark$isPanelOpen()
                && !holder.deeperdark$isUseVanillaSlots()) {
            cir.setReturnValue(3);
        }
    }

    // ── Recipe placement routing ──────────────────────────────────────────────
    // When the panel is open and the selected recipe requires a 3×3 grid, route
    // placement to the panel slots instead of the vanilla 2×2 inventory grid.
    // For 2×2 (and smaller) recipes, fall through to vanilla (which uses the 2×2 slots).

    @Inject(method = "handlePlacement", at = @At("HEAD"), cancellable = true)
    private void deeperdark$override3x3Placement(
        boolean useMaxItems, boolean allowDroppingItemsToClear,
        RecipeHolder<?> recipe, ServerLevel level, Inventory inventory,
        CallbackInfoReturnable<RecipeBookMenu.PostPlaceAction> cir
    ) {
        if (!(this instanceof CraftingPanelHolder holder) || !holder.deeperdark$isPanelOpen()) return;
        if (!deeperdark$needs3x3Grid(recipe)) return;

        CraftingContainer craftSlots  = holder.deeperdark$getExtraCraftSlots();
        ResultContainer   resultSlots = holder.deeperdark$getExtraResultSlots();
        List<Slot> inputSlots = this.getInputGridSlots(); // returns 3×3 when panel open + not forceVanilla

        @SuppressWarnings("unchecked")
        RecipeHolder<CraftingRecipe> typedRecipe = (RecipeHolder<CraftingRecipe>) recipe;

        this.beginPlacingRecipe();
        RecipeBookMenu.PostPlaceAction result;
        try {
            result = ServerPlaceRecipe.placeRecipe(
                new ServerPlaceRecipe.CraftingMenuAccess<CraftingRecipe>() {
                    @Override
                    public void fillCraftSlotsStackedContents(StackedItemContents stackedContents) {
                        craftSlots.fillStackedContents(stackedContents);
                    }
                    @Override
                    public void clearCraftingContent() {
                        resultSlots.clearContent();
                        craftSlots.clearContent();
                    }
                    @Override
                    public boolean recipeMatches(RecipeHolder<CraftingRecipe> r) {
                        return r.value().matches(craftSlots.asCraftInput(), owner().level());
                    }
                },
                3, 3, inputSlots, inputSlots, inventory, typedRecipe, useMaxItems, allowDroppingItemsToClear
            );
        } finally {
            this.finishPlacingRecipe(level, typedRecipe);
        }
        cir.setReturnValue(result);
    }

    @Unique
    private static boolean deeperdark$needs3x3Grid(RecipeHolder<?> holder) {
        return switch (holder.value()) {
            case ShapedRecipe shaped -> shaped.getWidth() > 2 || shaped.getHeight() > 2;
            case ShapelessRecipe shpl -> {
                // ingredients is private; derive count from the recipe's display
                for (RecipeDisplay display : shpl.display()) {
                    if (display instanceof ShapelessCraftingRecipeDisplay scd) {
                        yield scd.ingredients().size() > 4;
                    }
                }
                yield false;
            }
            default -> false;
        };
    }
}
