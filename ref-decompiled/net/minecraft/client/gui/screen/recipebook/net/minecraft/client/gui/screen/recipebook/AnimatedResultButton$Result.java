/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;

@Environment(value=EnvType.CLIENT)
static final class AnimatedResultButton.Result
extends Record {
    final NetworkRecipeId id;
    private final List<ItemStack> displayItems;

    AnimatedResultButton.Result(NetworkRecipeId id, List<ItemStack> displayItems) {
        this.id = id;
        this.displayItems = displayItems;
    }

    public ItemStack getDisplayStack(int currentIndex) {
        if (this.displayItems.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int i = currentIndex % this.displayItems.size();
        return this.displayItems.get(i);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{AnimatedResultButton.Result.class, "id;displayItems", "id", "displayItems"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AnimatedResultButton.Result.class, "id;displayItems", "id", "displayItems"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AnimatedResultButton.Result.class, "id;displayItems", "id", "displayItems"}, this, object);
    }

    public NetworkRecipeId id() {
        return this.id;
    }

    public List<ItemStack> displayItems() {
        return this.displayItems;
    }
}
