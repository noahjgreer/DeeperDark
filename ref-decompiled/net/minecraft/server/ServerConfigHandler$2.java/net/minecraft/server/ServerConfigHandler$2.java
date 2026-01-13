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
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.ServerConfigHandler;

static class ServerConfigHandler.2
implements ProfileLookupCallback {
    final /* synthetic */ MinecraftServer field_14332;
    final /* synthetic */ OperatorList field_14333;

    ServerConfigHandler.2() {
        this.field_14332 = minecraftServer;
        this.field_14333 = operatorList;
    }

    public void onProfileLookupSucceeded(String string, UUID uUID) {
        PlayerConfigEntry playerConfigEntry = new PlayerConfigEntry(uUID, string);
        this.field_14332.getApiServices().nameToIdCache().add(playerConfigEntry);
        this.field_14333.add(new OperatorEntry(playerConfigEntry, this.field_14332.getOpPermissionLevel(), false));
    }

    public void onProfileLookupFailed(String string, Exception exception) {
        LOGGER.warn("Could not lookup oplist entry for {}", (Object)string, (Object)exception);
        if (!(exception instanceof ProfileNotFoundException)) {
            throw new ServerConfigHandler.ServerConfigException("Could not request user " + string + " from backend systems", exception);
        }
    }
}
