/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static abstract class BeaconScreen.IconButtonWidget
extends BeaconScreen.BaseButtonWidget {
    private final Identifier texture;

    protected BeaconScreen.IconButtonWidget(int x, int y, Identifier identifier, Text message) {
        super(x, y, message);
        this.setTooltip(Tooltip.of(message));
        this.texture = identifier;
    }

    @Override
    protected void renderExtra(DrawContext context) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.getX() + 2, this.getY() + 2, 18, 18);
    }
}
