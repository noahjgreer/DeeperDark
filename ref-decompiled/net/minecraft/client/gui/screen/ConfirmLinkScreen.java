/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ConfirmLinkScreen
extends ConfirmScreen {
    private static final Text WARNING = Text.translatable((String)"chat.link.warning").withColor(-13108);
    private static final int field_61000 = 100;
    private final String link;
    private final boolean drawWarning;

    public ConfirmLinkScreen(BooleanConsumer callback, String link, boolean linkTrusted) {
        this(callback, (Text)ConfirmLinkScreen.getConfirmText((boolean)linkTrusted), (Text)Text.literal((String)link), link, linkTrusted ? ScreenTexts.CANCEL : ScreenTexts.NO, linkTrusted);
    }

    public ConfirmLinkScreen(BooleanConsumer callback, Text title, String link, boolean linkTrusted) {
        this(callback, title, (Text)ConfirmLinkScreen.getConfirmText((boolean)linkTrusted, (String)link), link, linkTrusted ? ScreenTexts.CANCEL : ScreenTexts.NO, linkTrusted);
    }

    public ConfirmLinkScreen(BooleanConsumer callback, Text title, URI link, boolean linkTrusted) {
        this(callback, title, link.toString(), linkTrusted);
    }

    public ConfirmLinkScreen(BooleanConsumer callback, Text title, Text message, URI link, Text noText, boolean linkTrusted) {
        this(callback, title, message, link.toString(), noText, true);
    }

    public ConfirmLinkScreen(BooleanConsumer callback, Text title, Text message, String link, Text noText, boolean linkTrusted) {
        super(callback, title, message);
        this.yesText = linkTrusted ? ScreenTexts.OPEN_LINK : ScreenTexts.YES;
        this.noText = noText;
        this.drawWarning = !linkTrusted;
        this.link = link;
    }

    protected static MutableText getConfirmText(boolean linkTrusted, String link) {
        return ConfirmLinkScreen.getConfirmText((boolean)linkTrusted).append(ScreenTexts.SPACE).append((Text)Text.literal((String)link));
    }

    protected static MutableText getConfirmText(boolean linkTrusted) {
        return Text.translatable((String)(linkTrusted ? "chat.link.confirmTrusted" : "chat.link.confirm"));
    }

    protected void initExtras() {
        if (this.drawWarning) {
            this.layout.add((Widget)new TextWidget(WARNING, this.textRenderer));
        }
    }

    protected void addButtons(DirectionalLayoutWidget layout) {
        this.yesButton = (ButtonWidget)layout.add((Widget)ButtonWidget.builder((Text)this.yesText, button -> this.callback.accept(true)).width(100).build());
        layout.add((Widget)ButtonWidget.builder((Text)ScreenTexts.COPY, button -> {
            this.copyToClipboard();
            this.callback.accept(false);
        }).width(100).build());
        this.noButton = (ButtonWidget)layout.add((Widget)ButtonWidget.builder((Text)this.noText, button -> this.callback.accept(false)).width(100).build());
    }

    public void copyToClipboard() {
        this.client.keyboard.setClipboard(this.link);
    }

    public static void open(Screen parent, String url, boolean linkTrusted) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.setScreen((Screen)new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getOperatingSystem().open(url);
            }
            minecraftClient.setScreen(parent);
        }, url, linkTrusted));
    }

    public static void open(Screen parent, URI uri, boolean linkTrusted) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.setScreen((Screen)new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getOperatingSystem().open(uri);
            }
            minecraftClient.setScreen(parent);
        }, uri.toString(), linkTrusted));
    }

    public static void open(Screen parent, URI uri) {
        ConfirmLinkScreen.open((Screen)parent, (URI)uri, (boolean)true);
    }

    public static void open(Screen parent, String url) {
        ConfirmLinkScreen.open((Screen)parent, (String)url, (boolean)true);
    }

    public static ButtonWidget.PressAction opening(Screen parent, String url, boolean linkTrusted) {
        return button -> ConfirmLinkScreen.open((Screen)parent, (String)url, (boolean)linkTrusted);
    }

    public static ButtonWidget.PressAction opening(Screen parent, URI uri, boolean linkTrusted) {
        return button -> ConfirmLinkScreen.open((Screen)parent, (URI)uri, (boolean)linkTrusted);
    }

    public static ButtonWidget.PressAction opening(Screen parent, String url) {
        return ConfirmLinkScreen.opening((Screen)parent, (String)url, (boolean)true);
    }

    public static ButtonWidget.PressAction opening(Screen parent, URI uri) {
        return ConfirmLinkScreen.opening((Screen)parent, (URI)uri, (boolean)true);
    }
}

