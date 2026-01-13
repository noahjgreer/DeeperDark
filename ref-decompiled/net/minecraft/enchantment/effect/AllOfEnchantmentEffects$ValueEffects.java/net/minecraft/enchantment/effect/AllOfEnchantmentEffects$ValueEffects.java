/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.enchantment.effect;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.enchantment.effect.AllOfEnchantmentEffects;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.util.math.random.Random;

public record AllOfEnchantmentEffects.ValueEffects(List<EnchantmentValueEffect> effects) implements EnchantmentValueEffect
{
    public static final MapCodec<AllOfEnchantmentEffects.ValueEffects> CODEC = AllOfEnchantmentEffects.buildCodec(EnchantmentValueEffect.CODEC, AllOfEnchantmentEffects.ValueEffects::new, AllOfEnchantmentEffects.ValueEffects::effects);

    @Override
    public float apply(int level, Random random, float inputValue) {
        for (EnchantmentValueEffect enchantmentValueEffect : this.effects) {
            inputValue = enchantmentValueEffect.apply(level, random, inputValue);
        }
        return inputValue;
    }

    public MapCodec<AllOfEnchantmentEffects.ValueEffects> getCodec() {
        return CODEC;
    }
}
