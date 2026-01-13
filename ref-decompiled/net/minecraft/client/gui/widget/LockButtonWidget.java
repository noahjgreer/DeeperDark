/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.LockButtonWidget
 *  net.minecraft.client.gui.widget.LockButtonWidget$Icon
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class LockButtonWidget
extends ButtonWidget {
    private boolean locked;

    public LockButtonWidget(int x, int y, ButtonWidget.PressAction action) {
        super(x, y, 20, 20, (Text)Text.translatable((String)"narrator.button.difficulty_lock"), action, DEFAULT_NARRATION_SUPPLIER);
    }

    protected MutableText getNarrationMessage() {
        return ScreenTexts.joinSentences((Text[])new Text[]{super.getNarrationMessage(), this.isLocked() ? Text.translatable((String)"narrator.button.difficulty_lock.locked") : Text.translatable((String)"narrator.button.difficulty_lock.unlocked")});
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Icon icon = !this.active ? (this.locked ? Icon.LOCKED_DISABLED : Icon.UNLOCKED_DISABLED) : (this.isSelected() ? (this.locked ? Icon.LOCKED_HOVER : Icon.UNLOCKED_HOVER) : (this.locked ? Icon.LOCKED : Icon.UNLOCKED));
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, icon.texture, this.getX(), this.getY(), this.width, this.height);
    }
}

