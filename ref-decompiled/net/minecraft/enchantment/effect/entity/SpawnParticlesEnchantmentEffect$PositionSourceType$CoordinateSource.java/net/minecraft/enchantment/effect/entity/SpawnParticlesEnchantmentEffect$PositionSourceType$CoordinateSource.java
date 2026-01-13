/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.enchantment.effect.entity;

import net.minecraft.util.math.random.Random;

@FunctionalInterface
static interface SpawnParticlesEnchantmentEffect.PositionSourceType.CoordinateSource {
    public double getCoordinate(double var1, double var3, float var5, Random var6);
}
