/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.MultilineTextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.gui.screen.RealmsClientIncompatibleScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsClientIncompatibleScreen
extends RealmsScreen {
    private static final Text INCOMPATIBLE_TITLE = Text.translatable((String)"mco.client.incompatible.title").withColor(-65536);
    private static final Text GAME_VERSION = Text.literal((String)SharedConstants.getGameVersion().name()).withColor(-65536);
    private static final Text UNSUPPORTED_SNAPSHOT_VERSION = Text.translatable((String)"mco.client.unsupported.snapshot.version", (Object[])new Object[]{GAME_VERSION});
    private static final Text OUTDATED_STABLE_VERSION = Text.translatable((String)"mco.client.outdated.stable.version", (Object[])new Object[]{GAME_VERSION});
    private final Screen parent;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);

    public RealmsClientIncompatibleScreen(Screen parent) {
        super(INCOMPATIBLE_TITLE);
        this.parent = parent;
    }

    public void init() {
        this.layout.addHeader(INCOMPATIBLE_TITLE, this.textRenderer);
        this.layout.addBody((Widget)new MultilineTextWidget(this.getErrorText(), this.textRenderer).setCentered(true));
        this.layout.addFooter((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, buttonWidget -> this.close()).width(200).build());
        this.layout.forEachChild(element -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(element);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    private Text getErrorText() {
        if (SharedConstants.getGameVersion().stable()) {
            return OUTDATED_STABLE_VERSION;
        }
        return UNSUPPORTED_SNAPSHOT_VERSION;
    }
}

