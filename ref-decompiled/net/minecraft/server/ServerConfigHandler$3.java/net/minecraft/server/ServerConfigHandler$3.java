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
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;

static class ServerConfigHandler.3
implements ProfileLookupCallback {
    final /* synthetic */ MinecraftServer field_14335;
    final /* synthetic */ Whitelist field_14334;

    ServerConfigHandler.3() {
        this.field_14335 = minecraftServer;
        this.field_14334 = whitelist;
    }

    public void onProfileLookupSucceeded(String string, UUID uUID) {
        PlayerConfigEntry playerConfigEntry = new PlayerConfigEntry(uUID, string);
        this.field_14335.getApiServices().nameToIdCache().add(playerConfigEntry);
        this.field_14334.add(new WhitelistEntry(playerConfigEntry));
    }

    public void onProfileLookupFailed(String string, Exception exception) {
        LOGGER.warn("Could not lookup user whitelist entry for {}", (Object)string, (Object)exception);
        if (!(exception instanceof ProfileNotFoundException)) {
            throw new ServerConfigHandler.ServerConfigException("Could not request user " + string + " from backend systems", exception);
        }
    }
}
