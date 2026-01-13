/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.dto.Backup
 *  net.minecraft.client.realms.dto.BackupList
 *  net.minecraft.util.LenientJsonParser
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record BackupList(List<Backup> backups) {
    private final List<Backup> backups;
    private static final Logger LOGGER = LogUtils.getLogger();

    public BackupList(List<Backup> backups) {
        this.backups = backups;
    }

    public static BackupList parse(String json) {
        ArrayList<Backup> list = new ArrayList<Backup>();
        try {
            JsonElement jsonElement = LenientJsonParser.parse((String)json).getAsJsonObject().get("backups");
            if (jsonElement.isJsonArray()) {
                for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
                    Backup backup = Backup.parse((JsonElement)jsonElement2);
                    if (backup == null) continue;
                    list.add(backup);
                }
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse BackupList", (Throwable)exception);
        }
        return new BackupList(list);
    }

    public List<Backup> backups() {
        return this.backups;
    }
}

