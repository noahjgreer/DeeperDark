/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.random.Random;

public static class AxolotlEntity.AxolotlData
extends PassiveEntity.PassiveData {
    public final AxolotlEntity.Variant[] variants;

    public AxolotlEntity.AxolotlData(AxolotlEntity.Variant ... variants) {
        super(false);
        this.variants = variants;
    }

    public AxolotlEntity.Variant getRandomVariant(Random random) {
        return this.variants[random.nextInt(this.variants.length)];
    }
}
