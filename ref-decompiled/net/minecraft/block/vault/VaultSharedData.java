package net.minecraft.block.vault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

public class VaultSharedData {
   static final String SHARED_DATA_KEY = "shared_data";
   static Codec codec = RecordCodecBuilder.create((instance) -> {
      return instance.group(ItemStack.createOptionalCodec("display_item").forGetter((data) -> {
         return data.displayItem;
      }), Uuids.LINKED_SET_CODEC.lenientOptionalFieldOf("connected_players", Set.of()).forGetter((data) -> {
         return data.connectedPlayers;
      }), Codec.DOUBLE.lenientOptionalFieldOf("connected_particles_range", VaultConfig.DEFAULT.deactivationRange()).forGetter((data) -> {
         return data.connectedParticlesRange;
      })).apply(instance, VaultSharedData::new);
   });
   private ItemStack displayItem;
   private Set connectedPlayers;
   private double connectedParticlesRange;
   boolean dirty;

   VaultSharedData(ItemStack displayItem, Set connectedPlayers, double connectedParticlesRange) {
      this.displayItem = ItemStack.EMPTY;
      this.connectedPlayers = new ObjectLinkedOpenHashSet();
      this.connectedParticlesRange = VaultConfig.DEFAULT.deactivationRange();
      this.displayItem = displayItem;
      this.connectedPlayers.addAll(connectedPlayers);
      this.connectedParticlesRange = connectedParticlesRange;
   }

   VaultSharedData() {
      this.displayItem = ItemStack.EMPTY;
      this.connectedPlayers = new ObjectLinkedOpenHashSet();
      this.connectedParticlesRange = VaultConfig.DEFAULT.deactivationRange();
   }

   public ItemStack getDisplayItem() {
      return this.displayItem;
   }

   public boolean hasDisplayItem() {
      return !this.displayItem.isEmpty();
   }

   public void setDisplayItem(ItemStack stack) {
      if (!ItemStack.areEqual(this.displayItem, stack)) {
         this.displayItem = stack.copy();
         this.markDirty();
      }
   }

   boolean hasConnectedPlayers() {
      return !this.connectedPlayers.isEmpty();
   }

   Set getConnectedPlayers() {
      return this.connectedPlayers;
   }

   double getConnectedParticlesRange() {
      return this.connectedParticlesRange;
   }

   void updateConnectedPlayers(ServerWorld world, BlockPos pos, VaultServerData serverData, VaultConfig config, double radius) {
      Set set = (Set)config.playerDetector().detect(world, config.entitySelector(), pos, radius, false).stream().filter((uuid) -> {
         return !serverData.getRewardedPlayers().contains(uuid);
      }).collect(Collectors.toSet());
      if (!this.connectedPlayers.equals(set)) {
         this.connectedPlayers = set;
         this.markDirty();
      }

   }

   private void markDirty() {
      this.dirty = true;
   }

   void copyFrom(VaultSharedData data) {
      this.displayItem = data.displayItem;
      this.connectedPlayers = data.connectedPlayers;
      this.connectedParticlesRange = data.connectedParticlesRange;
   }
}
