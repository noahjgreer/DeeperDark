/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

record ApplyBonusLootFunction.OreDrops() implements ApplyBonusLootFunction.Formula
{
    public static final ApplyBonusLootFunction.OreDrops INSTANCE = new ApplyBonusLootFunction.OreDrops();
    public static final Codec<ApplyBonusLootFunction.OreDrops> CODEC = MapCodec.unitCodec((Object)INSTANCE);
    public static final ApplyBonusLootFunction.Type TYPE = new ApplyBonusLootFunction.Type(Identifier.ofVanilla("ore_drops"), CODEC);

    @Override
    public int getValue(Random random, int initialCount, int enchantmentLevel) {
        if (enchantmentLevel > 0) {
            int i = random.nextInt(enchantmentLevel + 2) - 1;
            if (i < 0) {
                i = 0;
            }
            return initialCount * (i + 1);
        }
        return initialCount;
    }

    @Override
    public ApplyBonusLootFunction.Type getType() {
        return TYPE;
    }
}
