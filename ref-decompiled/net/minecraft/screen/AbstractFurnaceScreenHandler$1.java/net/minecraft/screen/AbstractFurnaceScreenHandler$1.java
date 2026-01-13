/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

class AbstractFurnaceScreenHandler.1
implements InputSlotFiller.Handler<AbstractCookingRecipe> {
    final /* synthetic */ List field_52564;
    final /* synthetic */ ServerWorld field_54594;

    AbstractFurnaceScreenHandler.1(List list, ServerWorld serverWorld) {
        this.field_52564 = list;
        this.field_54594 = serverWorld;
    }

    @Override
    public void populateRecipeFinder(RecipeFinder finder) {
        AbstractFurnaceScreenHandler.this.populateRecipeFinder(finder);
    }

    @Override
    public void clear() {
        this.field_52564.forEach(slot -> slot.setStackNoCallbacks(ItemStack.EMPTY));
    }

    @Override
    public boolean matches(RecipeEntry<AbstractCookingRecipe> entry) {
        return entry.value().matches(new SingleStackRecipeInput(AbstractFurnaceScreenHandler.this.inventory.getStack(0)), (World)this.field_54594);
    }
}
