package net.minecraft.world.chunk;

import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;

public interface ReadableContainer {
   Object get(int x, int y, int z);

   void forEachValue(Consumer action);

   void writePacket(PacketByteBuf buf);

   int getPacketSize();

   boolean hasAny(Predicate predicate);

   void count(PalettedContainer.Counter counter);

   PalettedContainer copy();

   PalettedContainer slice();

   Serialized serialize(IndexedIterable idList, PalettedContainer.PaletteProvider paletteProvider);

   public interface Reader {
      DataResult read(IndexedIterable idList, PalettedContainer.PaletteProvider paletteProvider, Serialized serialize);
   }

   public static record Serialized(List paletteEntries, Optional storage) {
      public Serialized(List list, Optional optional) {
         this.paletteEntries = list;
         this.storage = optional;
      }

      public List paletteEntries() {
         return this.paletteEntries;
      }

      public Optional storage() {
         return this.storage;
      }
   }
}
