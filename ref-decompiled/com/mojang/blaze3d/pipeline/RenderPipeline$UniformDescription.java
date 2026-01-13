/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.textures.TextureFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public record RenderPipeline.UniformDescription(String name, UniformType type, @Nullable TextureFormat textureFormat) {
    public RenderPipeline.UniformDescription(String name, UniformType type) {
        this(name, type, null);
        if (type == UniformType.TEXEL_BUFFER) {
            throw new IllegalArgumentException("Texel buffer needs a texture format");
        }
    }

    public RenderPipeline.UniformDescription(String name, TextureFormat format) {
        this(name, UniformType.TEXEL_BUFFER, format);
    }
}
