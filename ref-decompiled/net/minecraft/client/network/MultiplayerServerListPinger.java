/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.channel.ChannelHandler
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.multiplayer.ConnectScreen
 *  net.minecraft.client.network.Address
 *  net.minecraft.client.network.AllowedAddressResolver
 *  net.minecraft.client.network.MultiplayerServerListPinger
 *  net.minecraft.client.network.MultiplayerServerListPinger$1
 *  net.minecraft.client.network.ServerAddress
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.network.NetworkingBackend
 *  net.minecraft.network.listener.ClientQueryPacketListener
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerServerListPinger {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text CANNOT_CONNECT_TEXT = Text.translatable((String)"multiplayer.status.cannot_connect").withColor(-65536);
    private final List<ClientConnection> clientConnections = Collections.synchronizedList(Lists.newArrayList());

    public void add(ServerInfo entry, Runnable saver, Runnable pingCallback, NetworkingBackend backend) throws UnknownHostException {
        ServerAddress serverAddress = ServerAddress.parse((String)entry.address);
        Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(serverAddress).map(Address::getInetSocketAddress);
        if (optional.isEmpty()) {
            this.showError(ConnectScreen.UNKNOWN_HOST_TEXT, entry);
            return;
        }
        InetSocketAddress inetSocketAddress = optional.get();
        ClientConnection clientConnection = ClientConnection.connect((InetSocketAddress)inetSocketAddress, (NetworkingBackend)backend, null);
        this.clientConnections.add(clientConnection);
        entry.label = Text.translatable((String)"multiplayer.status.pinging");
        entry.playerListSummary = Collections.emptyList();
        1 clientQueryPacketListener = new /* Unavailable Anonymous Inner Class!! */;
        try {
            clientConnection.connect(serverAddress.getAddress(), serverAddress.getPort(), (ClientQueryPacketListener)clientQueryPacketListener);
            clientConnection.send((Packet)QueryRequestC2SPacket.INSTANCE);
        }
        catch (Throwable throwable) {
            LOGGER.error("Failed to ping server {}", (Object)serverAddress, (Object)throwable);
        }
    }

    void showError(Text error, ServerInfo info) {
        LOGGER.error("Can't ping {}: {}", (Object)info.address, (Object)error.getString());
        info.label = CANNOT_CONNECT_TEXT;
        info.playerCountLabel = ScreenTexts.EMPTY;
    }

    void ping(InetSocketAddress socketAddress, ServerAddress address, ServerInfo serverInfo, NetworkingBackend backend) {
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(backend.getEventLoopGroup())).handler((ChannelHandler)new /* Unavailable Anonymous Inner Class!! */)).channel(backend.getChannelClass())).connect(socketAddress.getAddress(), socketAddress.getPort());
    }

    public static Text createPlayerCountText(int current, int max) {
        MutableText text = Text.literal((String)Integer.toString(current)).formatted(Formatting.GRAY);
        MutableText text2 = Text.literal((String)Integer.toString(max)).formatted(Formatting.GRAY);
        return Text.translatable((String)"multiplayer.status.player_count", (Object[])new Object[]{text, text2}).formatted(Formatting.DARK_GRAY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        List list = this.clientConnections;
        synchronized (list) {
            Iterator iterator = this.clientConnections.iterator();
            while (iterator.hasNext()) {
                ClientConnection clientConnection = (ClientConnection)iterator.next();
                if (clientConnection.isOpen()) {
                    clientConnection.tick();
                    continue;
                }
                iterator.remove();
                clientConnection.handleDisconnection();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cancel() {
        List list = this.clientConnections;
        synchronized (list) {
            Iterator iterator = this.clientConnections.iterator();
            while (iterator.hasNext()) {
                ClientConnection clientConnection = (ClientConnection)iterator.next();
                if (!clientConnection.isOpen()) continue;
                iterator.remove();
                clientConnection.disconnect((Text)Text.translatable((String)"multiplayer.status.cancelled"));
            }
        }
    }
}

