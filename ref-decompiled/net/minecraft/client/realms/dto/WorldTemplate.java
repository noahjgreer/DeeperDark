/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.dto.WorldTemplate
 *  net.minecraft.client.realms.dto.WorldTemplate$WorldTemplateType
 *  net.minecraft.client.realms.util.JsonUtils
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.util.JsonUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record WorldTemplate(String id, String name, String version, String author, String link, @Nullable String image, String trailer, String recommendedPlayers, WorldTemplateType type) {
    private final String id;
    private final String name;
    private final String version;
    private final String author;
    private final String link;
    private final @Nullable String image;
    private final String trailer;
    private final String recommendedPlayers;
    private final WorldTemplateType type;
    private static final Logger LOGGER = LogUtils.getLogger();

    public WorldTemplate(String id, String name, String version, String author, String link, @Nullable String image, String trailer, String recommendedPlayers, WorldTemplateType type) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.author = author;
        this.link = link;
        this.image = image;
        this.trailer = trailer;
        this.recommendedPlayers = recommendedPlayers;
        this.type = type;
    }

    public static @Nullable WorldTemplate parse(JsonObject node) {
        try {
            String string = JsonUtils.getNullableStringOr((String)"type", (JsonObject)node, null);
            return new WorldTemplate(JsonUtils.getNullableStringOr((String)"id", (JsonObject)node, (String)""), JsonUtils.getNullableStringOr((String)"name", (JsonObject)node, (String)""), JsonUtils.getNullableStringOr((String)"version", (JsonObject)node, (String)""), JsonUtils.getNullableStringOr((String)"author", (JsonObject)node, (String)""), JsonUtils.getNullableStringOr((String)"link", (JsonObject)node, (String)""), JsonUtils.getNullableStringOr((String)"image", (JsonObject)node, null), JsonUtils.getNullableStringOr((String)"trailer", (JsonObject)node, (String)""), JsonUtils.getNullableStringOr((String)"recommendedPlayers", (JsonObject)node, (String)""), string == null ? WorldTemplateType.WORLD_TEMPLATE : WorldTemplateType.valueOf((String)string));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse WorldTemplate", (Throwable)exception);
            return null;
        }
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public String version() {
        return this.version;
    }

    public String author() {
        return this.author;
    }

    public String link() {
        return this.link;
    }

    public @Nullable String image() {
        return this.image;
    }

    public String trailer() {
        return this.trailer;
    }

    public String recommendedPlayers() {
        return this.recommendedPlayers;
    }

    public WorldTemplateType type() {
        return this.type;
    }
}

