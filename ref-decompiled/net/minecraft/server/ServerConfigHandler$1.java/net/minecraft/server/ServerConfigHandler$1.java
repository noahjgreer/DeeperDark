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
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.ServerConfigHandler;

static class ServerConfigHandler.1
implements ProfileLookupCallback {
    final /* synthetic */ MinecraftServer field_14331;
    final /* synthetic */ Map field_14330;
    final /* synthetic */ BannedPlayerList field_14329;

    ServerConfigHandler.1() {
        this.field_14331 = minecraftServer;
        this.field_14330 = map;
        this.field_14329 = bannedPlayerList;
    }

    public void onProfileLookupSucceeded(String string, UUID uUID) {
        PlayerConfigEntry playerConfigEntry = new PlayerConfigEntry(uUID, string);
        this.field_14331.getApiServices().nameToIdCache().add(playerConfigEntry);
        String[] strings = (String[])this.field_14330.get(playerConfigEntry.name().toLowerCase(Locale.ROOT));
        if (strings == null) {
            LOGGER.warn("Could not convert user banlist entry for {}", (Object)playerConfigEntry.name());
            throw new ServerConfigHandler.ServerConfigException("Profile not in the conversionlist");
        }
        Date date = strings.length > 1 ? ServerConfigHandler.parseDate(strings[1], null) : null;
        String string2 = strings.length > 2 ? strings[2] : null;
        Date date2 = strings.length > 3 ? ServerConfigHandler.parseDate(strings[3], null) : null;
        String string3 = strings.length > 4 ? strings[4] : null;
        this.field_14329.add(new BannedPlayerEntry(playerConfigEntry, date, string2, date2, string3));
    }

    public void onProfileLookupFailed(String string, Exception exception) {
        LOGGER.warn("Could not lookup user banlist entry for {}", (Object)string, (Object)exception);
        if (!(exception instanceof ProfileNotFoundException)) {
            throw new ServerConfigHandler.ServerConfigException("Could not request user " + string + " from backend systems", exception);
        }
    }
}
