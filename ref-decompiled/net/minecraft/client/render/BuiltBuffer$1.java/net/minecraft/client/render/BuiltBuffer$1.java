/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class BuiltBuffer.1 {
    static final /* synthetic */ int[] field_27353;

    static {
        field_27353 = new int[VertexFormat.IndexType.values().length];
        try {
            BuiltBuffer.1.field_27353[VertexFormat.IndexType.SHORT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BuiltBuffer.1.field_27353[VertexFormat.IndexType.INT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
