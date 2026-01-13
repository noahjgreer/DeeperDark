/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.metadata.TextureResourceMetadata
 *  net.minecraft.client.texture.CubemapTexture
 *  net.minecraft.client.texture.MipmapStrategy
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.ReloadableTexture
 *  net.minecraft.client.texture.TextureContents
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.MipmapStrategy;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CubemapTexture
extends ReloadableTexture {
    private static final String[] TEXTURE_SUFFIXES = new String[]{"_1.png", "_3.png", "_5.png", "_4.png", "_0.png", "_2.png"};

    public CubemapTexture(Identifier identifier) {
        super(identifier);
    }

    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        Identifier identifier = this.getId();
        try (TextureContents textureContents = TextureContents.load((ResourceManager)resourceManager, (Identifier)identifier.withSuffixedPath(TEXTURE_SUFFIXES[0]));){
            int i = textureContents.image().getWidth();
            int j = textureContents.image().getHeight();
            NativeImage nativeImage = new NativeImage(i, j * 6, false);
            textureContents.image().copyRect(nativeImage, 0, 0, 0, 0, i, j, false, true);
            for (int k = 1; k < 6; ++k) {
                try (TextureContents textureContents2 = TextureContents.load((ResourceManager)resourceManager, (Identifier)identifier.withSuffixedPath(TEXTURE_SUFFIXES[k]));){
                    if (textureContents2.image().getWidth() != i || textureContents2.image().getHeight() != j) {
                        throw new IOException("Image dimensions of cubemap '" + String.valueOf(identifier) + "' sides do not match: part 0 is " + i + "x" + j + ", but part " + k + " is " + textureContents2.image().getWidth() + "x" + textureContents2.image().getHeight());
                    }
                    textureContents2.image().copyRect(nativeImage, 0, 0, 0, k * j, i, j, false, true);
                    continue;
                }
            }
            TextureContents textureContents2 = new TextureContents(nativeImage, new TextureResourceMetadata(true, false, MipmapStrategy.MEAN, 0.0f));
            return textureContents2;
        }
    }

    protected void load(NativeImage image) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        int i = image.getWidth();
        int j = image.getHeight() / 6;
        this.close();
        this.glTexture = gpuDevice.createTexture(() -> ((Identifier)this.getId()).toString(), 21, TextureFormat.RGBA8, i, j, 6, 1);
        this.glTextureView = gpuDevice.createTextureView(this.glTexture);
        for (int k = 0; k < 6; ++k) {
            gpuDevice.createCommandEncoder().writeToTexture(this.glTexture, image, 0, k, 0, 0, i, j, 0, j * k);
        }
    }
}

