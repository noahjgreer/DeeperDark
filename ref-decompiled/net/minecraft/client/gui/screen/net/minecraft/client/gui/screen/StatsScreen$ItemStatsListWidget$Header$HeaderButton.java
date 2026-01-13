/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class StatsScreen.ItemStatsListWidget.Header.HeaderButton
extends TexturedButtonWidget {
    private final Identifier texture;

    StatsScreen.ItemStatsListWidget.Header.HeaderButton(StatsScreen.ItemStatsListWidget.Header header, int index, Identifier texture) {
        super(18, 18, new ButtonTextures(HEADER_TEXTURE, SLOT_TEXTURE), button -> header.field_62154.selectStatType(header.field_62154.getStatType(index)), header.field_62154.getStatType(index).getName());
        this.texture = texture;
        this.setTooltip(Tooltip.of(this.getMessage()));
    }

    @Override
    public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Identifier identifier = this.textures.get(this.isInteractable(), this.isSelected());
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.width, this.height);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), this.width, this.height);
    }
}
