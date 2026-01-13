/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.multiplayer;

import java.net.InetSocketAddress;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import net.minecraft.client.world.ClientChunkLoadProgress;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.state.LoginStates;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class ConnectScreen.1
extends Thread {
    final /* synthetic */ ServerAddress field_33737;
    final /* synthetic */ MinecraftClient field_33738;
    final /* synthetic */ ServerInfo field_40415;
    final /* synthetic */ CookieStorage field_48396;

    ConnectScreen.1(String string, ServerAddress serverAddress, MinecraftClient minecraftClient, ServerInfo serverInfo, CookieStorage cookieStorage) {
        this.field_33737 = serverAddress;
        this.field_33738 = minecraftClient;
        this.field_40415 = serverInfo;
        this.field_48396 = cookieStorage;
        super(string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        InetSocketAddress inetSocketAddress = null;
        try {
            ClientConnection clientConnection;
            if (ConnectScreen.this.connectingCancelled) {
                return;
            }
            Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(this.field_33737).map(Address::getInetSocketAddress);
            if (ConnectScreen.this.connectingCancelled) {
                return;
            }
            if (optional.isEmpty()) {
                this.field_33738.execute(() -> this.field_33738.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, ConnectScreen.this.failureErrorMessage, UNKNOWN_HOST_TEXT)));
                return;
            }
            inetSocketAddress = optional.get();
            ConnectScreen connectScreen = ConnectScreen.this;
            synchronized (connectScreen) {
                if (ConnectScreen.this.connectingCancelled) {
                    return;
                }
                clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
                clientConnection.resetPacketSizeLog(this.field_33738.getDebugHud().getPacketSizeLog());
                ConnectScreen.this.future = ClientConnection.connect(inetSocketAddress, NetworkingBackend.remote(this.field_33738.options.shouldUseNativeTransport()), clientConnection);
            }
            ConnectScreen.this.future.syncUninterruptibly();
            connectScreen = ConnectScreen.this;
            synchronized (connectScreen) {
                if (ConnectScreen.this.connectingCancelled) {
                    clientConnection.disconnect(ABORTED_TEXT);
                    return;
                }
                ConnectScreen.this.connection = clientConnection;
                this.field_33738.getServerResourcePackProvider().init(clientConnection, ConnectScreen.1.toAcceptanceStatus(this.field_40415.getResourcePackPolicy()));
            }
            ConnectScreen.this.connection.connect(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), LoginStates.C2S, LoginStates.S2C, new ClientLoginNetworkHandler(ConnectScreen.this.connection, this.field_33738, this.field_40415, ConnectScreen.this.parent, false, null, ConnectScreen.this::setStatus, new ClientChunkLoadProgress(), this.field_48396), this.field_48396 != null);
            ConnectScreen.this.connection.send(new LoginHelloC2SPacket(this.field_33738.getSession().getUsername(), this.field_33738.getSession().getUuidOrNull()));
        }
        catch (Exception exception) {
            Exception exception2;
            if (ConnectScreen.this.connectingCancelled) {
                return;
            }
            Throwable throwable = exception.getCause();
            Exception exception3 = throwable instanceof Exception ? (exception2 = (Exception)throwable) : exception;
            LOGGER.error("Couldn't connect to server", (Throwable)exception);
            String string = inetSocketAddress == null ? exception3.getMessage() : exception3.getMessage().replaceAll(inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort(), "").replaceAll(inetSocketAddress.toString(), "");
            this.field_33738.execute(() -> this.field_33738.setScreen(new DisconnectedScreen(ConnectScreen.this.parent, ConnectScreen.this.failureErrorMessage, (Text)Text.translatable("disconnect.genericReason", string))));
        }
    }

    private static ServerResourcePackManager.AcceptanceStatus toAcceptanceStatus(ServerInfo.ResourcePackPolicy policy) {
        return switch (policy) {
            default -> throw new MatchException(null, null);
            case ServerInfo.ResourcePackPolicy.ENABLED -> ServerResourcePackManager.AcceptanceStatus.ALLOWED;
            case ServerInfo.ResourcePackPolicy.DISABLED -> ServerResourcePackManager.AcceptanceStatus.DECLINED;
            case ServerInfo.ResourcePackPolicy.PROMPT -> ServerResourcePackManager.AcceptanceStatus.PENDING;
        };
    }
}
