/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.MultilineTextWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.gui.screen.RealmsParentalConsentScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.util.NarratorManager
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Urls
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import java.net.URI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Urls;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsParentalConsentScreen
extends RealmsScreen {
    private static final Text PRIVACY_INFO_TEXT = Text.translatable((String)"mco.account.privacy.information");
    private static final int field_46850 = 15;
    private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical();
    private final Screen parent;
    private @Nullable MultilineTextWidget privacyInfoWidget;

    public RealmsParentalConsentScreen(Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
    }

    public void init() {
        this.layout.spacing(15).getMainPositioner().alignHorizontalCenter();
        this.privacyInfoWidget = new MultilineTextWidget(PRIVACY_INFO_TEXT, this.textRenderer).setCentered(true);
        this.layout.add((Widget)this.privacyInfoWidget);
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.add((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        MutableText text = Text.translatable((String)"mco.account.privacy.info.button");
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)text, (ButtonWidget.PressAction)ConfirmLinkScreen.opening((Screen)this, (URI)Urls.GDPR)).build());
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    protected void refreshWidgetPositions() {
        if (this.privacyInfoWidget != null) {
            this.privacyInfoWidget.setMaxWidth(this.width - 15);
        }
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos((Widget)this.layout, (ScreenRect)this.getNavigationFocus());
    }

    public Text getNarratedTitle() {
        return PRIVACY_INFO_TEXT;
    }
}

