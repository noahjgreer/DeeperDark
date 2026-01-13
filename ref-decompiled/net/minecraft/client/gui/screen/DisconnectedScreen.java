/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.DisconnectedScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.TitleScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.MultilineTextWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.network.DisconnectionInfo
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 */
package net.minecraft.client.gui.screen;

import java.net.URI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class DisconnectedScreen
extends Screen {
    private static final Text TO_MENU_TEXT = Text.translatable((String)"gui.toMenu");
    private static final Text TO_TITLE_TEXT = Text.translatable((String)"gui.toTitle");
    private static final Text REPORT_TO_SERVER_TEXT = Text.translatable((String)"gui.report_to_server");
    private static final Text OPEN_REPORT_DIR_TEXT = Text.translatable((String)"gui.open_report_dir");
    private final Screen parent;
    private final DisconnectionInfo info;
    private final Text buttonLabel;
    private final DirectionalLayoutWidget grid = DirectionalLayoutWidget.vertical();

    public DisconnectedScreen(Screen parent, Text title, Text reason) {
        this(parent, title, new DisconnectionInfo(reason));
    }

    public DisconnectedScreen(Screen parent, Text title, Text reason, Text buttonLabel) {
        this(parent, title, new DisconnectionInfo(reason), buttonLabel);
    }

    public DisconnectedScreen(Screen parent, Text title, DisconnectionInfo info) {
        this(parent, title, info, TO_MENU_TEXT);
    }

    public DisconnectedScreen(Screen parent, Text title, DisconnectionInfo info, Text buttonLabel) {
        super(title);
        this.parent = parent;
        this.info = info;
        this.buttonLabel = buttonLabel;
    }

    protected void init() {
        this.grid.getMainPositioner().alignHorizontalCenter().margin(10);
        this.grid.add((Widget)new TextWidget(this.title, this.textRenderer));
        this.grid.add((Widget)new MultilineTextWidget(this.info.reason(), this.textRenderer).setMaxWidth(this.width - 50).setCentered(true));
        this.grid.getMainPositioner().margin(2);
        this.info.bugReportLink().ifPresent(uri -> this.grid.add((Widget)ButtonWidget.builder((Text)REPORT_TO_SERVER_TEXT, (ButtonWidget.PressAction)ConfirmLinkScreen.opening((Screen)this, (URI)uri, (boolean)false)).width(200).build()));
        this.info.report().ifPresent(path -> this.grid.add((Widget)ButtonWidget.builder((Text)OPEN_REPORT_DIR_TEXT, button -> Util.getOperatingSystem().open(path.getParent())).width(200).build()));
        ButtonWidget buttonWidget = this.client.isMultiplayerEnabled() ? ButtonWidget.builder((Text)this.buttonLabel, button -> this.client.setScreen(this.parent)).width(200).build() : ButtonWidget.builder((Text)TO_TITLE_TEXT, button -> this.client.setScreen((Screen)new TitleScreen())).width(200).build();
        this.grid.add((Widget)buttonWidget);
        this.grid.refreshPositions();
        this.grid.forEachChild(arg_0 -> ((DisconnectedScreen)this).addDrawableChild(arg_0));
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        SimplePositioningWidget.setPos((Widget)this.grid, (ScreenRect)this.getNavigationFocus());
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{this.title, this.info.reason()});
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}

