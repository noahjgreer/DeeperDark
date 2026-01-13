/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import java.util.UUID;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsText;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class RealmsNotification.InfoPopup
extends RealmsNotification {
    private static final String TITLE_KEY = "title";
    private static final String MESSAGE_KEY = "message";
    private static final String IMAGE_KEY = "image";
    private static final String URL_BUTTON_KEY = "urlButton";
    private final RealmsText title;
    private final RealmsText message;
    private final Identifier image;
    private final @Nullable RealmsNotification.UrlButton urlButton;

    private RealmsNotification.InfoPopup(RealmsNotification parent, RealmsText title, RealmsText message, Identifier image, @Nullable RealmsNotification.UrlButton urlButton) {
        super(parent.uuid, parent.dismissable, parent.seen, parent.type);
        this.title = title;
        this.message = message;
        this.image = image;
        this.urlButton = urlButton;
    }

    public static RealmsNotification.InfoPopup fromJson(RealmsNotification parent, JsonObject json) {
        RealmsText realmsText = JsonUtils.get(TITLE_KEY, json, RealmsText::fromJson);
        RealmsText realmsText2 = JsonUtils.get(MESSAGE_KEY, json, RealmsText::fromJson);
        Identifier identifier = Identifier.of(JsonUtils.getString(IMAGE_KEY, json));
        RealmsNotification.UrlButton urlButton = JsonUtils.getNullable(URL_BUTTON_KEY, json, RealmsNotification.UrlButton::fromJson);
        return new RealmsNotification.InfoPopup(parent, realmsText, realmsText2, identifier, urlButton);
    }

    public @Nullable PopupScreen createScreen(Screen backgroundScreen, Consumer<UUID> dismissCallback) {
        Text text = this.title.toText();
        if (text == null) {
            LOGGER.warn("Realms info popup had title with no available translation: {}", (Object)this.title);
            return null;
        }
        PopupScreen.Builder builder = new PopupScreen.Builder(backgroundScreen, text).image(this.image).message(this.message.toText(ScreenTexts.EMPTY));
        if (this.urlButton != null) {
            builder.button(this.urlButton.urlText.toText(OPEN_LINK_TEXT), screen -> {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                minecraftClient.setScreen(new ConfirmLinkScreen(confirmed -> {
                    if (confirmed) {
                        Util.getOperatingSystem().open(this.urlButton.url);
                        minecraftClient.setScreen(backgroundScreen);
                    } else {
                        minecraftClient.setScreen((Screen)screen);
                    }
                }, this.urlButton.url, true));
                dismissCallback.accept(this.getUuid());
            });
        }
        builder.button(ScreenTexts.OK, screen -> {
            screen.close();
            dismissCallback.accept(this.getUuid());
        });
        builder.onClosed(() -> dismissCallback.accept(this.getUuid()));
        return builder.build();
    }
}
