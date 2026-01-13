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
protected static final class RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot
extends Record {
    final int y;
    final int x;
    private final List<ItemStack> stacks;

    public RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot(int y, int y2, List<ItemStack> stacks) {
        if (stacks.isEmpty()) {
            throw new IllegalArgumentException("Ingredient list must be non-empty");
        }
        this.y = y;
        this.x = y2;
        this.stacks = stacks;
    }

    public ItemStack get(int index) {
        return this.stacks.get(index % this.stacks.size());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot.class, "x;y;ingredients", "y", "x", "stacks"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot.class, "x;y;ingredients", "y", "x", "stacks"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot.class, "x;y;ingredients", "y", "x", "stacks"}, this, object);
    }

    public int y() {
        return this.y;
    }

    public int x() {
        return this.x;
    }

    public List<ItemStack> stacks() {
        return this.stacks;
    }
}
