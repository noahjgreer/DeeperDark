/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;

@Environment(value=EnvType.CLIENT)
static class GlConst.1 {
    static final /* synthetic */ int[] field_58121;
    static final /* synthetic */ int[] field_58122;
    static final /* synthetic */ int[] field_58123;
    static final /* synthetic */ int[] field_58124;
    static final /* synthetic */ int[] field_58126;
    static final /* synthetic */ int[] field_58127;
    static final /* synthetic */ int[] field_58128;
    static final /* synthetic */ int[] field_58130;
    static final /* synthetic */ int[] field_58131;
    static final /* synthetic */ int[] field_58132;
    static final /* synthetic */ int[] field_58133;

    static {
        field_58133 = new int[ShaderType.values().length];
        try {
            GlConst.1.field_58133[ShaderType.VERTEX.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58133[ShaderType.FRAGMENT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_58132 = new int[TextureFormat.values().length];
        try {
            GlConst.1.field_58132[TextureFormat.RGBA8.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58132[TextureFormat.RED8.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58132[TextureFormat.RED8I.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58132[TextureFormat.DEPTH32.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_58131 = new int[VertexFormatElement.Type.values().length];
        try {
            GlConst.1.field_58131[VertexFormatElement.Type.FLOAT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58131[VertexFormatElement.Type.UBYTE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58131[VertexFormatElement.Type.BYTE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58131[VertexFormatElement.Type.USHORT.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58131[VertexFormatElement.Type.SHORT.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58131[VertexFormatElement.Type.UINT.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58131[VertexFormatElement.Type.INT.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_58130 = new int[AddressMode.values().length];
        try {
            GlConst.1.field_58130[AddressMode.REPEAT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58130[AddressMode.CLAMP_TO_EDGE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_58128 = new int[NativeImage.Format.values().length];
        try {
            GlConst.1.field_58128[NativeImage.Format.RGBA.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58128[NativeImage.Format.RGB.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58128[NativeImage.Format.LUMINANCE_ALPHA.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58128[NativeImage.Format.LUMINANCE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_58127 = new int[VertexFormat.IndexType.values().length];
        try {
            GlConst.1.field_58127[VertexFormat.IndexType.SHORT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58127[VertexFormat.IndexType.INT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_58126 = new int[VertexFormat.DrawMode.values().length];
        try {
            GlConst.1.field_58126[VertexFormat.DrawMode.LINES.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58126[VertexFormat.DrawMode.DEBUG_LINES.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58126[VertexFormat.DrawMode.DEBUG_LINE_STRIP.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58126[VertexFormat.DrawMode.POINTS.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58126[VertexFormat.DrawMode.TRIANGLES.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58126[VertexFormat.DrawMode.TRIANGLE_STRIP.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58126[VertexFormat.DrawMode.TRIANGLE_FAN.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58126[VertexFormat.DrawMode.QUADS.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_58124 = new int[SourceFactor.values().length];
        try {
            GlConst.1.field_58124[SourceFactor.CONSTANT_ALPHA.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.CONSTANT_COLOR.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.DST_ALPHA.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.DST_COLOR.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.ONE.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.ONE_MINUS_CONSTANT_ALPHA.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.ONE_MINUS_CONSTANT_COLOR.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.ONE_MINUS_DST_ALPHA.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.ONE_MINUS_DST_COLOR.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.ONE_MINUS_SRC_ALPHA.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.ONE_MINUS_SRC_COLOR.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.SRC_ALPHA.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.SRC_ALPHA_SATURATE.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.SRC_COLOR.ordinal()] = 14;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58124[SourceFactor.ZERO.ordinal()] = 15;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_58123 = new int[DestFactor.values().length];
        try {
            GlConst.1.field_58123[DestFactor.CONSTANT_ALPHA.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.CONSTANT_COLOR.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.DST_ALPHA.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.DST_COLOR.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.ONE.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.ONE_MINUS_CONSTANT_ALPHA.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.ONE_MINUS_CONSTANT_COLOR.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.ONE_MINUS_DST_ALPHA.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.ONE_MINUS_DST_COLOR.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.ONE_MINUS_SRC_ALPHA.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.ONE_MINUS_SRC_COLOR.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.SRC_ALPHA.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.SRC_COLOR.ordinal()] = 13;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58123[DestFactor.ZERO.ordinal()] = 14;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_58122 = new int[PolygonMode.values().length];
        try {
            GlConst.1.field_58122[PolygonMode.WIREFRAME.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_58121 = new int[DepthTestFunction.values().length];
        try {
            GlConst.1.field_58121[DepthTestFunction.NO_DEPTH_TEST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58121[DepthTestFunction.EQUAL_DEPTH_TEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58121[DepthTestFunction.LESS_DEPTH_TEST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GlConst.1.field_58121[DepthTestFunction.GREATER_DEPTH_TEST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
