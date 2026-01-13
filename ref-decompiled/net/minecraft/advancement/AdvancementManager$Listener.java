/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.advancement;

import net.minecraft.advancement.PlacedAdvancement;

public static interface AdvancementManager.Listener {
    public void onRootAdded(PlacedAdvancement var1);

    public void onRootRemoved(PlacedAdvancement var1);

    public void onDependentAdded(PlacedAdvancement var1);

    public void onDependentRemoved(PlacedAdvancement var1);

    public void onClear();
}
