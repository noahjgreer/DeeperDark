/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.util.DyeColor;
import net.minecraft.util.math.random.Random;

@FunctionalInterface
static interface SheepColors.ColorSelector {
    public DyeColor get(Random var1);
}
