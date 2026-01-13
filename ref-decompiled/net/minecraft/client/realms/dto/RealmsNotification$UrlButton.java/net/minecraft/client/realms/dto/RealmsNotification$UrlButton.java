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
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsText;
import net.minecraft.client.realms.util.JsonUtils;

@Environment(value=EnvType.CLIENT)
static final class RealmsNotification.UrlButton
extends Record {
    final String url;
    final RealmsText urlText;
    private static final String URL_KEY = "url";
    private static final String URL_TEXT_KEY = "urlText";

    private RealmsNotification.UrlButton(String url, RealmsText urlText) {
        this.url = url;
        this.urlText = urlText;
    }

    public static RealmsNotification.UrlButton fromJson(JsonObject json) {
        String string = JsonUtils.getString(URL_KEY, json);
        RealmsText realmsText = JsonUtils.get(URL_TEXT_KEY, json, RealmsText::fromJson);
        return new RealmsNotification.UrlButton(string, realmsText);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RealmsNotification.UrlButton.class, "url;urlText", "url", "urlText"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RealmsNotification.UrlButton.class, "url;urlText", "url", "urlText"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RealmsNotification.UrlButton.class, "url;urlText", "url", "urlText"}, this, object);
    }

    public String url() {
        return this.url;
    }

    public RealmsText urlText() {
        return this.urlText;
    }
}
