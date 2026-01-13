/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class VertexBufferManager.1 {
    static final /* synthetic */ int[] field_57888;

    static {
        field_57888 = new int[VertexFormatElement.Usage.values().length];
        try {
            VertexBufferManager.1.field_57888[VertexFormatElement.Usage.POSITION.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            VertexBufferManager.1.field_57888[VertexFormatElement.Usage.GENERIC.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            VertexBufferManager.1.field_57888[VertexFormatElement.Usage.UV.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            VertexBufferManager.1.field_57888[VertexFormatElement.Usage.NORMAL.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            VertexBufferManager.1.field_57888[VertexFormatElement.Usage.COLOR.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
