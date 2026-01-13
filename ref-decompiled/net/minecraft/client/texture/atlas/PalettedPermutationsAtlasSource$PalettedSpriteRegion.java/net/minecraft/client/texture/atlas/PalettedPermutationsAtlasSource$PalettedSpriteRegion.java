/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.texture.atlas;

import java.io.IOException;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSprite;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
record PalettedPermutationsAtlasSource.PalettedSpriteRegion(AtlasSprite baseImage, Supplier<IntUnaryOperator> palette, Identifier permutationLocation) implements AtlasSource.SpriteRegion
{
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public @Nullable SpriteContents load(SpriteOpener spriteOpener) {
        try {
            NativeImage nativeImage = this.baseImage.read().applyToCopy(this.palette.get());
            SpriteContents spriteContents = new SpriteContents(this.permutationLocation, new SpriteDimensions(nativeImage.getWidth(), nativeImage.getHeight()), nativeImage);
            return spriteContents;
        }
        catch (IOException | IllegalArgumentException exception) {
            LOGGER.error("unable to apply palette to {}", (Object)this.permutationLocation, (Object)exception);
            SpriteContents spriteContents = null;
            return spriteContents;
        }
        finally {
            this.baseImage.close();
        }
    }

    @Override
    public void close() {
        this.baseImage.close();
    }
}
