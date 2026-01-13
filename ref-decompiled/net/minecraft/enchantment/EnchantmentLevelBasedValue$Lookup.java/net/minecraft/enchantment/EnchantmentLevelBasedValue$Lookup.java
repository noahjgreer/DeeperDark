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
import java.util.List;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;

public record EnchantmentLevelBasedValue.Lookup(List<Float> values, EnchantmentLevelBasedValue fallback) implements EnchantmentLevelBasedValue
{
    public static final MapCodec<EnchantmentLevelBasedValue.Lookup> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.FLOAT.listOf().fieldOf("values").forGetter(EnchantmentLevelBasedValue.Lookup::values), (App)CODEC.fieldOf("fallback").forGetter(EnchantmentLevelBasedValue.Lookup::fallback)).apply((Applicative)instance, EnchantmentLevelBasedValue.Lookup::new));

    @Override
    public float getValue(int level) {
        return level <= this.values.size() ? this.values.get(level - 1).floatValue() : this.fallback.getValue(level);
    }

    public MapCodec<EnchantmentLevelBasedValue.Lookup> getCodec() {
        return CODEC;
    }
}
