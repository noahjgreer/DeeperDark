/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.ClientPlayerProfileResolver
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.server.GameProfileResolver
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.server.GameProfileResolver;

@Environment(value=EnvType.CLIENT)
public class ClientPlayerProfileResolver
implements GameProfileResolver {
    private final MinecraftClient client;
    private final GameProfileResolver profileResolver;

    public ClientPlayerProfileResolver(MinecraftClient client, GameProfileResolver profileResolver) {
        this.client = client;
        this.profileResolver = profileResolver;
    }

    public Optional<GameProfile> getProfileByName(String name) {
        PlayerListEntry playerListEntry;
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null && (playerListEntry = clientPlayNetworkHandler.getCaseInsensitivePlayerInfo(name)) != null) {
            return Optional.of(playerListEntry.getProfile());
        }
        return this.profileResolver.getProfileByName(name);
    }

    public Optional<GameProfile> getProfileById(UUID id) {
        PlayerListEntry playerListEntry;
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null && (playerListEntry = clientPlayNetworkHandler.getPlayerListEntry(id)) != null) {
            return Optional.of(playerListEntry.getProfile());
        }
        return this.profileResolver.getProfileById(id);
    }
}

