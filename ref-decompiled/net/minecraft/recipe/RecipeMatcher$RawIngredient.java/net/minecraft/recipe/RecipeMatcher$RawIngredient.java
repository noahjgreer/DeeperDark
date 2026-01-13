/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

@FunctionalInterface
public static interface RecipeMatcher.RawIngredient<T> {
    public boolean acceptsItem(T var1);
}
