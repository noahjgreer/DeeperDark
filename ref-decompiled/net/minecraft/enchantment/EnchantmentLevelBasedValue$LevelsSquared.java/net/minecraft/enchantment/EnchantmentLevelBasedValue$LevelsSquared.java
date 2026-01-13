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
import net.minecraft.util.math.MathHelper;

public record EnchantmentLevelBasedValue.LevelsSquared(float added) implements EnchantmentLevelBasedValue
{
    public static final MapCodec<EnchantmentLevelBasedValue.LevelsSquared> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.FLOAT.fieldOf("added").forGetter(EnchantmentLevelBasedValue.LevelsSquared::added)).apply((Applicative)instance, EnchantmentLevelBasedValue.LevelsSquared::new));

    @Override
    public float getValue(int level) {
        return (float)MathHelper.square(level) + this.added;
    }

    public MapCodec<EnchantmentLevelBasedValue.LevelsSquared> getCodec() {
        return CODEC;
    }
}
