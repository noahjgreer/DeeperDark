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
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static abstract class BeaconScreen.BaseButtonWidget
extends PressableWidget
implements BeaconScreen.BeaconButtonWidget {
    private boolean disabled;

    protected BeaconScreen.BaseButtonWidget(int x, int y) {
        super(x, y, 22, 22, ScreenTexts.EMPTY);
    }

    protected BeaconScreen.BaseButtonWidget(int x, int y, Text message) {
        super(x, y, 22, 22, message);
    }

    @Override
    public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Identifier identifier = !this.active ? BUTTON_DISABLED_TEXTURE : (this.disabled ? BUTTON_SELECTED_TEXTURE : (this.isSelected() ? BUTTON_HIGHLIGHTED_TEXTURE : BUTTON_TEXTURE));
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.width, this.height);
        this.renderExtra(context);
    }

    protected abstract void renderExtra(DrawContext var1);

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}
