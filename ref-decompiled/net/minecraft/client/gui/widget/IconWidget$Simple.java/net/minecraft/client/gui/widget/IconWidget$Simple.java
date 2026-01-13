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
static class IconWidget.Simple
extends IconWidget {
    private Identifier texture;

    public IconWidget.Simple(int x, int y, int width, int height, Identifier texture) {
        super(x, y, width, height);
        this.texture = texture;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public void setTexture(Identifier texture) {
        this.texture = texture;
    }
}
