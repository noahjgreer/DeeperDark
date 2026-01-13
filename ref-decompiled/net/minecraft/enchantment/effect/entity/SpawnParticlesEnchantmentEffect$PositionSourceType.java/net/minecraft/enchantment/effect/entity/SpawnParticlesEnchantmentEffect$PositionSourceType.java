/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;

public static final class SpawnParticlesEnchantmentEffect.PositionSourceType
extends Enum<SpawnParticlesEnchantmentEffect.PositionSourceType>
implements StringIdentifiable {
    public static final /* enum */ SpawnParticlesEnchantmentEffect.PositionSourceType ENTITY_POSITION = new SpawnParticlesEnchantmentEffect.PositionSourceType("entity_position", (entityPosition, boundingBoxCenter, boundingBoxSize, random) -> entityPosition);
    public static final /* enum */ SpawnParticlesEnchantmentEffect.PositionSourceType BOUNDING_BOX = new SpawnParticlesEnchantmentEffect.PositionSourceType("in_bounding_box", (entityPosition, boundingBoxCenter, boundingBoxSize, random) -> boundingBoxCenter + (random.nextDouble() - 0.5) * (double)boundingBoxSize);
    public static final Codec<SpawnParticlesEnchantmentEffect.PositionSourceType> CODEC;
    private final String id;
    private final CoordinateSource coordinateSource;
    private static final /* synthetic */ SpawnParticlesEnchantmentEffect.PositionSourceType[] field_51728;

    public static SpawnParticlesEnchantmentEffect.PositionSourceType[] values() {
        return (SpawnParticlesEnchantmentEffect.PositionSourceType[])field_51728.clone();
    }

    public static SpawnParticlesEnchantmentEffect.PositionSourceType valueOf(String string) {
        return Enum.valueOf(SpawnParticlesEnchantmentEffect.PositionSourceType.class, string);
    }

    private SpawnParticlesEnchantmentEffect.PositionSourceType(String id, CoordinateSource coordinateSource) {
        this.id = id;
        this.coordinateSource = coordinateSource;
    }

    public double getCoordinate(double entityPosition, double boundingBoxCenter, float boundingBoxSize, Random random) {
        return this.coordinateSource.getCoordinate(entityPosition, boundingBoxCenter, boundingBoxSize, random);
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ SpawnParticlesEnchantmentEffect.PositionSourceType[] method_60258() {
        return new SpawnParticlesEnchantmentEffect.PositionSourceType[]{ENTITY_POSITION, BOUNDING_BOX};
    }

    static {
        field_51728 = SpawnParticlesEnchantmentEffect.PositionSourceType.method_60258();
        CODEC = StringIdentifiable.createCodec(SpawnParticlesEnchantmentEffect.PositionSourceType::values);
    }

    @FunctionalInterface
    static interface CoordinateSource {
        public double getCoordinate(double var1, double var3, float var5, Random var6);
    }
}
