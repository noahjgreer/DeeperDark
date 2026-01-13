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

public record EnchantmentLevelBasedValue.Fraction(EnchantmentLevelBasedValue numerator, EnchantmentLevelBasedValue denominator) implements EnchantmentLevelBasedValue
{
    public static final MapCodec<EnchantmentLevelBasedValue.Fraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)CODEC.fieldOf("numerator").forGetter(EnchantmentLevelBasedValue.Fraction::numerator), (App)CODEC.fieldOf("denominator").forGetter(EnchantmentLevelBasedValue.Fraction::denominator)).apply((Applicative)instance, EnchantmentLevelBasedValue.Fraction::new));

    @Override
    public float getValue(int level) {
        float f = this.denominator.getValue(level);
        if (f == 0.0f) {
            return 0.0f;
        }
        return this.numerator.getValue(level) / f;
    }

    public MapCodec<EnchantmentLevelBasedValue.Fraction> getCodec() {
        return CODEC;
    }
}
