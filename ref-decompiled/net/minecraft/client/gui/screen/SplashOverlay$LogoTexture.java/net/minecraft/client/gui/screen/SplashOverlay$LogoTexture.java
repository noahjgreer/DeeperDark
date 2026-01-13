/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.MipmapStrategy;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;

@Environment(value=EnvType.CLIENT)
static class SplashOverlay.LogoTexture
extends ReloadableTexture {
    public SplashOverlay.LogoTexture() {
        super(LOGO);
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        ResourceFactory resourceFactory = MinecraftClient.getInstance().getDefaultResourcePack().getFactory();
        try (InputStream inputStream = resourceFactory.open(LOGO);){
            TextureContents textureContents = new TextureContents(NativeImage.read(inputStream), new TextureResourceMetadata(true, true, MipmapStrategy.MEAN, 0.0f));
            return textureContents;
        }
    }
}
