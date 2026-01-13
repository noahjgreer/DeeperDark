/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.CookieStorage
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.network;

import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record CookieStorage(Map<Identifier, byte[]> cookies, Map<UUID, PlayerListEntry> seenPlayers, boolean seenInsecureChatWarning) {
    private final Map<Identifier, byte[]> cookies;
    private final Map<UUID, PlayerListEntry> seenPlayers;
    private final boolean seenInsecureChatWarning;

    public CookieStorage(Map<Identifier, byte[]> cookies, Map<UUID, PlayerListEntry> seenPlayers, boolean seenInsecureChatWarning) {
        this.cookies = cookies;
        this.seenPlayers = seenPlayers;
        this.seenInsecureChatWarning = seenInsecureChatWarning;
    }

    public Map<Identifier, byte[]> cookies() {
        return this.cookies;
    }

    public Map<UUID, PlayerListEntry> seenPlayers() {
        return this.seenPlayers;
    }

    public boolean seenInsecureChatWarning() {
        return this.seenInsecureChatWarning;
    }
}

