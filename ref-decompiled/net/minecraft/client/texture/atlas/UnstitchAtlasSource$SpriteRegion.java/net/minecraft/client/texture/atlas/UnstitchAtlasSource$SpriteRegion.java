/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture.atlas;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSprite;
import net.minecraft.client.texture.atlas.UnstitchAtlasSource;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
static class UnstitchAtlasSource.SpriteRegion
implements AtlasSource.SpriteRegion {
    private final AtlasSprite sprite;
    private final UnstitchAtlasSource.Region region;
    private final double divisorX;
    private final double divisorY;

    UnstitchAtlasSource.SpriteRegion(AtlasSprite sprite, UnstitchAtlasSource.Region region, double divisorX, double divisorY) {
        this.sprite = sprite;
        this.region = region;
        this.divisorX = divisorX;
        this.divisorY = divisorY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SpriteContents load(SpriteOpener spriteOpener) {
        try {
            NativeImage nativeImage = this.sprite.read();
            double d = (double)nativeImage.getWidth() / this.divisorX;
            double e = (double)nativeImage.getHeight() / this.divisorY;
            int i = MathHelper.floor(this.region.x * d);
            int j = MathHelper.floor(this.region.y * e);
            int k = MathHelper.floor(this.region.width * d);
            int l = MathHelper.floor(this.region.height * e);
            NativeImage nativeImage2 = new NativeImage(NativeImage.Format.RGBA, k, l, false);
            nativeImage.copyRect(nativeImage2, i, j, 0, 0, k, l, false, false);
            SpriteContents spriteContents = new SpriteContents(this.region.sprite, new SpriteDimensions(k, l), nativeImage2);
            return spriteContents;
        }
        catch (Exception exception) {
            LOGGER.error("Failed to unstitch region {}", (Object)this.region.sprite, (Object)exception);
        }
        finally {
            this.sprite.close();
        }
        return MissingSprite.createSpriteContents();
    }

    @Override
    public void close() {
        this.sprite.close();
    }
}
