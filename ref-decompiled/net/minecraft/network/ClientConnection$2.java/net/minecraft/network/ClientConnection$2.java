/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelOutboundHandlerAdapter
 *  io.netty.channel.ChannelPromise
 */
package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.ClientConnection;

class ClientConnection.2
extends ChannelOutboundHandlerAdapter {
    ClientConnection.2(ClientConnection clientConnection) {
    }

    public void write(ChannelHandlerContext context, Object value, ChannelPromise promise) throws Exception {
        super.write(context, value, promise);
    }
}
