/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static class IconWidget.Texture
extends IconWidget {
    private Identifier texture;
    private final int textureWidth;
    private final int textureHeight;

    public IconWidget.Texture(int x, int y, int width, int height, Identifier texture, int textureWidth, int textureHeight) {
        super(x, y, width, height);
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), 0.0f, 0.0f, this.getWidth(), this.getHeight(), this.textureWidth, this.textureHeight);
    }

    @Override
    public void setTexture(Identifier texture) {
        this.texture = texture;
    }
}
