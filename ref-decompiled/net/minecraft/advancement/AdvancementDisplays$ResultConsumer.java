/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.advancement;

import net.minecraft.advancement.PlacedAdvancement;

@FunctionalInterface
public static interface AdvancementDisplays.ResultConsumer {
    public void accept(PlacedAdvancement var1, boolean var2);
}
