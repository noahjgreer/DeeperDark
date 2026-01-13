/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.CreditsScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Urls
 */
package net.minecraft.client.gui.screen.option;

import java.net.URI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Urls;

@Environment(value=EnvType.CLIENT)
public class CreditsAndAttributionScreen
extends Screen {
    private static final int SPACING = 8;
    private static final int BUTTON_WIDTH = 210;
    private static final Text TITLE = Text.translatable((String)"credits_and_attribution.screen.title");
    private static final Text CREDITS_TEXT = Text.translatable((String)"credits_and_attribution.button.credits");
    private static final Text ATTRIBUTION_TEXT = Text.translatable((String)"credits_and_attribution.button.attribution");
    private static final Text LICENSE_TEXT = Text.translatable((String)"credits_and_attribution.button.licenses");
    private final Screen parent;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);

    public CreditsAndAttributionScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    protected void init() {
        this.layout.addHeader(TITLE, this.textRenderer);
        DirectionalLayoutWidget directionalLayoutWidget = ((DirectionalLayoutWidget)this.layout.addBody((Widget)DirectionalLayoutWidget.vertical())).spacing(8);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)CREDITS_TEXT, button -> this.openCredits()).width(210).build());
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ATTRIBUTION_TEXT, (ButtonWidget.PressAction)ConfirmLinkScreen.opening((Screen)this, (URI)Urls.JAVA_ATTRIBUTION)).width(210).build());
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)LICENSE_TEXT, (ButtonWidget.PressAction)ConfirmLinkScreen.opening((Screen)this, (URI)Urls.JAVA_LICENSES)).width(210).build());
        this.layout.addFooter((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.close()).width(200).build());
        this.layout.refreshPositions();
        this.layout.forEachChild(arg_0 -> ((CreditsAndAttributionScreen)this).addDrawableChild(arg_0));
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    private void openCredits() {
        this.client.setScreen((Screen)new CreditsScreen(false, () -> this.client.setScreen((Screen)this)));
    }

    public void close() {
        this.client.setScreen(this.parent);
    }
}

