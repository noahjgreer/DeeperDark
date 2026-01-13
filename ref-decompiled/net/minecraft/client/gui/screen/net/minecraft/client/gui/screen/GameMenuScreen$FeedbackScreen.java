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
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
static class GameMenuScreen.FeedbackScreen
extends Screen {
    private static final Text TITLE = Text.translatable("menu.feedback.title");
    public final Screen parent;
    private final ThreePartsLayoutWidget layoutWidget = new ThreePartsLayoutWidget(this);

    protected GameMenuScreen.FeedbackScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.layoutWidget.addHeader(TITLE, this.textRenderer);
        GridWidget gridWidget = this.layoutWidget.addBody(new GridWidget());
        gridWidget.getMainPositioner().margin(4, 4, 4, 0);
        GridWidget.Adder adder = gridWidget.createAdder(2);
        GameMenuScreen.addFeedbackAndBugsButtons(this, adder);
        this.layoutWidget.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(200).build());
        this.layoutWidget.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layoutWidget.refreshPositions();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
