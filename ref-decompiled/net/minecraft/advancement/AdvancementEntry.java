package net.minecraft.advancement;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

public record AdvancementEntry(Identifier id, Advancement value) {
   public static final PacketCodec PACKET_CODEC;
   public static final PacketCodec LIST_PACKET_CODEC;

   public AdvancementEntry(Identifier identifier, Advancement advancement) {
      this.id = identifier;
      this.value = advancement;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof AdvancementEntry) {
            AdvancementEntry advancementEntry = (AdvancementEntry)o;
            if (this.id.equals(advancementEntry.id)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String toString() {
      return this.id.toString();
   }

   public Identifier id() {
      return this.id;
   }

   public Advancement value() {
      return this.value;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, AdvancementEntry::id, Advancement.PACKET_CODEC, AdvancementEntry::value, AdvancementEntry::new);
      LIST_PACKET_CODEC = PACKET_CODEC.collect(PacketCodecs.toList());
   }
}
