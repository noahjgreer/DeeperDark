/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelPipeline
 */
package net.minecraft.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import net.minecraft.SharedConstants;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.server.network.LocalServerHandshakeNetworkHandler;

class ServerNetworkIo.2
extends ChannelInitializer<Channel> {
    ServerNetworkIo.2() {
    }

    protected void initChannel(Channel channel) {
        ClientConnection clientConnection = new ClientConnection(NetworkSide.SERVERBOUND);
        clientConnection.setInitialPacketListener(new LocalServerHandshakeNetworkHandler(ServerNetworkIo.this.server, clientConnection));
        ServerNetworkIo.this.connections.add(clientConnection);
        ChannelPipeline channelPipeline = channel.pipeline();
        ClientConnection.addLocalValidator(channelPipeline, NetworkSide.SERVERBOUND);
        if (SharedConstants.FAKE_LATENCY_MS > 0) {
            channelPipeline.addLast("latency", (ChannelHandler)new ServerNetworkIo.DelayingChannelInboundHandler(SharedConstants.FAKE_LATENCY_MS, SharedConstants.FAKE_JITTER_MS));
        }
        clientConnection.addFlowControlHandler(channelPipeline);
    }
}
