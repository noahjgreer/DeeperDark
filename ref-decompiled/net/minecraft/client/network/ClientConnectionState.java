/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.network.ClientConnectionState
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.session.telemetry.WorldSession
 *  net.minecraft.client.world.ClientChunkLoadProgress
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 *  net.minecraft.resource.featuretoggle.FeatureSet
 *  net.minecraft.server.ServerLinks
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.client.world.ClientChunkLoadProgress;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.ServerLinks;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ClientConnectionState(ClientChunkLoadProgress chunkLoadProgress, GameProfile localGameProfile, WorldSession worldSession, DynamicRegistryManager.Immutable receivedRegistries, FeatureSet enabledFeatures, @Nullable String serverBrand, @Nullable ServerInfo serverInfo, @Nullable Screen postDisconnectScreen, Map<Identifier, byte[]> serverCookies, // Could not load outer class - annotation placement on inner may be incorrect
 @Nullable ChatHud.ChatState chatState, Map<String, String> customReportDetails, ServerLinks serverLinks, Map<UUID, PlayerListEntry> seenPlayers, boolean seenInsecureChatWarning) {
    private final ClientChunkLoadProgress chunkLoadProgress;
    private final GameProfile localGameProfile;
    private final WorldSession worldSession;
    private final DynamicRegistryManager.Immutable receivedRegistries;
    private final FeatureSet enabledFeatures;
    private final @Nullable String serverBrand;
    private final @Nullable ServerInfo serverInfo;
    private final @Nullable Screen postDisconnectScreen;
    private final Map<Identifier, byte[]> serverCookies;
    private final // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ChatHud.ChatState chatState;
    private final Map<String, String> customReportDetails;
    private final ServerLinks serverLinks;
    private final Map<UUID, PlayerListEntry> seenPlayers;
    private final boolean seenInsecureChatWarning;

    public ClientConnectionState(ClientChunkLoadProgress chunkLoadProgress, GameProfile localGameProfile, WorldSession worldSession, DynamicRegistryManager.Immutable receivedRegistries, FeatureSet enabledFeatures, @Nullable String serverBrand, @Nullable ServerInfo serverInfo, @Nullable Screen postDisconnectScreen, Map<Identifier, byte[]> serverCookies, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ChatHud.ChatState chatState, Map<String, String> customReportDetails, ServerLinks serverLinks, Map<UUID, PlayerListEntry> seenPlayers, boolean seenInsecureChatWarning) {
        this.chunkLoadProgress = chunkLoadProgress;
        this.localGameProfile = localGameProfile;
        this.worldSession = worldSession;
        this.receivedRegistries = receivedRegistries;
        this.enabledFeatures = enabledFeatures;
        this.serverBrand = serverBrand;
        this.serverInfo = serverInfo;
        this.postDisconnectScreen = postDisconnectScreen;
        this.serverCookies = serverCookies;
        this.chatState = chatState;
        this.customReportDetails = customReportDetails;
        this.serverLinks = serverLinks;
        this.seenPlayers = seenPlayers;
        this.seenInsecureChatWarning = seenInsecureChatWarning;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ClientConnectionState.class, "levelLoadTracker;localGameProfile;telemetryManager;receivedRegistries;enabledFeatures;serverBrand;serverData;postDisconnectScreen;serverCookies;chatState;customReportDetails;serverLinks;seenPlayers;seenInsecureChatWarning", "chunkLoadProgress", "localGameProfile", "worldSession", "receivedRegistries", "enabledFeatures", "serverBrand", "serverInfo", "postDisconnectScreen", "serverCookies", "chatState", "customReportDetails", "serverLinks", "seenPlayers", "seenInsecureChatWarning"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ClientConnectionState.class, "levelLoadTracker;localGameProfile;telemetryManager;receivedRegistries;enabledFeatures;serverBrand;serverData;postDisconnectScreen;serverCookies;chatState;customReportDetails;serverLinks;seenPlayers;seenInsecureChatWarning", "chunkLoadProgress", "localGameProfile", "worldSession", "receivedRegistries", "enabledFeatures", "serverBrand", "serverInfo", "postDisconnectScreen", "serverCookies", "chatState", "customReportDetails", "serverLinks", "seenPlayers", "seenInsecureChatWarning"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ClientConnectionState.class, "levelLoadTracker;localGameProfile;telemetryManager;receivedRegistries;enabledFeatures;serverBrand;serverData;postDisconnectScreen;serverCookies;chatState;customReportDetails;serverLinks;seenPlayers;seenInsecureChatWarning", "chunkLoadProgress", "localGameProfile", "worldSession", "receivedRegistries", "enabledFeatures", "serverBrand", "serverInfo", "postDisconnectScreen", "serverCookies", "chatState", "customReportDetails", "serverLinks", "seenPlayers", "seenInsecureChatWarning"}, this, object);
    }

    public ClientChunkLoadProgress chunkLoadProgress() {
        return this.chunkLoadProgress;
    }

    public GameProfile localGameProfile() {
        return this.localGameProfile;
    }

    public WorldSession worldSession() {
        return this.worldSession;
    }

    public DynamicRegistryManager.Immutable receivedRegistries() {
        return this.receivedRegistries;
    }

    public FeatureSet enabledFeatures() {
        return this.enabledFeatures;
    }

    public @Nullable String serverBrand() {
        return this.serverBrand;
    }

    public @Nullable ServerInfo serverInfo() {
        return this.serverInfo;
    }

    public @Nullable Screen postDisconnectScreen() {
        return this.postDisconnectScreen;
    }

    public Map<Identifier, byte[]> serverCookies() {
        return this.serverCookies;
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ChatHud.ChatState chatState() {
        return this.chatState;
    }

    public Map<String, String> customReportDetails() {
        return this.customReportDetails;
    }

    public ServerLinks serverLinks() {
        return this.serverLinks;
    }

    public Map<UUID, PlayerListEntry> seenPlayers() {
        return this.seenPlayers;
    }

    public boolean seenInsecureChatWarning() {
        return this.seenInsecureChatWarning;
    }
}

