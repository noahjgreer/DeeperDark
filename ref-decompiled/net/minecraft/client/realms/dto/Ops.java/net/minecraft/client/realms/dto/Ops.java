/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.HashSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record Ops(Set<String> ops) {
    private static final Logger field_63456 = LogUtils.getLogger();

    public static Ops parse(String json) {
        HashSet<String> set = new HashSet<String>();
        try {
            JsonObject jsonObject = LenientJsonParser.parse(json).getAsJsonObject();
            JsonElement jsonElement = jsonObject.get("ops");
            if (jsonElement.isJsonArray()) {
                for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
                    set.add(jsonElement2.getAsString());
                }
            }
        }
        catch (Exception exception) {
            field_63456.error("Could not parse Ops", (Throwable)exception);
        }
        return new Ops(set);
    }
}
