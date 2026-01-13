/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.MultilineTextWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ConfirmScreen
extends Screen {
    private final Text message;
    protected DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(8);
    protected Text yesText;
    protected Text noText;
    protected @Nullable ButtonWidget yesButton;
    protected @Nullable ButtonWidget noButton;
    private int buttonEnableTimer;
    protected final BooleanConsumer callback;

    public ConfirmScreen(BooleanConsumer callback, Text title, Text message) {
        this(callback, title, message, ScreenTexts.YES, ScreenTexts.NO);
    }

    public ConfirmScreen(BooleanConsumer callback, Text title, Text message, Text yesText, Text noText) {
        super(title);
        this.callback = callback;
        this.message = message;
        this.yesText = yesText;
        this.noText = noText;
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{super.getNarratedTitle(), this.message});
    }

    protected void init() {
        super.init();
        this.layout.getMainPositioner().alignHorizontalCenter();
        this.layout.add((Widget)new TextWidget(this.title, this.textRenderer));
        this.layout.add((Widget)new MultilineTextWidget(this.message, this.textRenderer).setMaxWidth(this.width - 50).setMaxRows(15).setCentered(true));
        this.initExtras();
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.add((Widget)DirectionalLayoutWidget.horizontal().spacing(4));
        directionalLayoutWidget.getMainPositioner().marginTop(16);
        this.addButtons(directionalLayoutWidget);
        this.layout.forEachChild(arg_0 -> ((ConfirmScreen)this).addDrawableChild(arg_0));
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos((Widget)this.layout, (ScreenRect)this.getNavigationFocus());
    }

    protected void initExtras() {
    }

    protected void addButtons(DirectionalLayoutWidget layout) {
        this.yesButton = (ButtonWidget)layout.add((Widget)ButtonWidget.builder((Text)this.yesText, button -> this.callback.accept(true)).build());
        this.noButton = (ButtonWidget)layout.add((Widget)ButtonWidget.builder((Text)this.noText, button -> this.callback.accept(false)).build());
    }

    public void disableButtons(int ticks) {
        this.buttonEnableTimer = ticks;
        this.yesButton.active = false;
        this.noButton.active = false;
    }

    public void tick() {
        super.tick();
        if (--this.buttonEnableTimer == 0) {
            this.yesButton.active = true;
            this.noButton.active = true;
        }
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public boolean keyPressed(KeyInput input) {
        if (this.buttonEnableTimer <= 0 && input.key() == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed(input);
    }
}

