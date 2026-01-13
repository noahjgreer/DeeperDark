/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget$Entry
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget$LanServerEntry
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget$ScanningEntry
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget$ServerEntry
 *  net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.client.network.LanServerInfo
 *  net.minecraft.client.option.ServerList
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.logging.UncaughtExceptionLogger
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerServerListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    static final Identifier INCOMPATIBLE_TEXTURE = Identifier.ofVanilla((String)"server_list/incompatible");
    static final Identifier UNREACHABLE_TEXTURE = Identifier.ofVanilla((String)"server_list/unreachable");
    static final Identifier PING_1_TEXTURE = Identifier.ofVanilla((String)"server_list/ping_1");
    static final Identifier PING_2_TEXTURE = Identifier.ofVanilla((String)"server_list/ping_2");
    static final Identifier PING_3_TEXTURE = Identifier.ofVanilla((String)"server_list/ping_3");
    static final Identifier PING_4_TEXTURE = Identifier.ofVanilla((String)"server_list/ping_4");
    static final Identifier PING_5_TEXTURE = Identifier.ofVanilla((String)"server_list/ping_5");
    static final Identifier PINGING_1_TEXTURE = Identifier.ofVanilla((String)"server_list/pinging_1");
    static final Identifier PINGING_2_TEXTURE = Identifier.ofVanilla((String)"server_list/pinging_2");
    static final Identifier PINGING_3_TEXTURE = Identifier.ofVanilla((String)"server_list/pinging_3");
    static final Identifier PINGING_4_TEXTURE = Identifier.ofVanilla((String)"server_list/pinging_4");
    static final Identifier PINGING_5_TEXTURE = Identifier.ofVanilla((String)"server_list/pinging_5");
    static final Identifier JOIN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"server_list/join_highlighted");
    static final Identifier JOIN_TEXTURE = Identifier.ofVanilla((String)"server_list/join");
    static final Identifier MOVE_UP_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"server_list/move_up_highlighted");
    static final Identifier MOVE_UP_TEXTURE = Identifier.ofVanilla((String)"server_list/move_up");
    static final Identifier MOVE_DOWN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"server_list/move_down_highlighted");
    static final Identifier MOVE_DOWN_TEXTURE = Identifier.ofVanilla((String)"server_list/move_down");
    static final Logger LOGGER = LogUtils.getLogger();
    static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new UncaughtExceptionLogger(LOGGER)).build());
    static final Text LAN_SCANNING_TEXT = Text.translatable((String)"lanServer.scanning");
    static final Text CANNOT_RESOLVE_TEXT = Text.translatable((String)"multiplayer.status.cannot_resolve").withColor(-65536);
    static final Text CANNOT_CONNECT_TEXT = Text.translatable((String)"multiplayer.status.cannot_connect").withColor(-65536);
    static final Text INCOMPATIBLE_TEXT = Text.translatable((String)"multiplayer.status.incompatible");
    static final Text NO_CONNECTION_TEXT = Text.translatable((String)"multiplayer.status.no_connection");
    static final Text PINGING_TEXT = Text.translatable((String)"multiplayer.status.pinging");
    static final Text ONLINE_TEXT = Text.translatable((String)"multiplayer.status.online");
    private final MultiplayerScreen screen;
    private final List<ServerEntry> servers = Lists.newArrayList();
    private final Entry scanningEntry = new ScanningEntry();
    private final List<LanServerEntry> lanServers = Lists.newArrayList();

    public MultiplayerServerListWidget(MultiplayerScreen screen, MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, bottom);
        this.screen = screen;
    }

    private void updateEntries() {
        Entry entry = (Entry)this.getSelectedOrNull();
        ArrayList<Entry> list = new ArrayList<Entry>(this.servers);
        list.add(this.scanningEntry);
        list.addAll(this.lanServers);
        this.replaceEntries(list);
        if (entry != null) {
            for (Entry entry2 : list) {
                if (!entry2.isOfSameType(entry)) continue;
                this.setSelected(entry2);
                break;
            }
        }
    }

    public void setSelected(// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable MultiplayerServerListWidget.Entry entry) {
        super.setSelected((EntryListWidget.Entry)entry);
        this.screen.updateButtonActivationStates();
    }

    public void setServers(ServerList servers) {
        this.servers.clear();
        for (int i = 0; i < servers.size(); ++i) {
            this.servers.add(new ServerEntry(this, this.screen, servers.get(i)));
        }
        this.updateEntries();
    }

    public void setLanServers(List<LanServerInfo> lanServers) {
        int i = lanServers.size() - this.lanServers.size();
        this.lanServers.clear();
        for (LanServerInfo lanServerInfo : lanServers) {
            this.lanServers.add(new LanServerEntry(this.screen, lanServerInfo));
        }
        this.updateEntries();
        for (int j = this.lanServers.size() - i; j < this.lanServers.size(); ++j) {
            LanServerEntry lanServerEntry = (LanServerEntry)this.lanServers.get(j);
            int k = j - this.lanServers.size() + this.children().size();
            int l = this.getRowTop(k);
            int m = this.getRowBottom(k);
            if (m < this.getY() || l > this.getBottom()) continue;
            this.client.getNarratorManager().narrateSystemMessage((Text)Text.translatable((String)"multiplayer.lan.server_found", (Object[])new Object[]{lanServerEntry.getMotdNarration()}));
        }
    }

    public int getRowWidth() {
        return 305;
    }

    public void onRemoved() {
    }

    static /* synthetic */ void method_76277(MultiplayerServerListWidget multiplayerServerListWidget, DrawContext drawContext) {
        multiplayerServerListWidget.setCursor(drawContext);
    }

    static /* synthetic */ void method_76278(MultiplayerServerListWidget multiplayerServerListWidget, DrawContext drawContext) {
        multiplayerServerListWidget.setCursor(drawContext);
    }

    static /* synthetic */ void method_76279(MultiplayerServerListWidget multiplayerServerListWidget, DrawContext drawContext) {
        multiplayerServerListWidget.setCursor(drawContext);
    }

    static /* synthetic */ void method_76276(MultiplayerServerListWidget multiplayerServerListWidget, int i, int j) {
        multiplayerServerListWidget.swapEntriesOnPositions(i, j);
    }
}

