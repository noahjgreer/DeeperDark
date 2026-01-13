/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.UploadableGlyph;

@Environment(value=EnvType.CLIENT)
class BuiltinEmptyGlyph.1
implements UploadableGlyph {
    BuiltinEmptyGlyph.1() {
    }

    @Override
    public int getWidth() {
        return BuiltinEmptyGlyph.this.image.getWidth();
    }

    @Override
    public int getHeight() {
        return BuiltinEmptyGlyph.this.image.getHeight();
    }

    @Override
    public float getOversample() {
        return 1.0f;
    }

    @Override
    public void upload(int x, int y, GpuTexture texture) {
        RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, BuiltinEmptyGlyph.this.image, 0, 0, x, y, BuiltinEmptyGlyph.this.image.getWidth(), BuiltinEmptyGlyph.this.image.getHeight(), 0, 0);
    }

    @Override
    public boolean hasColor() {
        return true;
    }
}
