/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.TextureFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlUniform;

@Environment(value=EnvType.CLIENT)
public record GlUniform.TexelBuffer(int location, int samplerIndex, TextureFormat format, int texture) implements GlUniform
{
    public GlUniform.TexelBuffer(int location, int samplerIndex, TextureFormat format) {
        this(location, samplerIndex, format, GlStateManager._genTexture());
    }

    @Override
    public void close() {
        GlStateManager._deleteTexture(this.texture);
    }
}
