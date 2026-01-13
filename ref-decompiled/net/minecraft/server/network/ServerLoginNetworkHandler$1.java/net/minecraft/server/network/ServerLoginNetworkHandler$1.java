/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import org.jspecify.annotations.Nullable;

class ServerLoginNetworkHandler.1
extends Thread {
    final /* synthetic */ String field_26900;

    ServerLoginNetworkHandler.1(String string, String string2) {
        this.field_26900 = string2;
        super(string);
    }

    @Override
    public void run() {
        String string = Objects.requireNonNull(ServerLoginNetworkHandler.this.profileName, "Player name not initialized");
        try {
            ProfileResult profileResult = ServerLoginNetworkHandler.this.server.getApiServices().sessionService().hasJoinedServer(string, this.field_26900, this.getClientAddress());
            if (profileResult != null) {
                GameProfile gameProfile = profileResult.profile();
                LOGGER.info("UUID of player {} is {}", (Object)gameProfile.name(), (Object)gameProfile.id());
                ServerLoginNetworkHandler.this.activityNotifier.notifyListenersWithRateLimit();
                ServerLoginNetworkHandler.this.startVerify(gameProfile);
            } else if (ServerLoginNetworkHandler.this.server.isSingleplayer()) {
                LOGGER.warn("Failed to verify username but will let them in anyway!");
                ServerLoginNetworkHandler.this.startVerify(Uuids.getOfflinePlayerProfile(string));
            } else {
                ServerLoginNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.unverified_username"));
                LOGGER.error("Username '{}' tried to join with an invalid session", (Object)string);
            }
        }
        catch (AuthenticationUnavailableException authenticationUnavailableException) {
            if (ServerLoginNetworkHandler.this.server.isSingleplayer()) {
                LOGGER.warn("Authentication servers are down but will let them in anyway!");
                ServerLoginNetworkHandler.this.startVerify(Uuids.getOfflinePlayerProfile(string));
            }
            ServerLoginNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.authservers_down"));
            LOGGER.error("Couldn't verify username because servers are unavailable");
        }
    }

    private @Nullable InetAddress getClientAddress() {
        SocketAddress socketAddress = ServerLoginNetworkHandler.this.connection.getAddress();
        return ServerLoginNetworkHandler.this.server.shouldPreventProxyConnections() && socketAddress instanceof InetSocketAddress ? ((InetSocketAddress)socketAddress).getAddress() : null;
    }
}
