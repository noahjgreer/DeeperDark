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

public record EnchantmentLevelBasedValue.Constant(float value) implements EnchantmentLevelBasedValue
{
    public static final Codec<EnchantmentLevelBasedValue.Constant> CODEC = Codec.FLOAT.xmap(EnchantmentLevelBasedValue.Constant::new, EnchantmentLevelBasedValue.Constant::value);
    public static final MapCodec<EnchantmentLevelBasedValue.Constant> TYPE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.FLOAT.fieldOf("value").forGetter(EnchantmentLevelBasedValue.Constant::value)).apply((Applicative)instance, EnchantmentLevelBasedValue.Constant::new));

    @Override
    public float getValue(int level) {
        return this.value;
    }

    public MapCodec<EnchantmentLevelBasedValue.Constant> getCodec() {
        return TYPE_CODEC;
    }
}
