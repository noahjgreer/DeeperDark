/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.enchantment;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;

public record EnchantmentLevelBasedValue.Exponent(EnchantmentLevelBasedValue base, EnchantmentLevelBasedValue power) implements EnchantmentLevelBasedValue
{
    public static final MapCodec<EnchantmentLevelBasedValue.Exponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)CODEC.fieldOf("base").forGetter(EnchantmentLevelBasedValue.Exponent::base), (App)CODEC.fieldOf("power").forGetter(EnchantmentLevelBasedValue.Exponent::power)).apply((Applicative)instance, EnchantmentLevelBasedValue.Exponent::new));

    @Override
    public float getValue(int level) {
        return (float)Math.pow(this.base.getValue(level), this.power.getValue(level));
    }

    public MapCodec<EnchantmentLevelBasedValue.Exponent> getCodec() {
        return CODEC;
    }
}
