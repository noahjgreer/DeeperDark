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
import net.minecraft.client.gui.screen.GameModeSwitcherScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;

@Environment(value=EnvType.CLIENT)
public static class GameModeSwitcherScreen.ButtonWidget
extends ClickableWidget {
    final GameModeSwitcherScreen.GameModeSelection gameMode;
    private boolean selected;

    public GameModeSwitcherScreen.ButtonWidget(GameModeSwitcherScreen.GameModeSelection gameMode, int x, int y) {
        super(x, y, 26, 26, gameMode.text);
        this.gameMode = gameMode;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.drawBackground(context);
        if (this.selected) {
            this.drawSelectionBox(context);
        }
        this.gameMode.renderIcon(context, this.getX() + 5, this.getY() + 5);
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    @Override
    public boolean isSelected() {
        return super.isSelected() || this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private void drawBackground(DrawContext context) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_TEXTURE, this.getX(), this.getY(), 26, 26);
    }

    private void drawSelectionBox(DrawContext context) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SELECTION_TEXTURE, this.getX(), this.getY(), 26, 26);
    }
}
