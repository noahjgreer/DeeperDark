/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.util.HashedWheelTimer
 *  io.netty.util.Timeout
 *  io.netty.util.Timer
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import java.util.List;
import java.util.concurrent.TimeUnit;

static class ServerNetworkIo.DelayingChannelInboundHandler
extends ChannelInboundHandlerAdapter {
    private static final Timer TIMER = new HashedWheelTimer();
    private final int baseDelay;
    private final int extraDelay;
    private final List<Packet> packets = Lists.newArrayList();

    public ServerNetworkIo.DelayingChannelInboundHandler(int baseDelay, int extraDelay) {
        this.baseDelay = baseDelay;
        this.extraDelay = extraDelay;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        this.delay(ctx, msg);
    }

    private void delay(ChannelHandlerContext ctx, Object msg) {
        int i = this.baseDelay + (int)(Math.random() * (double)this.extraDelay);
        this.packets.add(new Packet(ctx, msg));
        TIMER.newTimeout(this::forward, (long)i, TimeUnit.MILLISECONDS);
    }

    private void forward(Timeout timeout) {
        Packet packet = this.packets.remove(0);
        packet.context.fireChannelRead(packet.message);
    }

    static class Packet {
        public final ChannelHandlerContext context;
        public final Object message;

        public Packet(ChannelHandlerContext context, Object message) {
            this.context = context;
            this.message = message;
        }
    }
}
