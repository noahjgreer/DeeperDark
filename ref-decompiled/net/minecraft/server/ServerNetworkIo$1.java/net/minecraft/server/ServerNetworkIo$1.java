/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.ChannelPipeline
 *  io.netty.handler.timeout.ReadTimeoutHandler
 */
package net.minecraft.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.RateLimitedConnection;
import net.minecraft.network.handler.LegacyQueryHandler;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;

class ServerNetworkIo.1
extends ChannelInitializer<Channel> {
    ServerNetworkIo.1() {
    }

    protected void initChannel(Channel channel) {
        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
        }
        catch (ChannelException channelException) {
            // empty catch block
        }
        ChannelPipeline channelPipeline = channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30));
        if (ServerNetworkIo.this.server.acceptsStatusQuery()) {
            channelPipeline.addLast("legacy_query", (ChannelHandler)new LegacyQueryHandler(ServerNetworkIo.this.getServer()));
        }
        ClientConnection.addHandlers(channelPipeline, NetworkSide.SERVERBOUND, false, null);
        int i = ServerNetworkIo.this.server.getRateLimit();
        ClientConnection clientConnection = i > 0 ? new RateLimitedConnection(i) : new ClientConnection(NetworkSide.SERVERBOUND);
        ServerNetworkIo.this.connections.add(clientConnection);
        clientConnection.addFlowControlHandler(channelPipeline);
        clientConnection.setInitialPacketListener(new ServerHandshakeNetworkHandler(ServerNetworkIo.this.server, clientConnection));
    }
}
