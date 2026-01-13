/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 */
package net.minecraft.network.handler;

import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public static interface NetworkStateTransitions.DecoderTransitioner {
    public void run(ChannelHandlerContext var1);

    default public NetworkStateTransitions.DecoderTransitioner andThen(NetworkStateTransitions.DecoderTransitioner decoderTransitioner) {
        return context -> {
            this.run(context);
            decoderTransitioner.run(context);
        };
    }
}
