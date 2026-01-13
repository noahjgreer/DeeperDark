/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.dto.WorldDownload
 *  net.minecraft.client.realms.util.JsonUtils
 *  net.minecraft.util.LenientJsonParser
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record WorldDownload(String downloadLink, String resourcePackUrl, String resourcePackHash) {
    private final String downloadLink;
    private final String resourcePackUrl;
    private final String resourcePackHash;
    private static final Logger LOGGER = LogUtils.getLogger();

    public WorldDownload(String downloadLink, String resourcePackUrl, String resourcePackHash) {
        this.downloadLink = downloadLink;
        this.resourcePackUrl = resourcePackUrl;
        this.resourcePackHash = resourcePackHash;
    }

    public static WorldDownload parse(String json) {
        JsonObject jsonObject = LenientJsonParser.parse((String)json).getAsJsonObject();
        try {
            return new WorldDownload(JsonUtils.getNullableStringOr((String)"downloadLink", (JsonObject)jsonObject, (String)""), JsonUtils.getNullableStringOr((String)"resourcePackUrl", (JsonObject)jsonObject, (String)""), JsonUtils.getNullableStringOr((String)"resourcePackHash", (JsonObject)jsonObject, (String)""));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse WorldDownload", (Throwable)exception);
            return new WorldDownload("", "", "");
        }
    }

    public String downloadLink() {
        return this.downloadLink;
    }

    public String resourcePackUrl() {
        return this.resourcePackUrl;
    }

    public String resourcePackHash() {
        return this.resourcePackHash;
    }
}

