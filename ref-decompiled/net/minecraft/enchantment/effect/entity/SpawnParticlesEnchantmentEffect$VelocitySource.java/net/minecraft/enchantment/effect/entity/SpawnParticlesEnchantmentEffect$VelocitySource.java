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
package net.minecraft.enchantment.effect.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.random.Random;

public record SpawnParticlesEnchantmentEffect.VelocitySource(float movementScale, FloatProvider base) {
    public static final MapCodec<SpawnParticlesEnchantmentEffect.VelocitySource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.FLOAT.optionalFieldOf("movement_scale", (Object)Float.valueOf(0.0f)).forGetter(SpawnParticlesEnchantmentEffect.VelocitySource::movementScale), (App)FloatProvider.VALUE_CODEC.optionalFieldOf("base", (Object)ConstantFloatProvider.ZERO).forGetter(SpawnParticlesEnchantmentEffect.VelocitySource::base)).apply((Applicative)instance, SpawnParticlesEnchantmentEffect.VelocitySource::new));

    public double getVelocity(double entityVelocity, Random random) {
        return entityVelocity * (double)this.movementScale + (double)this.base.get(random);
    }
}
