/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.world.gen.structure.OceanRuinStructure;

static class OceanRuinGenerator.1 {
    static final /* synthetic */ int[] field_31619;

    static {
        field_31619 = new int[OceanRuinStructure.BiomeTemperature.values().length];
        try {
            OceanRuinGenerator.1.field_31619[OceanRuinStructure.BiomeTemperature.WARM.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            OceanRuinGenerator.1.field_31619[OceanRuinStructure.BiomeTemperature.COLD.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
