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
 *  io.netty.handler.codec.http.HttpObjectAggregator
 *  io.netty.handler.codec.http.HttpServerCodec
 *  io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
 *  io.netty.handler.ssl.SslContext
 */
package net.minecraft.server.dedicated.management;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import net.minecraft.server.dedicated.management.ManagementLogger;
import net.minecraft.server.dedicated.management.dispatch.ManagementHandlerDispatcher;
import net.minecraft.server.dedicated.management.network.JsonElementToWebSocketFrameEncoder;
import net.minecraft.server.dedicated.management.network.ManagementConnectionHandler;
import net.minecraft.server.dedicated.management.network.WebSocketFrameToJsonElementDecoder;

class ManagementServer.1
extends ChannelInitializer<Channel> {
    final /* synthetic */ SslContext field_62803;
    final /* synthetic */ ManagementHandlerDispatcher field_62317;
    final /* synthetic */ ManagementLogger field_62318;

    ManagementServer.1(SslContext sslContext, ManagementHandlerDispatcher managementHandlerDispatcher, ManagementLogger managementLogger) {
        this.field_62803 = sslContext;
        this.field_62317 = managementHandlerDispatcher;
        this.field_62318 = managementLogger;
    }

    protected void initChannel(Channel channel) {
        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
        }
        catch (ChannelException channelException) {
            // empty catch block
        }
        ChannelPipeline channelPipeline = channel.pipeline();
        if (this.field_62803 != null) {
            channelPipeline.addLast(new ChannelHandler[]{this.field_62803.newHandler(channel.alloc())});
        }
        channelPipeline.addLast(new ChannelHandler[]{new HttpServerCodec()}).addLast(new ChannelHandler[]{new HttpObjectAggregator(65536)}).addLast(new ChannelHandler[]{ManagementServer.this.authHandler}).addLast(new ChannelHandler[]{new WebSocketServerProtocolHandler("/")}).addLast(new ChannelHandler[]{new WebSocketFrameToJsonElementDecoder()}).addLast(new ChannelHandler[]{new JsonElementToWebSocketFrameEncoder()}).addLast(new ChannelHandler[]{new ManagementConnectionHandler(channel, ManagementServer.this, this.field_62317, this.field_62318)});
    }
}
