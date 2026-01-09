package net.minecraft.network.packet.s2c.custom;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record DebugVillageSectionsCustomPayload(Set villageChunks, Set notVillageChunks) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugVillageSectionsCustomPayload::write, DebugVillageSectionsCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/village_sections");

   private DebugVillageSectionsCustomPayload(PacketByteBuf buf) {
      this((Set)buf.readCollection(HashSet::new, PacketByteBuf::readChunkSectionPos), (Set)buf.readCollection(HashSet::new, PacketByteBuf::readChunkSectionPos));
   }

   public DebugVillageSectionsCustomPayload(Set set, Set set2) {
      this.villageChunks = set;
      this.notVillageChunks = set2;
   }

   private void write(PacketByteBuf buf) {
      buf.writeCollection(this.villageChunks, PacketByteBuf::writeChunkSectionPos);
      buf.writeCollection(this.notVillageChunks, PacketByteBuf::writeChunkSectionPos);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public Set villageChunks() {
      return this.villageChunks;
   }

   public Set notVillageChunks() {
      return this.notVillageChunks;
   }
}
