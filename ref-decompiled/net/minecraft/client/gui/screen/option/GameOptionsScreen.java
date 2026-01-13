/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.OptionListWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.SimpleOption
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class GameOptionsScreen
extends Screen {
    protected final Screen parent;
    protected final GameOptions gameOptions;
    protected @Nullable OptionListWidget body;
    public final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);

    public GameOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(title);
        this.parent = parent;
        this.gameOptions = gameOptions;
    }

    protected void init() {
        this.initHeader();
        this.initBody();
        this.initFooter();
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void initHeader() {
        this.layout.addHeader(this.title, this.textRenderer);
    }

    protected void initBody() {
        this.body = (OptionListWidget)this.layout.addBody((Widget)new OptionListWidget(this.client, this.width, this));
        this.addOptions();
        ClickableWidget clickableWidget = this.body.getWidgetFor(this.gameOptions.getNarrator());
        if (clickableWidget instanceof CyclingButtonWidget) {
            CyclingButtonWidget cyclingButtonWidget;
            this.narratorToggleButton = cyclingButtonWidget = (CyclingButtonWidget)clickableWidget;
            this.narratorToggleButton.active = this.client.getNarratorManager().isActive();
        }
    }

    protected abstract void addOptions();

    protected void initFooter() {
        this.layout.addFooter((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.close()).width(200).build());
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.body != null) {
            this.body.position(this.width, this.layout);
        }
    }

    public void removed() {
        this.client.options.write();
    }

    public void close() {
        if (this.body != null) {
            this.body.applyAllPendingValues();
        }
        this.client.setScreen(this.parent);
    }

    public void update(SimpleOption<?> option) {
        if (this.body != null) {
            this.body.update(option);
        }
    }
}

