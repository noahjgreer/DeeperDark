package net.minecraft.server.network;

import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.tag.TagPacketSerializer;

public class SynchronizeRegistriesTask implements ServerPlayerConfigurationTask {
   public static final ServerPlayerConfigurationTask.Key KEY = new ServerPlayerConfigurationTask.Key("synchronize_registries");
   private final List knownPacks;
   private final CombinedDynamicRegistries registries;

   public SynchronizeRegistriesTask(List knownPacks, CombinedDynamicRegistries registries) {
      this.knownPacks = knownPacks;
      this.registries = registries;
   }

   public void sendPacket(Consumer sender) {
      sender.accept(new SelectKnownPacksS2CPacket(this.knownPacks));
   }

   private void syncRegistryAndTags(Consumer sender, Set commonKnownPacks) {
      DynamicOps dynamicOps = this.registries.getCombinedRegistryManager().getOps(NbtOps.INSTANCE);
      SerializableRegistries.forEachSyncedRegistry(dynamicOps, this.registries.getSucceedingRegistryManagers(ServerDynamicRegistryType.WORLDGEN), commonKnownPacks, (key, entries) -> {
         sender.accept(new DynamicRegistriesS2CPacket(key, entries));
      });
      sender.accept(new SynchronizeTagsS2CPacket(TagPacketSerializer.serializeTags(this.registries)));
   }

   public void onSelectKnownPacks(List clientKnownPacks, Consumer sender) {
      if (clientKnownPacks.equals(this.knownPacks)) {
         this.syncRegistryAndTags(sender, Set.copyOf(this.knownPacks));
      } else {
         this.syncRegistryAndTags(sender, Set.of());
      }

   }

   public ServerPlayerConfigurationTask.Key getKey() {
      return KEY;
   }
}
