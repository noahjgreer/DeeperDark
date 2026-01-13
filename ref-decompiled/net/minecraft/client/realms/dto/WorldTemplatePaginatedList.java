/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.dto.WorldTemplate
 *  net.minecraft.client.realms.dto.WorldTemplatePaginatedList
 *  net.minecraft.client.realms.util.JsonUtils
 *  net.minecraft.util.LenientJsonParser
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record WorldTemplatePaginatedList(List<WorldTemplate> templates, int page, int size, int total) {
    private final List<WorldTemplate> templates;
    private final int page;
    private final int size;
    private final int total;
    private static final Logger LOGGER = LogUtils.getLogger();

    public WorldTemplatePaginatedList(int size) {
        this(List.of(), 0, size, -1);
    }

    public WorldTemplatePaginatedList(List<WorldTemplate> templates, int page, int size, int total) {
        this.templates = templates;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public boolean isLastPage() {
        return this.page * this.size >= this.total && this.page > 0 && this.total > 0 && this.size > 0;
    }

    public static WorldTemplatePaginatedList parse(String json) {
        ArrayList<WorldTemplate> list = new ArrayList<WorldTemplate>();
        int i = 0;
        int j = 0;
        int k = 0;
        try {
            JsonObject jsonObject = LenientJsonParser.parse((String)json).getAsJsonObject();
            if (jsonObject.get("templates").isJsonArray()) {
                for (JsonElement jsonElement : jsonObject.get("templates").getAsJsonArray()) {
                    WorldTemplate worldTemplate = WorldTemplate.parse((JsonObject)jsonElement.getAsJsonObject());
                    if (worldTemplate == null) continue;
                    list.add(worldTemplate);
                }
            }
            i = JsonUtils.getIntOr((String)"page", (JsonObject)jsonObject, (int)0);
            j = JsonUtils.getIntOr((String)"size", (JsonObject)jsonObject, (int)0);
            k = JsonUtils.getIntOr((String)"total", (JsonObject)jsonObject, (int)0);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse WorldTemplatePaginatedList", (Throwable)exception);
        }
        return new WorldTemplatePaginatedList(list, i, j, k);
    }

    public List<WorldTemplate> templates() {
        return this.templates;
    }

    public int page() {
        return this.page;
    }

    public int size() {
        return this.size;
    }

    public int total() {
        return this.total;
    }
}

