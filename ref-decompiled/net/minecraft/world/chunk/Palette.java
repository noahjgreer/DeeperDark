package net.minecraft.world.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;

public interface Palette {
   int index(Object object);

   boolean hasAny(Predicate predicate);

   Object get(int id);

   void readPacket(PacketByteBuf buf);

   void writePacket(PacketByteBuf buf);

   int getPacketSize();

   int getSize();

   Palette copy(PaletteResizeListener resizeListener);

   public interface Factory {
      Palette create(int bits, IndexedIterable idList, PaletteResizeListener listener, List list);
   }
}
