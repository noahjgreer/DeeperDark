/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.ProfileLookupCallback
 *  com.mojang.authlib.yggdrasil.ProfileNotFoundException
 */
package net.minecraft.server;

import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.util.UUID;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;

static class ServerConfigHandler.5
implements ProfileLookupCallback {
    final /* synthetic */ MinecraftDedicatedServer field_14341;
    final /* synthetic */ File field_14342;
    final /* synthetic */ File field_14339;
    final /* synthetic */ File field_14338;
    final /* synthetic */ String[] field_14340;

    ServerConfigHandler.5() {
        this.field_14341 = minecraftDedicatedServer;
        this.field_14342 = file;
        this.field_14339 = file2;
        this.field_14338 = file3;
        this.field_14340 = strings;
    }

    public void onProfileLookupSucceeded(String string, UUID uUID) {
        PlayerConfigEntry playerConfigEntry = new PlayerConfigEntry(uUID, string);
        this.field_14341.getApiServices().nameToIdCache().add(playerConfigEntry);
        this.convertPlayerFile(this.field_14342, this.getPlayerFileName(string), uUID.toString());
    }

    public void onProfileLookupFailed(String string, Exception exception) {
        LOGGER.warn("Could not lookup user uuid for {}", (Object)string, (Object)exception);
        if (!(exception instanceof ProfileNotFoundException)) {
            throw new ServerConfigHandler.ServerConfigException("Could not request user " + string + " from backend systems", exception);
        }
        String string2 = this.getPlayerFileName(string);
        this.convertPlayerFile(this.field_14339, string2, string2);
    }

    private void convertPlayerFile(File playerDataFolder, String fileName, String uuid) {
        File file = new File(this.field_14338, fileName + ".dat");
        File file2 = new File(playerDataFolder, uuid + ".dat");
        ServerConfigHandler.createDirectory(playerDataFolder);
        if (!file.renameTo(file2)) {
            throw new ServerConfigHandler.ServerConfigException("Could not convert file for " + fileName);
        }
    }

    private String getPlayerFileName(String string) {
        String string2 = null;
        for (String string3 : this.field_14340) {
            if (string3 == null || !string3.equalsIgnoreCase(string)) continue;
            string2 = string3;
            break;
        }
        if (string2 == null) {
            throw new ServerConfigHandler.ServerConfigException("Could not find the filename for " + string + " anymore");
        }
        return string2;
    }
}
