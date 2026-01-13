/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.hit.HitResult;

@Environment(value=EnvType.CLIENT)
static class MinecraftClient.2 {
    static final /* synthetic */ int[] field_1778;

    static {
        field_1778 = new int[HitResult.Type.values().length];
        try {
            MinecraftClient.2.field_1778[HitResult.Type.ENTITY.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MinecraftClient.2.field_1778[HitResult.Type.BLOCK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MinecraftClient.2.field_1778[HitResult.Type.MISS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
