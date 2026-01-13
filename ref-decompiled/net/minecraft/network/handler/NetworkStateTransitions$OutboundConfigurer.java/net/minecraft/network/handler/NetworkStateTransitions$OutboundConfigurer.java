/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelOutboundHandlerAdapter
 *  io.netty.channel.ChannelPromise
 *  io.netty.handler.codec.EncoderException
 *  io.netty.util.ReferenceCountUtil
 */
package net.minecraft.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.network.handler.NetworkStateTransitions;
import net.minecraft.network.packet.Packet;

public static class NetworkStateTransitions.OutboundConfigurer
extends ChannelOutboundHandlerAdapter {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(ChannelHandlerContext context, Object received, ChannelPromise promise) throws Exception {
        if (received instanceof Packet) {
            ReferenceCountUtil.release((Object)received);
            throw new EncoderException("Pipeline has no outbound protocol configured, can't process packet " + String.valueOf(received));
        }
        if (received instanceof NetworkStateTransitions.EncoderTransitioner) {
            NetworkStateTransitions.EncoderTransitioner encoderTransitioner = (NetworkStateTransitions.EncoderTransitioner)received;
            try {
                encoderTransitioner.run(context);
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
