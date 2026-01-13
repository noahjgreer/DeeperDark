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

@Environment(value=EnvType.CLIENT)
static final class GhostRecipe.CyclingItem
extends Record {
    private final List<ItemStack> items;
    final boolean isResultSlot;

    GhostRecipe.CyclingItem(List<ItemStack> items, boolean isResultSlot) {
        this.items = items;
        this.isResultSlot = isResultSlot;
    }

    public ItemStack get(int index) {
        int i = this.items.size();
        if (i == 0) {
            return ItemStack.EMPTY;
        }
        return this.items.get(index % i);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GhostRecipe.CyclingItem.class, "items;isResultSlot", "items", "isResultSlot"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GhostRecipe.CyclingItem.class, "items;isResultSlot", "items", "isResultSlot"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GhostRecipe.CyclingItem.class, "items;isResultSlot", "items", "isResultSlot"}, this, object);
    }

    public List<ItemStack> items() {
        return this.items;
    }

    public boolean isResultSlot() {
        return this.isResultSlot;
    }
}
