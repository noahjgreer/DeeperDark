package net.minecraft.client.font;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.file.Path;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class GlyphAtlasTexture extends AbstractTexture implements DynamicTexture {
   private static final int SLOT_LENGTH = 256;
   private final TextRenderLayerSet textRenderLayers;
   private final boolean hasColor;
   private final Slot rootSlot;

   public GlyphAtlasTexture(Supplier nameSupplier, TextRenderLayerSet textRenderLayers, boolean hasColor) {
      this.hasColor = hasColor;
      this.rootSlot = new Slot(0, 0, 256, 256);
      GpuDevice gpuDevice = RenderSystem.getDevice();
      this.glTexture = gpuDevice.createTexture((Supplier)nameSupplier, 7, hasColor ? TextureFormat.RGBA8 : TextureFormat.RED8, 256, 256, 1, 1);
      this.glTexture.setTextureFilter(FilterMode.NEAREST, false);
      this.glTextureView = gpuDevice.createTextureView(this.glTexture);
      this.textRenderLayers = textRenderLayers;
   }

   @Nullable
   public BakedGlyph bake(RenderableGlyph glyph) {
      if (glyph.hasColor() != this.hasColor) {
         return null;
      } else {
         Slot slot = this.rootSlot.findSlotFor(glyph);
         if (slot != null && this.glTexture != null && this.glTextureView != null) {
            glyph.upload(slot.x, slot.y, this.glTexture);
            float f = 256.0F;
            float g = 256.0F;
            float h = 0.01F;
            return new BakedGlyph(this.textRenderLayers, this.glTextureView, ((float)slot.x + 0.01F) / 256.0F, ((float)slot.x - 0.01F + (float)glyph.getWidth()) / 256.0F, ((float)slot.y + 0.01F) / 256.0F, ((float)slot.y - 0.01F + (float)glyph.getHeight()) / 256.0F, glyph.getXMin(), glyph.getXMax(), glyph.getYMin(), glyph.getYMax());
         } else {
            return null;
         }
      }
   }

   public void save(Identifier id, Path path) {
      if (this.glTexture != null) {
         String string = id.toUnderscoreSeparatedString();
         TextureUtil.writeAsPNG(path, string, this.glTexture, 0, (color) -> {
            return (color & -16777216) == 0 ? -16777216 : color;
         });
      }
   }

   @Environment(EnvType.CLIENT)
   private static class Slot {
      final int x;
      final int y;
      private final int width;
      private final int height;
      @Nullable
      private Slot subSlot1;
      @Nullable
      private Slot subSlot2;
      private boolean occupied;

      Slot(int x, int y, int width, int height) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }

      @Nullable
      Slot findSlotFor(RenderableGlyph glyph) {
         if (this.subSlot1 != null && this.subSlot2 != null) {
            Slot slot = this.subSlot1.findSlotFor(glyph);
            if (slot == null) {
               slot = this.subSlot2.findSlotFor(glyph);
            }

            return slot;
         } else if (this.occupied) {
            return null;
         } else {
            int i = glyph.getWidth();
            int j = glyph.getHeight();
            if (i <= this.width && j <= this.height) {
               if (i == this.width && j == this.height) {
                  this.occupied = true;
                  return this;
               } else {
                  int k = this.width - i;
                  int l = this.height - j;
                  if (k > l) {
                     this.subSlot1 = new Slot(this.x, this.y, i, this.height);
                     this.subSlot2 = new Slot(this.x + i + 1, this.y, this.width - i - 1, this.height);
                  } else {
                     this.subSlot1 = new Slot(this.x, this.y, this.width, j);
                     this.subSlot2 = new Slot(this.x, this.y + j + 1, this.width, this.height - j - 1);
                  }

                  return this.subSlot1.findSlotFor(glyph);
               }
            } else {
               return null;
            }
         }
      }
   }
}
