/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.realms.CheckedGson
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.util.RealmsPersistence
 *  net.minecraft.client.realms.util.RealmsPersistence$RealmsPersistenceData
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.util;

import com.mojang.logging.LogUtils;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.util.RealmsPersistence;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsPersistence {
    private static final String FILE_NAME = "realms_persistence.json";
    private static final CheckedGson CHECKED_GSON = new CheckedGson();
    private static final Logger LOGGER = LogUtils.getLogger();

    public RealmsPersistenceData load() {
        return RealmsPersistence.readFile();
    }

    public void save(RealmsPersistenceData data) {
        RealmsPersistence.writeFile((RealmsPersistenceData)data);
    }

    public static RealmsPersistenceData readFile() {
        Path path = RealmsPersistence.getFile();
        try {
            String string = Files.readString(path, StandardCharsets.UTF_8);
            RealmsPersistenceData realmsPersistenceData = (RealmsPersistenceData)CHECKED_GSON.fromJson(string, RealmsPersistenceData.class);
            if (realmsPersistenceData != null) {
                return realmsPersistenceData;
            }
        }
        catch (NoSuchFileException string) {
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to read Realms storage {}", (Object)path, (Object)exception);
        }
        return new RealmsPersistenceData();
    }

    public static void writeFile(RealmsPersistenceData data) {
        Path path = RealmsPersistence.getFile();
        try {
            Files.writeString(path, (CharSequence)CHECKED_GSON.toJson((RealmsSerializable)data), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static Path getFile() {
        return MinecraftClient.getInstance().runDirectory.toPath().resolve("realms_persistence.json");
    }
}

