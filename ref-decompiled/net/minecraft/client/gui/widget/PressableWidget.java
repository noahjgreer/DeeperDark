/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ButtonTextures
 *  net.minecraft.client.gui.widget.ClickableWidget$InactivityIndicatingWidget
 *  net.minecraft.client.gui.widget.PressableWidget
 *  net.minecraft.client.input.AbstractInput
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class PressableWidget
extends ClickableWidget.InactivityIndicatingWidget {
    protected static final int field_43050 = 2;
    private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla((String)"widget/button"), Identifier.ofVanilla((String)"widget/button_disabled"), Identifier.ofVanilla((String)"widget/button_highlighted"));
    private @Nullable Supplier<Boolean> focusOverride;

    public PressableWidget(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    public abstract void onPress(AbstractInput var1);

    protected final void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.drawIcon(context, mouseX, mouseY, deltaTicks);
        this.setCursor(context);
    }

    protected abstract void drawIcon(DrawContext var1, int var2, int var3, float var4);

    protected void drawLabel(DrawnTextConsumer drawer) {
        this.drawTextWithMargin(drawer, this.getMessage(), 2);
    }

    protected final void drawButton(DrawContext context) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURES.get(this.active, this.focusOverride != null ? ((Boolean)this.focusOverride.get()).booleanValue() : this.isSelected()), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorHelper.getWhite((float)this.alpha));
    }

    public void onClick(Click click, boolean doubled) {
        this.onPress((AbstractInput)click);
    }

    public boolean keyPressed(KeyInput input) {
        if (!this.isInteractable()) {
            return false;
        }
        if (input.isEnterOrSpace()) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onPress((AbstractInput)input);
            return true;
        }
        return false;
    }

    public void setFocusOverride(Supplier<Boolean> focusOverride) {
        this.focusOverride = focusOverride;
    }
}

