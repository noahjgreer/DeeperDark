/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.enchantment;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.util.math.MathHelper;

public record EnchantmentLevelBasedValue.Clamped(EnchantmentLevelBasedValue value, float min, float max) implements EnchantmentLevelBasedValue
{
    public static final MapCodec<EnchantmentLevelBasedValue.Clamped> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)CODEC.fieldOf("value").forGetter(EnchantmentLevelBasedValue.Clamped::value), (App)Codec.FLOAT.fieldOf("min").forGetter(EnchantmentLevelBasedValue.Clamped::min), (App)Codec.FLOAT.fieldOf("max").forGetter(EnchantmentLevelBasedValue.Clamped::max)).apply((Applicative)instance, EnchantmentLevelBasedValue.Clamped::new)).validate(type -> {
        if (type.max <= type.min) {
            return DataResult.error(() -> "Max must be larger than min, min: " + clamped.min + ", max: " + clamped.max);
        }
        return DataResult.success((Object)type);
    });

    @Override
    public float getValue(int level) {
        return MathHelper.clamp(this.value.getValue(level), this.min, this.max);
    }

    public MapCodec<EnchantmentLevelBasedValue.Clamped> getCodec() {
        return CODEC;
    }
}
