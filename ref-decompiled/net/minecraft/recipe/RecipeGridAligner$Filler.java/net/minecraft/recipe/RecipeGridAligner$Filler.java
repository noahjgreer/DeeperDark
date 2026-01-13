/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

@FunctionalInterface
public static interface RecipeGridAligner.Filler<T> {
    public void addItemToSlot(T var1, int var2, int var3, int var4);
}
