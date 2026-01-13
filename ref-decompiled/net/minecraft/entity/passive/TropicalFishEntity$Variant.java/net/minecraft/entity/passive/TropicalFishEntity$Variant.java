/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.DyeColor;

public record TropicalFishEntity.Variant(TropicalFishEntity.Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
    public static final Codec<TropicalFishEntity.Variant> CODEC = Codec.INT.xmap(TropicalFishEntity.Variant::new, TropicalFishEntity.Variant::getId);

    public TropicalFishEntity.Variant(int id) {
        this(TropicalFishEntity.getVariety(id), TropicalFishEntity.getBaseColor(id), TropicalFishEntity.getPatternColor(id));
    }

    public int getId() {
        return TropicalFishEntity.getVariantId(this.pattern, this.baseColor, this.patternColor);
    }
}
