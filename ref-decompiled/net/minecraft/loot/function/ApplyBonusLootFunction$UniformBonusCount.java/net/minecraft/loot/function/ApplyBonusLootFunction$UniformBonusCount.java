/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.loot.function;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

record ApplyBonusLootFunction.UniformBonusCount(int bonusMultiplier) implements ApplyBonusLootFunction.Formula
{
    public static final Codec<ApplyBonusLootFunction.UniformBonusCount> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("bonusMultiplier").forGetter(ApplyBonusLootFunction.UniformBonusCount::bonusMultiplier)).apply((Applicative)instance, ApplyBonusLootFunction.UniformBonusCount::new));
    public static final ApplyBonusLootFunction.Type TYPE = new ApplyBonusLootFunction.Type(Identifier.ofVanilla("uniform_bonus_count"), CODEC);

    @Override
    public int getValue(Random random, int initialCount, int enchantmentLevel) {
        return initialCount + random.nextInt(this.bonusMultiplier * enchantmentLevel + 1);
    }

    @Override
    public ApplyBonusLootFunction.Type getType() {
        return TYPE;
    }
}
