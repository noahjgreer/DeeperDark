/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.function;

import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.util.math.random.Random;

static interface ApplyBonusLootFunction.Formula {
    public int getValue(Random var1, int var2, int var3);

    public ApplyBonusLootFunction.Type getType();
}
