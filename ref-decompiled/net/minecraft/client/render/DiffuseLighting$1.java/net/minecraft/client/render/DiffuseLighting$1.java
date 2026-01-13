/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.dimension.DimensionType;

@Environment(value=EnvType.CLIENT)
static class DiffuseLighting.1 {
    static final /* synthetic */ int[] field_64417;

    static {
        field_64417 = new int[DimensionType.CardinalLightType.values().length];
        try {
            DiffuseLighting.1.field_64417[DimensionType.CardinalLightType.DEFAULT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DiffuseLighting.1.field_64417[DimensionType.CardinalLightType.NETHER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
