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
static class Keyboard.1 {
    static final /* synthetic */ int[] field_1685;

    static {
        field_1685 = new int[HitResult.Type.values().length];
        try {
            Keyboard.1.field_1685[HitResult.Type.BLOCK.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Keyboard.1.field_1685[HitResult.Type.ENTITY.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
