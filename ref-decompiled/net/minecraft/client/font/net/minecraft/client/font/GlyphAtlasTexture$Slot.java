/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.UploadableGlyph;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class GlyphAtlasTexture.Slot {
    final int x;
    final int y;
    private final int width;
    private final int height;
    private @Nullable GlyphAtlasTexture.Slot subSlot1;
    private @Nullable GlyphAtlasTexture.Slot subSlot2;
    private boolean occupied;

    GlyphAtlasTexture.Slot(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Nullable GlyphAtlasTexture.Slot findSlotFor(UploadableGlyph glyph) {
        if (this.subSlot1 != null && this.subSlot2 != null) {
            GlyphAtlasTexture.Slot slot = this.subSlot1.findSlotFor(glyph);
            if (slot == null) {
                slot = this.subSlot2.findSlotFor(glyph);
            }
            return slot;
        }
        if (this.occupied) {
            return null;
        }
        int i = glyph.getWidth();
        int j = glyph.getHeight();
        if (i > this.width || j > this.height) {
            return null;
        }
        if (i == this.width && j == this.height) {
            this.occupied = true;
            return this;
        }
        int k = this.width - i;
        int l = this.height - j;
        if (k > l) {
            this.subSlot1 = new GlyphAtlasTexture.Slot(this.x, this.y, i, this.height);
            this.subSlot2 = new GlyphAtlasTexture.Slot(this.x + i + 1, this.y, this.width - i - 1, this.height);
        } else {
            this.subSlot1 = new GlyphAtlasTexture.Slot(this.x, this.y, this.width, j);
            this.subSlot2 = new GlyphAtlasTexture.Slot(this.x, this.y + j + 1, this.width, this.height - j - 1);
        }
        return this.subSlot1.findSlotFor(glyph);
    }
}
