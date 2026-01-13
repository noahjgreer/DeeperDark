/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.WaitingForResponseScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WaitingForResponseScreen
extends Screen {
    private static final Text TITLE = Text.translatable((String)"gui.waitingForResponse.title");
    private static final Text[] BUTTON_TEXTS = new Text[]{Text.empty(), Text.translatable((String)"gui.waitingForResponse.button.inactive", (Object[])new Object[]{4}), Text.translatable((String)"gui.waitingForResponse.button.inactive", (Object[])new Object[]{3}), Text.translatable((String)"gui.waitingForResponse.button.inactive", (Object[])new Object[]{2}), Text.translatable((String)"gui.waitingForResponse.button.inactive", (Object[])new Object[]{1}), ScreenTexts.BACK};
    private static final int SECONDS_BEFORE_BACK_BUTTON_APPEARS = 1;
    private static final int SECONDS_BEFORE_BACK_BUTTON_ACTIVATES = 5;
    private final @Nullable Screen parent;
    private final ThreePartsLayoutWidget layout;
    private final ButtonWidget backButton;
    private int inactiveTicks;

    public WaitingForResponseScreen(@Nullable Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.layout = new ThreePartsLayoutWidget((Screen)this, 33, 0);
        this.backButton = ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).width(200).build();
    }

    protected void init() {
        super.init();
        this.layout.addHeader(TITLE, this.textRenderer);
        this.layout.addBody((Widget)this.backButton);
        this.backButton.visible = false;
        this.backButton.active = false;
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos((Widget)this.layout, (ScreenRect)this.getNavigationFocus());
    }

    public void tick() {
        super.tick();
        if (!this.backButton.active) {
            int i;
            this.backButton.visible = (i = this.inactiveTicks++ / 20) >= 1;
            this.backButton.setMessage(BUTTON_TEXTS[i]);
            if (i == 5) {
                this.backButton.active = true;
                this.narrateScreenIfNarrationEnabled(true);
            }
        }
    }

    public boolean shouldPause() {
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return this.backButton.active;
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    public @Nullable Screen getParentScreen() {
        return this.parent;
    }
}

