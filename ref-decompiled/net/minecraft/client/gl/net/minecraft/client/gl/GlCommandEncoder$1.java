/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.LogicOp;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class GlCommandEncoder.1 {
    static final /* synthetic */ int[] field_57850;

    static {
        field_57850 = new int[LogicOp.values().length];
        try {
            GlCommandEncoder.1.field_57850[LogicOp.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlCommandEncoder.1.field_57850[LogicOp.OR_REVERSE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
