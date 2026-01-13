/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.LegacyServerPinger;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class MultiplayerServerListPinger.2
extends ChannelInitializer<Channel> {
    final /* synthetic */ ServerAddress field_3778;
    final /* synthetic */ ServerInfo field_3779;

    MultiplayerServerListPinger.2(MultiplayerServerListPinger multiplayerServerListPinger, ServerAddress serverAddress, ServerInfo serverInfo) {
        this.field_3778 = serverAddress;
        this.field_3779 = serverInfo;
    }

    protected void initChannel(Channel channel) {
        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
        }
        catch (ChannelException channelException) {
            // empty catch block
        }
        channel.pipeline().addLast(new ChannelHandler[]{new LegacyServerPinger(this.field_3778, (protocolVersion, version, label, currentPlayers, maxPlayers) -> {
            this.field_3779.setStatus(ServerInfo.Status.INCOMPATIBLE);
            serverInfo.version = Text.literal(version);
            serverInfo.label = Text.literal(label);
            serverInfo.playerCountLabel = MultiplayerServerListPinger.createPlayerCountText(currentPlayers, maxPlayers);
            serverInfo.players = new ServerMetadata.Players(maxPlayers, currentPlayers, List.of());
        })});
    }
}
