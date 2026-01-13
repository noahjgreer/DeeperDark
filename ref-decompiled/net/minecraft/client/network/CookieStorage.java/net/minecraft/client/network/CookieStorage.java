/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
}
