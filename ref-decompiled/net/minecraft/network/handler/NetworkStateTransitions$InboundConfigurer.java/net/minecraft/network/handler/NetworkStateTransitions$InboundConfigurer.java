/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 *  io.netty.handler.codec.DecoderException
 *  io.netty.util.ReferenceCountUtil
 */
package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.DecoderException;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.network.handler.NetworkStateTransitions;
import net.minecraft.network.packet.Packet;

public static class NetworkStateTransitions.InboundConfigurer
extends ChannelDuplexHandler {
    public void channelRead(ChannelHandlerContext context, Object received) {
        if (received instanceof ByteBuf || received instanceof Packet) {
            ReferenceCountUtil.release((Object)received);
            throw new DecoderException("Pipeline has no inbound protocol configured, can't process packet " + String.valueOf(received));
        }
        context.fireChannelRead(received);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(ChannelHandlerContext context, Object received, ChannelPromise promise) throws Exception {
        if (received instanceof NetworkStateTransitions.DecoderTransitioner) {
            NetworkStateTransitions.DecoderTransitioner decoderTransitioner = (NetworkStateTransitions.DecoderTransitioner)received;
            try {
                decoderTransitioner.run(context);
            }
            finally {
                ReferenceCountUtil.release((Object)received);
            }
            promise.setSuccess();
        } else {
            context.write(received, promise);
        }
    }
}
