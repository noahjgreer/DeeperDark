/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.display;

import java.util.List;
import net.minecraft.recipe.display.DisplayedItemFactory;

public static interface DisplayedItemFactory.FromRemainder<T>
extends DisplayedItemFactory<T> {
    public T toDisplayed(T var1, List<T> var2);
}
