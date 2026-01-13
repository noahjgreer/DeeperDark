/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.display;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;

public static final class CuttingRecipeDisplay.GroupEntry<T extends Recipe<?>>
extends Record {
    final Ingredient input;
    private final CuttingRecipeDisplay<T> recipe;

    public CuttingRecipeDisplay.GroupEntry(Ingredient input, CuttingRecipeDisplay<T> recipe) {
        this.input = input;
        this.recipe = recipe;
    }

    public static <T extends Recipe<?>> PacketCodec<RegistryByteBuf, CuttingRecipeDisplay.GroupEntry<T>> codec() {
        return PacketCodec.tuple(Ingredient.PACKET_CODEC, CuttingRecipeDisplay.GroupEntry::input, CuttingRecipeDisplay.codec(), CuttingRecipeDisplay.GroupEntry::recipe, CuttingRecipeDisplay.GroupEntry::new);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CuttingRecipeDisplay.GroupEntry.class, "input;recipe", "input", "recipe"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CuttingRecipeDisplay.GroupEntry.class, "input;recipe", "input", "recipe"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CuttingRecipeDisplay.GroupEntry.class, "input;recipe", "input", "recipe"}, this, object);
    }

    public Ingredient input() {
        return this.input;
    }

    public CuttingRecipeDisplay<T> recipe() {
        return this.recipe;
    }
}
