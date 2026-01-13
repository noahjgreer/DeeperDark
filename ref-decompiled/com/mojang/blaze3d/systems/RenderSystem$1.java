/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class RenderSystem.1 {
    static final /* synthetic */ int[] field_38976;
    static final /* synthetic */ int[] field_27331;

    static {
        field_27331 = new int[VertexFormat.IndexType.values().length];
        try {
            RenderSystem.1.field_27331[VertexFormat.IndexType.SHORT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RenderSystem.1.field_27331[VertexFormat.IndexType.INT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_38976 = new int[VertexFormat.DrawMode.values().length];
        try {
            RenderSystem.1.field_38976[VertexFormat.DrawMode.QUADS.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RenderSystem.1.field_38976[VertexFormat.DrawMode.LINES.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
