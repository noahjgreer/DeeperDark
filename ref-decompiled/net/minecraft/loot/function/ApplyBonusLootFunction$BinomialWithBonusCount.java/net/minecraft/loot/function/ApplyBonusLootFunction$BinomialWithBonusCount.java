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
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

record ApplyBonusLootFunction.BinomialWithBonusCount(int extra, float probability) implements ApplyBonusLootFunction.Formula
{
    private static final Codec<ApplyBonusLootFunction.BinomialWithBonusCount> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("extra").forGetter(ApplyBonusLootFunction.BinomialWithBonusCount::extra), (App)Codec.FLOAT.fieldOf("probability").forGetter(ApplyBonusLootFunction.BinomialWithBonusCount::probability)).apply((Applicative)instance, ApplyBonusLootFunction.BinomialWithBonusCount::new));
    public static final ApplyBonusLootFunction.Type TYPE = new ApplyBonusLootFunction.Type(Identifier.ofVanilla("binomial_with_bonus_count"), CODEC);

    @Override
    public int getValue(Random random, int initialCount, int enchantmentLevel) {
        for (int i = 0; i < enchantmentLevel + this.extra; ++i) {
            if (!(random.nextFloat() < this.probability)) continue;
            ++initialCount;
        }
        return initialCount;
    }

    @Override
    public ApplyBonusLootFunction.Type getType() {
        return TYPE;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ApplyBonusLootFunction.BinomialWithBonusCount.class, "extraRounds;probability", "extra", "probability"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ApplyBonusLootFunction.BinomialWithBonusCount.class, "extraRounds;probability", "extra", "probability"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ApplyBonusLootFunction.BinomialWithBonusCount.class, "extraRounds;probability", "extra", "probability"}, this, object);
    }
}
