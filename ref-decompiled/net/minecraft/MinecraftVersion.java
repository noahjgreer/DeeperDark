/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  net.minecraft.GameVersion
 *  net.minecraft.GameVersion$Impl
 *  net.minecraft.MinecraftVersion
 *  net.minecraft.SaveVersion
 *  net.minecraft.SharedConstants
 *  net.minecraft.resource.PackVersion
 *  net.minecraft.util.JsonHelper
 *  org.slf4j.Logger
 */
package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.GameVersion;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;
import net.minecraft.resource.PackVersion;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
public class MinecraftVersion {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final GameVersion DEVELOPMENT = MinecraftVersion.create((String)UUID.randomUUID().toString().replaceAll("-", ""), (String)"Development Version");

    public static GameVersion create(String id, String name) {
        return MinecraftVersion.create((String)id, (String)name, (boolean)true);
    }

    public static GameVersion create(String id, String name, boolean stable) {
        return new GameVersion.Impl(id, name, new SaveVersion(4671, "main"), SharedConstants.getProtocolVersion(), PackVersion.of((int)75, (int)0), PackVersion.of((int)94, (int)1), new Date(), stable);
    }

    private static GameVersion fromJson(JsonObject json) {
        JsonObject jsonObject = JsonHelper.getObject((JsonObject)json, (String)"pack_version");
        return new GameVersion.Impl(JsonHelper.getString((JsonObject)json, (String)"id"), JsonHelper.getString((JsonObject)json, (String)"name"), new SaveVersion(JsonHelper.getInt((JsonObject)json, (String)"world_version"), JsonHelper.getString((JsonObject)json, (String)"series_id", (String)"main")), JsonHelper.getInt((JsonObject)json, (String)"protocol_version"), PackVersion.of((int)JsonHelper.getInt((JsonObject)jsonObject, (String)"resource_major"), (int)JsonHelper.getInt((JsonObject)jsonObject, (String)"resource_minor")), PackVersion.of((int)JsonHelper.getInt((JsonObject)jsonObject, (String)"data_major"), (int)JsonHelper.getInt((JsonObject)jsonObject, (String)"data_minor")), Date.from(ZonedDateTime.parse(JsonHelper.getString((JsonObject)json, (String)"build_time")).toInstant()), JsonHelper.getBoolean((JsonObject)json, (String)"stable"));
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static GameVersion create() {
        try (InputStream inputStream = MinecraftVersion.class.getResourceAsStream("/version.json");){
            GameVersion gameVersion;
            if (inputStream == null) {
                LOGGER.warn("Missing version information!");
                GameVersion gameVersion2 = DEVELOPMENT;
                return gameVersion2;
            }
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);){
                gameVersion = MinecraftVersion.fromJson((JsonObject)JsonHelper.deserialize((Reader)inputStreamReader));
            }
            return gameVersion;
        }
        catch (JsonParseException | IOException exception) {
            throw new IllegalStateException("Game version information is corrupt", exception);
        }
    }
}

