/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.enchantment;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;

public record EnchantmentLevelBasedValue.Linear(float base, float perLevelAboveFirst) implements EnchantmentLevelBasedValue
{
    public static final MapCodec<EnchantmentLevelBasedValue.Linear> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.FLOAT.fieldOf("base").forGetter(EnchantmentLevelBasedValue.Linear::base), (App)Codec.FLOAT.fieldOf("per_level_above_first").forGetter(EnchantmentLevelBasedValue.Linear::perLevelAboveFirst)).apply((Applicative)instance, EnchantmentLevelBasedValue.Linear::new));

    @Override
    public float getValue(int level) {
        return this.base + this.perLevelAboveFirst * (float)(level - 1);
    }

    public MapCodec<EnchantmentLevelBasedValue.Linear> getCodec() {
        return CODEC;
    }
}
