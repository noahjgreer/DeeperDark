/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
public record ClientConnectionState(ClientChunkLoadProgress chunkLoadProgress, GameProfile localGameProfile, WorldSession worldSession, DynamicRegistryManager.Immutable receivedRegistries, FeatureSet enabledFeatures, @Nullable String serverBrand, @Nullable ServerInfo serverInfo, @Nullable Screen postDisconnectScreen, Map<Identifier, byte[]> serverCookies,  @Nullable ChatHud.ChatState chatState, Map<String, String> customReportDetails, ServerLinks serverLinks, Map<UUID, PlayerListEntry> seenPlayers, boolean seenInsecureChatWarning) {
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
}
