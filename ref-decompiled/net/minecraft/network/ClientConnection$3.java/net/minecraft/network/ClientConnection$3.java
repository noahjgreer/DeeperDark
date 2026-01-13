/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelPipeline
 */
package net.minecraft.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;

static class ClientConnection.3
extends ChannelInitializer<Channel> {
    final /* synthetic */ ClientConnection field_48517;

    ClientConnection.3(ClientConnection clientConnection) {
        this.field_48517 = clientConnection;
    }

    protected void initChannel(Channel channel) {
        ChannelPipeline channelPipeline = channel.pipeline();
        ClientConnection.addLocalValidator(channelPipeline, NetworkSide.CLIENTBOUND);
        this.field_48517.addFlowControlHandler(channelPipeline);
    }
}
