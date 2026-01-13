/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.Glyph;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class FontStorage.LazyBakedGlyph
implements Supplier<BakedGlyph> {
    final Glyph glyph;
    private @Nullable BakedGlyph baked;

    FontStorage.LazyBakedGlyph(Glyph glyph) {
        this.glyph = glyph;
    }

    @Override
    public BakedGlyph get() {
        if (this.baked == null) {
            this.baked = this.glyph.bake(FontStorage.this.abstractBaker);
        }
        return this.baked;
    }

    @Override
    public /* synthetic */ Object get() {
        return this.get();
    }
}
