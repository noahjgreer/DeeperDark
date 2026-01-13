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
package net.minecraft.enchantment.effect.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

public record SpawnParticlesEnchantmentEffect.PositionSource(SpawnParticlesEnchantmentEffect.PositionSourceType type, float offset, float scale) {
    public static final MapCodec<SpawnParticlesEnchantmentEffect.PositionSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)SpawnParticlesEnchantmentEffect.PositionSourceType.CODEC.fieldOf("type").forGetter(SpawnParticlesEnchantmentEffect.PositionSource::type), (App)Codec.FLOAT.optionalFieldOf("offset", (Object)Float.valueOf(0.0f)).forGetter(SpawnParticlesEnchantmentEffect.PositionSource::offset), (App)Codecs.POSITIVE_FLOAT.optionalFieldOf("scale", (Object)Float.valueOf(1.0f)).forGetter(SpawnParticlesEnchantmentEffect.PositionSource::scale)).apply((Applicative)instance, SpawnParticlesEnchantmentEffect.PositionSource::new)).validate(source -> {
        if (source.type() == SpawnParticlesEnchantmentEffect.PositionSourceType.ENTITY_POSITION && source.scale() != 1.0f) {
            return DataResult.error(() -> "Cannot scale an entity position coordinate source");
        }
        return DataResult.success((Object)source);
    });

    public double getPosition(double entityPosition, double boundingBoxCenter, float boundingBoxSize, Random random) {
        return this.type.getCoordinate(entityPosition, boundingBoxCenter, boundingBoxSize * this.scale, random) + (double)this.offset;
    }
}
