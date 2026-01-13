/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen
 *  net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen$DetailsScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.GridWidget
 *  net.minecraft.client.gui.widget.GridWidget$Adder
 *  net.minecraft.client.gui.widget.MultilineTextWidget
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.resource.ResourcePackProfile
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.pack;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ExperimentalWarningScreen
extends Screen {
    private static final Text TITLE = Text.translatable((String)"selectWorld.experimental.title");
    private static final Text MESSAGE = Text.translatable((String)"selectWorld.experimental.message");
    private static final Text DETAILS = Text.translatable((String)"selectWorld.experimental.details");
    private static final int field_42498 = 10;
    private static final int field_42499 = 100;
    private final BooleanConsumer callback;
    final Collection<ResourcePackProfile> enabledProfiles;
    private final GridWidget grid = new GridWidget().setColumnSpacing(10).setRowSpacing(20);

    public ExperimentalWarningScreen(Collection<ResourcePackProfile> enabledProfiles, BooleanConsumer callback) {
        super(TITLE);
        this.enabledProfiles = enabledProfiles;
        this.callback = callback;
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{super.getNarratedTitle(), MESSAGE});
    }

    protected void init() {
        super.init();
        GridWidget.Adder adder = this.grid.createAdder(2);
        Positioner positioner = adder.copyPositioner().alignHorizontalCenter();
        adder.add((Widget)new TextWidget(this.title, this.textRenderer), 2, positioner);
        MultilineTextWidget multilineTextWidget = (MultilineTextWidget)adder.add((Widget)new MultilineTextWidget(MESSAGE, this.textRenderer).setCentered(true), 2, positioner);
        multilineTextWidget.setMaxWidth(310);
        adder.add((Widget)ButtonWidget.builder((Text)DETAILS, button -> this.client.setScreen((Screen)new DetailsScreen(this))).width(100).build(), 2, positioner);
        adder.add((Widget)ButtonWidget.builder((Text)ScreenTexts.PROCEED, button -> this.callback.accept(true)).build());
        adder.add((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.callback.accept(false)).build());
        this.grid.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.grid.refreshPositions();
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        SimplePositioningWidget.setPos((Widget)this.grid, (int)0, (int)0, (int)this.width, (int)this.height, (float)0.5f, (float)0.5f);
    }

    public void close() {
        this.callback.accept(false);
    }
}

