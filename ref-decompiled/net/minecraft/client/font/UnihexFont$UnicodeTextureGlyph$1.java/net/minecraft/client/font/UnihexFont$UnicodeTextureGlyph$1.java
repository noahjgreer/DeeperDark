/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphMetrics;

@Environment(value=EnvType.CLIENT)
class UnihexFont.UnicodeTextureGlyph.1
implements GlyphMetrics {
    UnihexFont.UnicodeTextureGlyph.1() {
    }

    @Override
    public float getAdvance() {
        return UnicodeTextureGlyph.this.width() / 2 + 1;
    }

    @Override
    public float getShadowOffset() {
        return 0.5f;
    }

    @Override
    public float getBoldOffset() {
        return 0.5f;
    }
}
