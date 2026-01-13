/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry
 *  net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListWidget
 *  net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen
 *  net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen$Tab
 *  net.minecraft.client.gui.widget.ElementListWidget
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.client.session.report.log.ChatLog
 *  net.minecraft.client.session.report.log.ChatLogEntry
 *  net.minecraft.client.session.report.log.ReceivedMessage$ChatMessage
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.session.report.log.ChatLog;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.client.session.report.log.ReceivedMessage;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SocialInteractionsPlayerListWidget
extends ElementListWidget<SocialInteractionsPlayerListEntry> {
    private final SocialInteractionsScreen parent;
    private final List<SocialInteractionsPlayerListEntry> players = Lists.newArrayList();
    private @Nullable String currentSearch;

    public SocialInteractionsPlayerListWidget(SocialInteractionsScreen parent, MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
        this.parent = parent;
    }

    protected void drawMenuListBackground(DrawContext context) {
    }

    protected void drawHeaderAndFooterSeparators(DrawContext context) {
    }

    protected void enableScissor(DrawContext context) {
        context.enableScissor(this.getX(), this.getY() + 4, this.getRight(), this.getBottom());
    }

    public void update(Collection<UUID> uuids, double scrollAmount, boolean includeOffline) {
        HashMap map = new HashMap();
        this.setPlayers(uuids, map);
        if (includeOffline) {
            this.collectOfflinePlayers(map);
        }
        this.markOfflineMembers(map, includeOffline);
        this.refresh(map.values(), scrollAmount);
    }

    private void setPlayers(Collection<UUID> playerUuids, Map<UUID, SocialInteractionsPlayerListEntry> entriesByUuids) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        for (UUID uUID : playerUuids) {
            PlayerListEntry playerListEntry = clientPlayNetworkHandler.getPlayerListEntry(uUID);
            if (playerListEntry == null) continue;
            SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry = this.createListEntry(uUID, playerListEntry);
            entriesByUuids.put(uUID, socialInteractionsPlayerListEntry);
        }
    }

    private void collectOfflinePlayers(Map<UUID, SocialInteractionsPlayerListEntry> entriesByUuids) {
        Map map = this.client.player.networkHandler.getSeenPlayers();
        for (Map.Entry entry : map.entrySet()) {
            entriesByUuids.computeIfAbsent((UUID)entry.getKey(), uuid -> {
                SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry = this.createListEntry(uuid, (PlayerListEntry)entry.getValue());
                socialInteractionsPlayerListEntry.setOffline(true);
                return socialInteractionsPlayerListEntry;
            });
        }
    }

    private SocialInteractionsPlayerListEntry createListEntry(UUID uuid, PlayerListEntry playerListEntry) {
        return new SocialInteractionsPlayerListEntry(this.client, this.parent, uuid, playerListEntry.getProfile().name(), () -> ((PlayerListEntry)playerListEntry).getSkinTextures(), playerListEntry.hasPublicKey());
    }

    private void markOfflineMembers(Map<UUID, SocialInteractionsPlayerListEntry> entries, boolean includeOffline) {
        Map map = SocialInteractionsPlayerListWidget.collectReportableProfiles((ChatLog)this.client.getAbuseReportContext().getChatLog());
        map.forEach((uuid2, profile) -> {
            SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry;
            if (includeOffline) {
                socialInteractionsPlayerListEntry = entries.computeIfAbsent((UUID)uuid2, uuid -> {
                    SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry = new SocialInteractionsPlayerListEntry(this.client, this.parent, profile.id(), profile.name(), this.client.getSkinProvider().supplySkinTextures(profile, true), true);
                    socialInteractionsPlayerListEntry.setOffline(true);
                    return socialInteractionsPlayerListEntry;
                });
            } else {
                socialInteractionsPlayerListEntry = (SocialInteractionsPlayerListEntry)entries.get(uuid2);
                if (socialInteractionsPlayerListEntry == null) {
                    return;
                }
            }
            socialInteractionsPlayerListEntry.setSentMessage(true);
        });
    }

    private static Map<UUID, GameProfile> collectReportableProfiles(ChatLog log) {
        Object2ObjectLinkedOpenHashMap map = new Object2ObjectLinkedOpenHashMap();
        for (int i = log.getMaxIndex(); i >= log.getMinIndex(); --i) {
            ReceivedMessage.ChatMessage chatMessage;
            ChatLogEntry chatLogEntry = log.get(i);
            if (!(chatLogEntry instanceof ReceivedMessage.ChatMessage) || !(chatMessage = (ReceivedMessage.ChatMessage)chatLogEntry).message().hasSignature()) continue;
            map.put(chatMessage.getSenderUuid(), chatMessage.profile());
        }
        return map;
    }

    private void sortPlayers() {
        this.players.sort(Comparator.comparing(player -> {
            if (this.client.uuidEquals(player.getUuid())) {
                return 0;
            }
            if (this.client.getAbuseReportContext().draftPlayerUuidEquals(player.getUuid())) {
                return 1;
            }
            if (player.getUuid().version() == 2) {
                return 4;
            }
            if (player.hasSentMessage()) {
                return 2;
            }
            return 3;
        }).thenComparing(player -> {
            int i;
            if (!player.getName().isBlank() && ((i = player.getName().codePointAt(0)) == 95 || i >= 97 && i <= 122 || i >= 65 && i <= 90 || i >= 48 && i <= 57)) {
                return 0;
            }
            return 1;
        }).thenComparing(SocialInteractionsPlayerListEntry::getName, String::compareToIgnoreCase));
    }

    private void refresh(Collection<SocialInteractionsPlayerListEntry> players, double scrollAmount) {
        this.players.clear();
        this.players.addAll(players);
        this.sortPlayers();
        this.filterPlayers();
        this.replaceEntries((Collection)this.players);
        this.setScrollY(scrollAmount);
    }

    private void filterPlayers() {
        if (this.currentSearch != null) {
            this.players.removeIf(player -> !player.getName().toLowerCase(Locale.ROOT).contains(this.currentSearch));
            this.replaceEntries((Collection)this.players);
        }
    }

    public void setCurrentSearch(String currentSearch) {
        this.currentSearch = currentSearch;
    }

    public boolean isEmpty() {
        return this.players.isEmpty();
    }

    public void setPlayerOnline(PlayerListEntry player, SocialInteractionsScreen.Tab tab) {
        UUID uUID = player.getProfile().id();
        for (SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry : this.players) {
            if (!socialInteractionsPlayerListEntry.getUuid().equals(uUID)) continue;
            socialInteractionsPlayerListEntry.setOffline(false);
            return;
        }
        if ((tab == SocialInteractionsScreen.Tab.ALL || this.client.getSocialInteractionsManager().isPlayerMuted(uUID)) && (Strings.isNullOrEmpty((String)this.currentSearch) || player.getProfile().name().toLowerCase(Locale.ROOT).contains(this.currentSearch))) {
            SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry;
            boolean bl = player.hasPublicKey();
            socialInteractionsPlayerListEntry = new SocialInteractionsPlayerListEntry(this.client, this.parent, player.getProfile().id(), player.getProfile().name(), () -> ((PlayerListEntry)player).getSkinTextures(), bl);
            this.addEntry((EntryListWidget.Entry)socialInteractionsPlayerListEntry);
            this.players.add(socialInteractionsPlayerListEntry);
        }
    }

    public void setPlayerOffline(UUID uuid) {
        for (SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry : this.players) {
            if (!socialInteractionsPlayerListEntry.getUuid().equals(uuid)) continue;
            socialInteractionsPlayerListEntry.setOffline(true);
            return;
        }
    }

    public void updateHasDraftReport() {
        this.players.forEach(player -> player.updateHasDraftReport(this.client.getAbuseReportContext()));
    }
}

