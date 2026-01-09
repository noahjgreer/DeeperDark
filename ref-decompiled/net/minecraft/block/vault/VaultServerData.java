package net.minecraft.block.vault;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.MathHelper;

public class VaultServerData {
   static final String SERVER_DATA_KEY = "server_data";
   static Codec codec = RecordCodecBuilder.create((instance) -> {
      return instance.group(Uuids.LINKED_SET_CODEC.lenientOptionalFieldOf("rewarded_players", Set.of()).forGetter((data) -> {
         return data.rewardedPlayers;
      }), Codec.LONG.lenientOptionalFieldOf("state_updating_resumes_at", 0L).forGetter((data) -> {
         return data.stateUpdatingResumesAt;
      }), ItemStack.CODEC.listOf().lenientOptionalFieldOf("items_to_eject", List.of()).forGetter((data) -> {
         return data.itemsToEject;
      }), Codec.INT.lenientOptionalFieldOf("total_ejections_needed", 0).forGetter((data) -> {
         return data.totalEjectionsNeeded;
      })).apply(instance, VaultServerData::new);
   });
   private static final int MAX_STORED_REWARDED_PLAYERS = 128;
   private final Set rewardedPlayers = new ObjectLinkedOpenHashSet();
   private long stateUpdatingResumesAt;
   private final List itemsToEject = new ObjectArrayList();
   private long lastFailedUnlockTime;
   private int totalEjectionsNeeded;
   boolean dirty;

   VaultServerData(Set rewardedPlayers, long stateUpdatingResumesAt, List itemsToEject, int totalEjectionsNeeded) {
      this.rewardedPlayers.addAll(rewardedPlayers);
      this.stateUpdatingResumesAt = stateUpdatingResumesAt;
      this.itemsToEject.addAll(itemsToEject);
      this.totalEjectionsNeeded = totalEjectionsNeeded;
   }

   VaultServerData() {
   }

   void setLastFailedUnlockTime(long lastFailedUnlockTime) {
      this.lastFailedUnlockTime = lastFailedUnlockTime;
   }

   long getLastFailedUnlockTime() {
      return this.lastFailedUnlockTime;
   }

   Set getRewardedPlayers() {
      return this.rewardedPlayers;
   }

   boolean hasRewardedPlayer(PlayerEntity player) {
      return this.rewardedPlayers.contains(player.getUuid());
   }

   @VisibleForTesting
   public void markPlayerAsRewarded(PlayerEntity player) {
      this.rewardedPlayers.add(player.getUuid());
      if (this.rewardedPlayers.size() > 128) {
         Iterator iterator = this.rewardedPlayers.iterator();
         if (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
         }
      }

      this.markDirty();
   }

   long getStateUpdatingResumeTime() {
      return this.stateUpdatingResumesAt;
   }

   void setStateUpdatingResumeTime(long stateUpdatingResumesAt) {
      this.stateUpdatingResumesAt = stateUpdatingResumesAt;
      this.markDirty();
   }

   List getItemsToEject() {
      return this.itemsToEject;
   }

   void finishEjecting() {
      this.totalEjectionsNeeded = 0;
      this.markDirty();
   }

   void setItemsToEject(List itemsToEject) {
      this.itemsToEject.clear();
      this.itemsToEject.addAll(itemsToEject);
      this.totalEjectionsNeeded = this.itemsToEject.size();
      this.markDirty();
   }

   ItemStack getItemToDisplay() {
      return this.itemsToEject.isEmpty() ? ItemStack.EMPTY : (ItemStack)Objects.requireNonNullElse((ItemStack)this.itemsToEject.get(this.itemsToEject.size() - 1), ItemStack.EMPTY);
   }

   ItemStack getItemToEject() {
      if (this.itemsToEject.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.markDirty();
         return (ItemStack)Objects.requireNonNullElse((ItemStack)this.itemsToEject.remove(this.itemsToEject.size() - 1), ItemStack.EMPTY);
      }
   }

   void copyFrom(VaultServerData data) {
      this.stateUpdatingResumesAt = data.getStateUpdatingResumeTime();
      this.itemsToEject.clear();
      this.itemsToEject.addAll(data.itemsToEject);
      this.rewardedPlayers.clear();
      this.rewardedPlayers.addAll(data.rewardedPlayers);
   }

   private void markDirty() {
      this.dirty = true;
   }

   public float getEjectSoundPitchModifier() {
      return this.totalEjectionsNeeded == 1 ? 1.0F : 1.0F - MathHelper.getLerpProgress((float)this.getItemsToEject().size(), 1.0F, (float)this.totalEjectionsNeeded);
   }
}
