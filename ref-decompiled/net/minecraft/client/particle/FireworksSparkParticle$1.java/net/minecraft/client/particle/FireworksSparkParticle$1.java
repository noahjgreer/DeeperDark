/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.type.FireworkExplosionComponent;

@Environment(value=EnvType.CLIENT)
static class FireworksSparkParticle.1 {
    static final /* synthetic */ int[] field_3797;

    static {
        field_3797 = new int[FireworkExplosionComponent.Type.values().length];
        try {
            FireworksSparkParticle.1.field_3797[FireworkExplosionComponent.Type.SMALL_BALL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FireworksSparkParticle.1.field_3797[FireworkExplosionComponent.Type.LARGE_BALL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FireworksSparkParticle.1.field_3797[FireworkExplosionComponent.Type.STAR.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FireworksSparkParticle.1.field_3797[FireworkExplosionComponent.Type.CREEPER.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FireworksSparkParticle.1.field_3797[FireworkExplosionComponent.Type.BURST.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
