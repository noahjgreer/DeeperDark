/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsText;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static class RealmsNotification.VisitUrl
extends RealmsNotification {
    private static final String URL_KEY = "url";
    private static final String BUTTON_TEXT_KEY = "buttonText";
    private static final String MESSAGE_KEY = "message";
    private final String url;
    private final RealmsText buttonText;
    private final RealmsText message;

    private RealmsNotification.VisitUrl(RealmsNotification parent, String url, RealmsText buttonText, RealmsText message) {
        super(parent.uuid, parent.dismissable, parent.seen, parent.type);
        this.url = url;
        this.buttonText = buttonText;
        this.message = message;
    }

    public static RealmsNotification.VisitUrl fromJson(RealmsNotification parent, JsonObject json) {
        String string = JsonUtils.getString(URL_KEY, json);
        RealmsText realmsText = JsonUtils.get(BUTTON_TEXT_KEY, json, RealmsText::fromJson);
        RealmsText realmsText2 = JsonUtils.get(MESSAGE_KEY, json, RealmsText::fromJson);
        return new RealmsNotification.VisitUrl(parent, string, realmsText, realmsText2);
    }

    public Text getDefaultMessage() {
        return this.message.toText(Text.translatable("mco.notification.visitUrl.message.default"));
    }

    public ButtonWidget createButton(Screen currentScreen) {
        Text text = this.buttonText.toText(OPEN_LINK_TEXT);
        return ButtonWidget.builder(text, ConfirmLinkScreen.opening(currentScreen, this.url)).build();
    }
}
