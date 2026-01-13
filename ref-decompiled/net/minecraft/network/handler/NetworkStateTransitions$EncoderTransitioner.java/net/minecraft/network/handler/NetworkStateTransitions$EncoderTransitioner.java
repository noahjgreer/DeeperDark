/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandlerContext
 */
package net.minecraft.network.handler;

import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public static interface NetworkStateTransitions.EncoderTransitioner {
    public void run(ChannelHandlerContext var1);

    default public NetworkStateTransitions.EncoderTransitioner andThen(NetworkStateTransitions.EncoderTransitioner encoderTransitioner) {
        return context -> {
            this.run(context);
            encoderTransitioner.run(context);
        };
    }
}
