/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
class MultiplayerServerListPinger.1
implements ClientQueryPacketListener {
    private boolean sentQuery;
    private boolean received;
    private long startTime;
    final /* synthetic */ ClientConnection field_3774;
    final /* synthetic */ ServerInfo field_3776;
    final /* synthetic */ Runnable field_25636;
    final /* synthetic */ Runnable field_47886;
    final /* synthetic */ InetSocketAddress field_33741;
    final /* synthetic */ ServerAddress field_45613;
    final /* synthetic */ NetworkingBackend field_63973;

    MultiplayerServerListPinger.1() {
        this.field_3774 = clientConnection;
        this.field_3776 = serverInfo;
        this.field_25636 = runnable;
        this.field_47886 = runnable2;
        this.field_33741 = inetSocketAddress;
        this.field_45613 = serverAddress;
        this.field_63973 = networkingBackend;
    }

    @Override
    public void onResponse(QueryResponseS2CPacket packet) {
        if (this.received) {
            this.field_3774.disconnect(Text.translatable("multiplayer.status.unrequested"));
            return;
        }
        this.received = true;
        ServerMetadata serverMetadata = packet.metadata();
        this.field_3776.label = serverMetadata.description();
        serverMetadata.version().ifPresentOrElse(version -> {
            serverInfo.version = Text.literal(version.gameVersion());
            serverInfo.protocolVersion = version.protocolVersion();
        }, () -> {
            serverInfo.version = Text.translatable("multiplayer.status.old");
            serverInfo.protocolVersion = 0;
        });
        serverMetadata.players().ifPresentOrElse(players -> {
            serverInfo.playerCountLabel = MultiplayerServerListPinger.createPlayerCountText(players.online(), players.max());
            serverInfo.players = players;
            if (!players.sample().isEmpty()) {
                ArrayList<Text> list = new ArrayList<Text>(players.sample().size());
                for (PlayerConfigEntry playerConfigEntry : players.sample()) {
                    MutableText text = playerConfigEntry.equals(MinecraftServer.ANONYMOUS_PLAYER_PROFILE) ? Text.translatable("multiplayer.status.anonymous_player") : Text.literal(playerConfigEntry.name());
                    list.add(text);
                }
                if (players.sample().size() < players.online()) {
                    list.add(Text.translatable("multiplayer.status.and_more", players.online() - players.sample().size()));
                }
                serverInfo.playerListSummary = list;
            } else {
                serverInfo.playerListSummary = List.of();
            }
        }, () -> {
            serverInfo.playerCountLabel = Text.translatable("multiplayer.status.unknown").formatted(Formatting.DARK_GRAY);
        });
        serverMetadata.favicon().ifPresent(favicon -> {
            if (!Arrays.equals(favicon.iconBytes(), this.field_3776.getFavicon())) {
                this.field_3776.setFavicon(ServerInfo.validateFavicon(favicon.iconBytes()));
                this.field_25636.run();
            }
        });
        this.startTime = Util.getMeasuringTimeMs();
        this.field_3774.send(new QueryPingC2SPacket(this.startTime));
        this.sentQuery = true;
    }

    @Override
    public void onPingResult(PingResultS2CPacket packet) {
        long l = this.startTime;
        long m = Util.getMeasuringTimeMs();
        this.field_3776.ping = m - l;
        this.field_3774.disconnect(Text.translatable("multiplayer.status.finished"));
        this.field_47886.run();
    }

    @Override
    public void onDisconnected(DisconnectionInfo info) {
        if (!this.sentQuery) {
            MultiplayerServerListPinger.this.showError(info.reason(), this.field_3776);
            MultiplayerServerListPinger.this.ping(this.field_33741, this.field_45613, this.field_3776, this.field_63973);
        }
    }

    @Override
    public boolean isConnectionOpen() {
        return this.field_3774.isOpen();
    }
}
