/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.ProfileLookupCallback
 */
package net.minecraft.server;

import com.mojang.authlib.ProfileLookupCallback;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;

static class ServerConfigHandler.4
implements ProfileLookupCallback {
    final /* synthetic */ MinecraftServer field_14336;
    final /* synthetic */ List field_14337;

    ServerConfigHandler.4() {
        this.field_14336 = minecraftServer;
        this.field_14337 = list;
    }

    public void onProfileLookupSucceeded(String string, UUID uUID) {
        PlayerConfigEntry playerConfigEntry = new PlayerConfigEntry(uUID, string);
        this.field_14336.getApiServices().nameToIdCache().add(playerConfigEntry);
        this.field_14337.add(playerConfigEntry);
    }

    public void onProfileLookupFailed(String string, Exception exception) {
        LOGGER.warn("Could not lookup user whitelist entry for {}", (Object)string, (Object)exception);
    }
}
