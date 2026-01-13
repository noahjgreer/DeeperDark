/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.UniformType;

@Environment(value=EnvType.CLIENT)
static class ShaderProgram.1 {
    static final /* synthetic */ int[] field_60020;

    static {
        field_60020 = new int[UniformType.values().length];
        try {
            ShaderProgram.1.field_60020[UniformType.UNIFORM_BUFFER.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ShaderProgram.1.field_60020[UniformType.TEXEL_BUFFER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
