/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.TextureUtil
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.BakedGlyphImpl
 *  net.minecraft.client.font.GlyphAtlasTexture
 *  net.minecraft.client.font.GlyphAtlasTexture$Slot
 *  net.minecraft.client.font.GlyphMetrics
 *  net.minecraft.client.font.TextRenderLayerSet
 *  net.minecraft.client.font.UploadableGlyph
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.DynamicTexture
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.file.Path;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyphImpl;
import net.minecraft.client.font.GlyphAtlasTexture;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.font.UploadableGlyph;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GlyphAtlasTexture
extends AbstractTexture
implements DynamicTexture {
    private static final int SLOT_LENGTH = 256;
    private final TextRenderLayerSet textRenderLayers;
    private final boolean hasColor;
    private final Slot rootSlot;

    public GlyphAtlasTexture(Supplier<String> nameSupplier, TextRenderLayerSet textRenderLayers, boolean hasColor) {
        this.hasColor = hasColor;
        this.rootSlot = new Slot(0, 0, 256, 256);
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.glTexture = gpuDevice.createTexture(nameSupplier, 7, hasColor ? TextureFormat.RGBA8 : TextureFormat.RED8, 256, 256, 1, 1);
        this.sampler = RenderSystem.getSamplerCache().getRepeated(FilterMode.NEAREST);
        this.glTextureView = gpuDevice.createTextureView(this.glTexture);
        this.textRenderLayers = textRenderLayers;
    }

    public @Nullable BakedGlyphImpl bake(GlyphMetrics metrics, UploadableGlyph glyph) {
        if (glyph.hasColor() != this.hasColor) {
            return null;
        }
        Slot slot = this.rootSlot.findSlotFor(glyph);
        if (slot != null) {
            glyph.upload(slot.x, slot.y, this.getGlTexture());
            float f = 256.0f;
            float g = 256.0f;
            float h = 0.01f;
            return new BakedGlyphImpl(metrics, this.textRenderLayers, this.getGlTextureView(), ((float)slot.x + 0.01f) / 256.0f, ((float)slot.x - 0.01f + (float)glyph.getWidth()) / 256.0f, ((float)slot.y + 0.01f) / 256.0f, ((float)slot.y - 0.01f + (float)glyph.getHeight()) / 256.0f, glyph.getXMin(), glyph.getXMax(), glyph.getYMin(), glyph.getYMax());
        }
        return null;
    }

    public void save(Identifier id, Path path) {
        if (this.glTexture == null) {
            return;
        }
        String string = id.toUnderscoreSeparatedString();
        TextureUtil.writeAsPNG((Path)path, (String)string, (GpuTexture)this.glTexture, (int)0, color -> (color & 0xFF000000) == 0 ? -16777216 : color);
    }
}

