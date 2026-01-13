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
package net.minecraft.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;

static class ClientConnection.1
extends ChannelInitializer<Channel> {
    final /* synthetic */ ClientConnection field_11663;

    ClientConnection.1(ClientConnection clientConnection) {
        this.field_11663 = clientConnection;
    }

    protected void initChannel(Channel channel) {
        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
        }
        catch (ChannelException channelException) {
            // empty catch block
        }
        ChannelPipeline channelPipeline = channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30));
        ClientConnection.addHandlers(channelPipeline, NetworkSide.CLIENTBOUND, false, this.field_11663.packetSizeLogger);
        this.field_11663.addFlowControlHandler(channelPipeline);
    }
}
