/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer$Usage
 *  com.mojang.blaze3d.opengl.GlConst
 *  com.mojang.blaze3d.opengl.GlConst$1
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.platform.DestFactor
 *  com.mojang.blaze3d.platform.PolygonMode
 *  com.mojang.blaze3d.platform.SourceFactor
 *  com.mojang.blaze3d.shaders.ShaderType
 *  com.mojang.blaze3d.textures.AddressMode
 *  com.mojang.blaze3d.textures.TextureFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  com.mojang.blaze3d.vertex.VertexFormat$IndexType
 *  com.mojang.blaze3d.vertex.VertexFormatElement$Type
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.texture.NativeImage$Format
 *  net.minecraft.util.annotation.DeobfuscateClass
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlConst;
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
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class GlConst {
    public static final int GL_READ_FRAMEBUFFER = 36008;
    public static final int GL_DRAW_FRAMEBUFFER = 36009;
    public static final int GL_TRUE = 1;
    public static final int GL_FALSE = 0;
    public static final int GL_NONE = 0;
    public static final int GL_LINES = 1;
    public static final int GL_LINE_STRIP = 3;
    public static final int GL_TRIANGLE_STRIP = 5;
    public static final int GL_TRIANGLE_FAN = 6;
    public static final int GL_TRIANGLES = 4;
    public static final int GL_POINTS = 0;
    public static final int GL_WRITE_ONLY = 35001;
    public static final int GL_READ_ONLY = 35000;
    public static final int GL_READ_WRITE = 35002;
    public static final int GL_MAP_READ_BIT = 1;
    public static final int GL_MAP_WRITE_BIT = 2;
    public static final int GL_EQUAL = 514;
    public static final int GL_LEQUAL = 515;
    public static final int GL_LESS = 513;
    public static final int GL_GREATER = 516;
    public static final int GL_GEQUAL = 518;
    public static final int GL_ALWAYS = 519;
    public static final int GL_TEXTURE_MAG_FILTER = 10240;
    public static final int GL_TEXTURE_MIN_FILTER = 10241;
    public static final int GL_TEXTURE_WRAP_S = 10242;
    public static final int GL_TEXTURE_WRAP_T = 10243;
    public static final int GL_NEAREST = 9728;
    public static final int GL_LINEAR = 9729;
    public static final int GL_NEAREST_MIPMAP_LINEAR = 9986;
    public static final int GL_LINEAR_MIPMAP_LINEAR = 9987;
    public static final int GL_CLAMP_TO_EDGE = 33071;
    public static final int GL_REPEAT = 10497;
    public static final int GL_FRONT = 1028;
    public static final int GL_FRONT_AND_BACK = 1032;
    public static final int GL_LINE = 6913;
    public static final int GL_FILL = 6914;
    public static final int GL_BYTE = 5120;
    public static final int GL_UNSIGNED_BYTE = 5121;
    public static final int GL_SHORT = 5122;
    public static final int GL_UNSIGNED_SHORT = 5123;
    public static final int GL_INT = 5124;
    public static final int GL_UNSIGNED_INT = 5125;
    public static final int GL_FLOAT = 5126;
    public static final int GL_ZERO = 0;
    public static final int GL_ONE = 1;
    public static final int GL_SRC_COLOR = 768;
    public static final int GL_ONE_MINUS_SRC_COLOR = 769;
    public static final int GL_SRC_ALPHA = 770;
    public static final int GL_ONE_MINUS_SRC_ALPHA = 771;
    public static final int GL_DST_ALPHA = 772;
    public static final int GL_ONE_MINUS_DST_ALPHA = 773;
    public static final int GL_DST_COLOR = 774;
    public static final int GL_ONE_MINUS_DST_COLOR = 775;
    public static final int GL_REPLACE = 7681;
    public static final int GL_DEPTH_BUFFER_BIT = 256;
    public static final int GL_COLOR_BUFFER_BIT = 16384;
    public static final int GL_RGBA8 = 32856;
    public static final int GL_PROXY_TEXTURE_2D = 32868;
    public static final int GL_RGBA = 6408;
    public static final int GL_TEXTURE_WIDTH = 4096;
    public static final int GL_BGR = 32992;
    public static final int GL_FUNC_ADD = 32774;
    public static final int GL_MIN = 32775;
    public static final int GL_MAX = 32776;
    public static final int GL_FUNC_SUBTRACT = 32778;
    public static final int GL_FUNC_REVERSE_SUBTRACT = 32779;
    public static final int GL_DEPTH_COMPONENT24 = 33190;
    public static final int GL_STATIC_DRAW = 35044;
    public static final int GL_DYNAMIC_DRAW = 35048;
    public static final int GL_STREAM_DRAW = 35040;
    public static final int GL_STATIC_READ = 35045;
    public static final int GL_DYNAMIC_READ = 35049;
    public static final int GL_STREAM_READ = 35041;
    public static final int GL_STATIC_COPY = 35046;
    public static final int GL_DYNAMIC_COPY = 35050;
    public static final int GL_STREAM_COPY = 35042;
    public static final int GL_SYNC_GPU_COMMANDS_COMPLETE = 37143;
    public static final int GL_TIMEOUT_EXPIRED = 37147;
    public static final int GL_WAIT_FAILED = 37149;
    public static final int GL_UNPACK_SWAP_BYTES = 3312;
    public static final int GL_UNPACK_LSB_FIRST = 3313;
    public static final int GL_UNPACK_ROW_LENGTH = 3314;
    public static final int GL_UNPACK_SKIP_ROWS = 3315;
    public static final int GL_UNPACK_SKIP_PIXELS = 3316;
    public static final int GL_UNPACK_ALIGNMENT = 3317;
    public static final int GL_PACK_ALIGNMENT = 3333;
    public static final int GL_PACK_ROW_LENGTH = 3330;
    public static final int GL_MAX_TEXTURE_SIZE = 3379;
    public static final int GL_TEXTURE_2D = 3553;
    public static final int[] CUBEMAP_TARGETS = new int[]{34069, 34070, 34071, 34072, 34073, 34074};
    public static final int GL_DEPTH_COMPONENT = 6402;
    public static final int GL_DEPTH_COMPONENT32 = 33191;
    public static final int GL_FRAMEBUFFER = 36160;
    public static final int GL_RENDERBUFFER = 36161;
    public static final int GL_COLOR_ATTACHMENT0 = 36064;
    public static final int GL_DEPTH_ATTACHMENT = 36096;
    public static final int GL_FRAMEBUFFER_COMPLETE = 36053;
    public static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
    public static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
    public static final int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
    public static final int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
    public static final int GL_FRAMEBUFFER_UNSUPPORTED = 36061;
    public static final int GL_LINK_STATUS = 35714;
    public static final int GL_COMPILE_STATUS = 35713;
    public static final int GL_VERTEX_SHADER = 35633;
    public static final int GL_FRAGMENT_SHADER = 35632;
    public static final int GL_TEXTURE0 = 33984;
    public static final int GL_TEXTURE1 = 33985;
    public static final int GL_TEXTURE2 = 33986;
    public static final int GL_DEPTH_TEXTURE_MODE = 34891;
    public static final int GL_TEXTURE_COMPARE_MODE = 34892;
    public static final int GL_ARRAY_BUFFER = 34962;
    public static final int GL_ELEMENT_ARRAY_BUFFER = 34963;
    public static final int GL_PIXEL_PACK_BUFFER = 35051;
    public static final int GL_COPY_READ_BUFFER = 36662;
    public static final int GL_COPY_WRITE_BUFFER = 36663;
    public static final int GL_PIXEL_UNPACK_BUFFER = 35052;
    public static final int GL_UNIFORM_BUFFER = 35345;
    public static final int GL_ALPHA_BIAS = 3357;
    public static final int GL_RGB = 6407;
    public static final int GL_RG = 33319;
    public static final int GL_R8 = 33321;
    public static final int GL_RED = 6403;
    public static final int GL_OUT_OF_MEMORY = 1285;

    public static int toGl(DepthTestFunction function) {
        return switch (1.field_58121[function.ordinal()]) {
            case 1 -> 519;
            case 2 -> 514;
            case 3 -> 513;
            case 4 -> 516;
            default -> 515;
        };
    }

    public static int toGl(PolygonMode polygonMode) {
        return switch (1.field_58122[polygonMode.ordinal()]) {
            case 1 -> 6913;
            default -> 6914;
        };
    }

    public static int toGl(DestFactor factor) {
        return switch (1.field_58123[factor.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> 32771;
            case 2 -> 32769;
            case 3 -> 772;
            case 4 -> 774;
            case 5 -> 1;
            case 6 -> 32772;
            case 7 -> 32770;
            case 8 -> 773;
            case 9 -> 775;
            case 10 -> 771;
            case 11 -> 769;
            case 12 -> 770;
            case 13 -> 768;
            case 14 -> 0;
        };
    }

    public static int toGl(SourceFactor factor) {
        return switch (1.field_58124[factor.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> 32771;
            case 2 -> 32769;
            case 3 -> 772;
            case 4 -> 774;
            case 5 -> 1;
            case 6 -> 32772;
            case 7 -> 32770;
            case 8 -> 773;
            case 9 -> 775;
            case 10 -> 771;
            case 11 -> 769;
            case 12 -> 770;
            case 13 -> 776;
            case 14 -> 768;
            case 15 -> 0;
        };
    }

    public static int toGl(VertexFormat.DrawMode drawMode) {
        return switch (1.field_58126[drawMode.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> 4;
            case 2 -> 1;
            case 3 -> 3;
            case 4 -> 0;
            case 5 -> 4;
            case 6 -> 5;
            case 7 -> 6;
            case 8 -> 4;
        };
    }

    public static int toGl(VertexFormat.IndexType type) {
        return switch (1.field_58127[type.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> 5123;
            case 2 -> 5125;
        };
    }

    public static int toGl(NativeImage.Format format) {
        return switch (1.field_58128[format.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> 6408;
            case 2 -> 6407;
            case 3 -> 33319;
            case 4 -> 6403;
        };
    }

    public static int toGl(AddressMode addressMode) {
        return switch (1.field_58130[addressMode.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> 10497;
            case 2 -> 33071;
        };
    }

    public static int toGl(VertexFormatElement.Type type) {
        return switch (1.field_58131[type.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> 5126;
            case 2 -> 5121;
            case 3 -> 5120;
            case 4 -> 5123;
            case 5 -> 5122;
            case 6 -> 5125;
            case 7 -> 5124;
        };
    }

    public static int toGlInternalId(TextureFormat format) {
        return switch (1.field_58132[format.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> 32856;
            case 2 -> 33321;
            case 3 -> 33329;
            case 4 -> 33191;
        };
    }

    public static int toGlExternalId(TextureFormat format) {
        return switch (1.field_58132[format.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> 6408;
            case 2 -> 6403;
            case 3 -> 6403;
            case 4 -> 6402;
        };
    }

    public static int toGlType(TextureFormat format) {
        return switch (1.field_58132[format.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> 5121;
            case 2 -> 5121;
            case 3 -> 5121;
            case 4 -> 5126;
        };
    }

    public static int toGl(ShaderType type) {
        return switch (1.field_58133[type.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> 35633;
            case 2 -> 35632;
        };
    }

    public static int bufferUsageToGlFlag(@GpuBuffer.Usage int usage) {
        int i = 0;
        if ((usage & 1) != 0) {
            i |= 0x41;
        }
        if ((usage & 2) != 0) {
            i |= 0x42;
        }
        if ((usage & 8) != 0) {
            i |= 0x100;
        }
        if ((usage & 4) != 0) {
            i |= 0x200;
        }
        return i;
    }

    public static int bufferUsageToGlEnum(@GpuBuffer.Usage int usage) {
        boolean bl;
        boolean bl2 = bl = (usage & 4) != 0;
        if ((usage & 2) != 0) {
            if (bl) {
                return 35040;
            }
            return 35044;
        }
        if ((usage & 1) != 0) {
            if (bl) {
                return 35041;
            }
            return 35045;
        }
        return 35044;
    }
}

