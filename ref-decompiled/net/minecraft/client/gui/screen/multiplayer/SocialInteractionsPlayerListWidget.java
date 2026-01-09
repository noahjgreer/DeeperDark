package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.session.report.log.ChatLog;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.client.session.report.log.ReceivedMessage;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SocialInteractionsPlayerListWidget extends ElementListWidget {
   private final SocialInteractionsScreen parent;
   private final List players = Lists.newArrayList();
   @Nullable
   private String currentSearch;

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

   public void update(Collection uuids, double scrollAmount, boolean includeOffline) {
      Map map = new HashMap();
      this.setPlayers(uuids, map);
      this.markOfflineMembers(map, includeOffline);
      this.refresh(map.values(), scrollAmount);
   }

   private void setPlayers(Collection playerUuids, Map entriesByUuids) {
      ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
      Iterator var4 = playerUuids.iterator();

      while(var4.hasNext()) {
         UUID uUID = (UUID)var4.next();
         PlayerListEntry playerListEntry = clientPlayNetworkHandler.getPlayerListEntry(uUID);
         if (playerListEntry != null) {
            boolean bl = playerListEntry.hasPublicKey();
            MinecraftClient var10004 = this.client;
            SocialInteractionsScreen var10005 = this.parent;
            String var10007 = playerListEntry.getProfile().getName();
            Objects.requireNonNull(playerListEntry);
            entriesByUuids.put(uUID, new SocialInteractionsPlayerListEntry(var10004, var10005, uUID, var10007, playerListEntry::getSkinTextures, bl));
         }
      }

   }

   private void markOfflineMembers(Map entries, boolean includeOffline) {
      Collection collection = collectReportableProfiles(this.client.getAbuseReportContext().getChatLog());
      Iterator var4 = collection.iterator();

      while(true) {
         SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry;
         do {
            if (!var4.hasNext()) {
               return;
            }

            GameProfile gameProfile = (GameProfile)var4.next();
            if (includeOffline) {
               socialInteractionsPlayerListEntry = (SocialInteractionsPlayerListEntry)entries.computeIfAbsent(gameProfile.getId(), (uuid) -> {
                  SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry = new SocialInteractionsPlayerListEntry(this.client, this.parent, gameProfile.getId(), gameProfile.getName(), this.client.getSkinProvider().getSkinTexturesSupplier(gameProfile), true);
                  socialInteractionsPlayerListEntry.setOffline(true);
                  return socialInteractionsPlayerListEntry;
               });
               break;
            }

            socialInteractionsPlayerListEntry = (SocialInteractionsPlayerListEntry)entries.get(gameProfile.getId());
         } while(socialInteractionsPlayerListEntry == null);

         socialInteractionsPlayerListEntry.setSentMessage(true);
      }
   }

   private static Collection collectReportableProfiles(ChatLog log) {
      Set set = new ObjectLinkedOpenHashSet();

      for(int i = log.getMaxIndex(); i >= log.getMinIndex(); --i) {
         ChatLogEntry chatLogEntry = log.get(i);
         if (chatLogEntry instanceof ReceivedMessage.ChatMessage chatMessage) {
            if (chatMessage.message().hasSignature()) {
               set.add(chatMessage.profile());
            }
         }
      }

      return set;
   }

   private void sortPlayers() {
      this.players.sort(Comparator.comparing((player) -> {
         if (this.client.uuidEquals(player.getUuid())) {
            return 0;
         } else if (this.client.getAbuseReportContext().draftPlayerUuidEquals(player.getUuid())) {
            return 1;
         } else if (player.getUuid().version() == 2) {
            return 4;
         } else {
            return player.hasSentMessage() ? 2 : 3;
         }
      }).thenComparing((player) -> {
         if (!player.getName().isBlank()) {
            int i = player.getName().codePointAt(0);
            if (i == 95 || i >= 97 && i <= 122 || i >= 65 && i <= 90 || i >= 48 && i <= 57) {
               return 0;
            }
         }

         return 1;
      }).thenComparing(SocialInteractionsPlayerListEntry::getName, String::compareToIgnoreCase));
   }

   private void refresh(Collection players, double scrollAmount) {
      this.players.clear();
      this.players.addAll(players);
      this.sortPlayers();
      this.filterPlayers();
      this.replaceEntries(this.players);
      this.setScrollY(scrollAmount);
   }

   private void filterPlayers() {
      if (this.currentSearch != null) {
         this.players.removeIf((player) -> {
            return !player.getName().toLowerCase(Locale.ROOT).contains(this.currentSearch);
         });
         this.replaceEntries(this.players);
      }

   }

   public void setCurrentSearch(String currentSearch) {
      this.currentSearch = currentSearch;
   }

   public boolean isEmpty() {
      return this.players.isEmpty();
   }

   public void setPlayerOnline(PlayerListEntry player, SocialInteractionsScreen.Tab tab) {
      UUID uUID = player.getProfile().getId();
      Iterator var4 = this.players.iterator();

      SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry;
      while(var4.hasNext()) {
         socialInteractionsPlayerListEntry = (SocialInteractionsPlayerListEntry)var4.next();
         if (socialInteractionsPlayerListEntry.getUuid().equals(uUID)) {
            socialInteractionsPlayerListEntry.setOffline(false);
            return;
         }
      }

      if ((tab == SocialInteractionsScreen.Tab.ALL || this.client.getSocialInteractionsManager().isPlayerMuted(uUID)) && (Strings.isNullOrEmpty(this.currentSearch) || player.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.currentSearch))) {
         boolean bl = player.hasPublicKey();
         MinecraftClient var10002 = this.client;
         SocialInteractionsScreen var10003 = this.parent;
         UUID var10004 = player.getProfile().getId();
         String var10005 = player.getProfile().getName();
         Objects.requireNonNull(player);
         socialInteractionsPlayerListEntry = new SocialInteractionsPlayerListEntry(var10002, var10003, var10004, var10005, player::getSkinTextures, bl);
         this.addEntry(socialInteractionsPlayerListEntry);
         this.players.add(socialInteractionsPlayerListEntry);
      }

   }

   public void setPlayerOffline(UUID uuid) {
      Iterator var2 = this.players.iterator();

      SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry;
      do {
         if (!var2.hasNext()) {
            return;
         }

         socialInteractionsPlayerListEntry = (SocialInteractionsPlayerListEntry)var2.next();
      } while(!socialInteractionsPlayerListEntry.getUuid().equals(uuid));

      socialInteractionsPlayerListEntry.setOffline(true);
   }

   public void updateHasDraftReport() {
      this.players.forEach((player) -> {
         player.updateHasDraftReport(this.client.getAbuseReportContext());
      });
   }
}
