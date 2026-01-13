/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record WorldTemplate(String id, String name, String version, String author, String link, @Nullable String image, String trailer, String recommendedPlayers, WorldTemplateType type) {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static @Nullable WorldTemplate parse(JsonObject node) {
        try {
            String string = JsonUtils.getNullableStringOr("type", node, null);
            return new WorldTemplate(JsonUtils.getNullableStringOr("id", node, ""), JsonUtils.getNullableStringOr("name", node, ""), JsonUtils.getNullableStringOr("version", node, ""), JsonUtils.getNullableStringOr("author", node, ""), JsonUtils.getNullableStringOr("link", node, ""), JsonUtils.getNullableStringOr("image", node, null), JsonUtils.getNullableStringOr("trailer", node, ""), JsonUtils.getNullableStringOr("recommendedPlayers", node, ""), string == null ? WorldTemplateType.WORLD_TEMPLATE : WorldTemplateType.valueOf(string));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse WorldTemplate", (Throwable)exception);
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class WorldTemplateType
    extends Enum<WorldTemplateType> {
        public static final /* enum */ WorldTemplateType WORLD_TEMPLATE = new WorldTemplateType();
        public static final /* enum */ WorldTemplateType MINIGAME = new WorldTemplateType();
        public static final /* enum */ WorldTemplateType ADVENTUREMAP = new WorldTemplateType();
        public static final /* enum */ WorldTemplateType EXPERIENCE = new WorldTemplateType();
        public static final /* enum */ WorldTemplateType INSPIRATION = new WorldTemplateType();
        private static final /* synthetic */ WorldTemplateType[] field_19452;

        public static WorldTemplateType[] values() {
            return (WorldTemplateType[])field_19452.clone();
        }

        public static WorldTemplateType valueOf(String name) {
            return Enum.valueOf(WorldTemplateType.class, name);
        }

        private static /* synthetic */ WorldTemplateType[] method_36851() {
            return new WorldTemplateType[]{WORLD_TEMPLATE, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION};
        }

        static {
            field_19452 = WorldTemplateType.method_36851();
        }
    }
}
