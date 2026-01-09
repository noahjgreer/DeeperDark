package net.minecraft.world.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;

public class IdListPalette implements Palette {
   private final IndexedIterable idList;

   public IdListPalette(IndexedIterable idList) {
      this.idList = idList;
   }

   public static Palette create(int bits, IndexedIterable idList, PaletteResizeListener listener, List list) {
      return new IdListPalette(idList);
   }

   public int index(Object object) {
      int i = this.idList.getRawId(object);
      return i == -1 ? 0 : i;
   }

   public boolean hasAny(Predicate predicate) {
      return true;
   }

   public Object get(int id) {
      Object object = this.idList.get(id);
      if (object == null) {
         throw new EntryMissingException(id);
      } else {
         return object;
      }
   }

   public void readPacket(PacketByteBuf buf) {
   }

   public void writePacket(PacketByteBuf buf) {
   }

   public int getPacketSize() {
      return 0;
   }

   public int getSize() {
      return this.idList.size();
   }

   public Palette copy(PaletteResizeListener resizeListener) {
      return this;
   }
}
