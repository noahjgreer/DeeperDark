/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.DrawContext$HoverType
 *  net.minecraft.client.gui.cursor.StandardCursors
 *  net.minecraft.client.gui.navigation.GuiNavigationType
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.ClickableWidget$InactivityIndicatingWidget
 *  net.minecraft.client.gui.widget.SliderWidget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class SliderWidget
extends ClickableWidget.InactivityIndicatingWidget {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"widget/slider");
    private static final Identifier HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"widget/slider_highlighted");
    private static final Identifier HANDLE_TEXTURE = Identifier.ofVanilla((String)"widget/slider_handle");
    private static final Identifier HANDLE_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"widget/slider_handle_highlighted");
    protected static final int field_43054 = 2;
    public static final int field_60708 = 20;
    protected static final int field_41790 = 8;
    private static final int field_41789 = 4;
    protected double value;
    protected boolean sliderFocused;
    private boolean dragging;

    public SliderWidget(int x, int y, int width, int height, Text text, double value) {
        super(x, y, width, height, text);
        this.value = value;
    }

    private Identifier getTexture() {
        if (this.isInteractable() && this.isFocused() && !this.sliderFocused) {
            return HIGHLIGHTED_TEXTURE;
        }
        return TEXTURE;
    }

    private Identifier getHandleTexture() {
        if (this.isInteractable() && (this.hovered || this.sliderFocused)) {
            return HANDLE_HIGHLIGHTED_TEXTURE;
        }
        return HANDLE_TEXTURE;
    }

    protected MutableText getNarrationMessage() {
        return Text.translatable((String)"gui.narrate.slider", (Object[])new Object[]{this.getMessage()});
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)this.getNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                if (this.sliderFocused) {
                    builder.put(NarrationPart.USAGE, (Text)Text.translatable((String)"narration.slider.usage.focused"));
                } else {
                    builder.put(NarrationPart.USAGE, (Text)Text.translatable((String)"narration.slider.usage.focused.keyboard_cannot_change_value"));
                }
            } else {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable((String)"narration.slider.usage.hovered"));
            }
        }
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getTexture(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorHelper.getWhite((float)this.alpha));
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getHandleTexture(), this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.getHeight(), ColorHelper.getWhite((float)this.alpha));
        this.drawTextWithMargin(context.getHoverListener((ClickableWidget)this, DrawContext.HoverType.NONE), this.getMessage(), 2);
        if (this.isHovered()) {
            context.setCursor(this.dragging ? StandardCursors.RESIZE_EW : StandardCursors.POINTING_HAND);
        }
    }

    public void onClick(Click click, boolean doubled) {
        this.dragging = this.active;
        this.setValueFromMouse(click);
    }

    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.sliderFocused = false;
            return;
        }
        GuiNavigationType guiNavigationType = MinecraftClient.getInstance().getNavigationType();
        if (guiNavigationType == GuiNavigationType.MOUSE || guiNavigationType == GuiNavigationType.KEYBOARD_TAB) {
            this.sliderFocused = true;
        }
    }

    public boolean keyPressed(KeyInput input) {
        if (input.isEnterOrSpace()) {
            this.sliderFocused = !this.sliderFocused;
            return true;
        }
        if (this.sliderFocused) {
            boolean bl = input.isLeft();
            boolean bl2 = input.isRight();
            if (bl || bl2) {
                float f = bl ? -1.0f : 1.0f;
                this.setValue(this.value + (double)(f / (float)(this.width - 8)));
                return true;
            }
        }
        return false;
    }

    private void setValueFromMouse(Click click) {
        this.setValue((click.x() - (double)(this.getX() + 4)) / (double)(this.width - 8));
    }

    protected void setValue(double value) {
        double d = this.value;
        this.value = MathHelper.clamp((double)value, (double)0.0, (double)1.0);
        if (d != this.value) {
            this.applyValue();
        }
        this.updateMessage();
    }

    protected void onDrag(Click click, double offsetX, double offsetY) {
        this.setValueFromMouse(click);
        super.onDrag(click, offsetX, offsetY);
    }

    public void playDownSound(SoundManager soundManager) {
    }

    public void onRelease(Click click) {
        this.dragging = false;
        super.playDownSound(MinecraftClient.getInstance().getSoundManager());
    }

    protected abstract void updateMessage();

    protected abstract void applyValue();
}

