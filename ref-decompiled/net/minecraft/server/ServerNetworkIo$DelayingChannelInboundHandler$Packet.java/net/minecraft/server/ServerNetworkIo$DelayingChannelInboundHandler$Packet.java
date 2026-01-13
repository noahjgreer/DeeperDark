/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 */
package net.minecraft.server;

import io.netty.channel.ChannelHandlerContext;

static class ServerNetworkIo.DelayingChannelInboundHandler.Packet {
    public final ChannelHandlerContext context;
    public final Object message;

    public ServerNetworkIo.DelayingChannelInboundHandler.Packet(ChannelHandlerContext context, Object message) {
        this.context = context;
        this.message = message;
    }
}
