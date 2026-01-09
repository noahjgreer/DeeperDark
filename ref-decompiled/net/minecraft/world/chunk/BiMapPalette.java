package net.minecraft.world.chunk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.collection.Int2ObjectBiMap;

public class BiMapPalette implements Palette {
   private final IndexedIterable idList;
   private final Int2ObjectBiMap map;
   private final PaletteResizeListener listener;
   private final int indexBits;

   public BiMapPalette(IndexedIterable idList, int bits, PaletteResizeListener listener, List entries) {
      this(idList, bits, listener);
      Int2ObjectBiMap var10001 = this.map;
      Objects.requireNonNull(var10001);
      entries.forEach(var10001::add);
   }

   public BiMapPalette(IndexedIterable idList, int indexBits, PaletteResizeListener listener) {
      this(idList, indexBits, listener, Int2ObjectBiMap.create(1 << indexBits));
   }

   private BiMapPalette(IndexedIterable idList, int indexBits, PaletteResizeListener listener, Int2ObjectBiMap map) {
      this.idList = idList;
      this.indexBits = indexBits;
      this.listener = listener;
      this.map = map;
   }

   public static Palette create(int bits, IndexedIterable idList, PaletteResizeListener listener, List entries) {
      return new BiMapPalette(idList, bits, listener, entries);
   }

   public int index(Object object) {
      int i = this.map.getRawId(object);
      if (i == -1) {
         i = this.map.add(object);
         if (i >= 1 << this.indexBits) {
            i = this.listener.onResize(this.indexBits + 1, object);
         }
      }

      return i;
   }

   public boolean hasAny(Predicate predicate) {
      for(int i = 0; i < this.getSize(); ++i) {
         if (predicate.test(this.map.get(i))) {
            return true;
         }
      }

      return false;
   }

   public Object get(int id) {
      Object object = this.map.get(id);
      if (object == null) {
         throw new EntryMissingException(id);
      } else {
         return object;
      }
   }

   public void readPacket(PacketByteBuf buf) {
      this.map.clear();
      int i = buf.readVarInt();

      for(int j = 0; j < i; ++j) {
         this.map.add(this.idList.getOrThrow(buf.readVarInt()));
      }

   }

   public void writePacket(PacketByteBuf buf) {
      int i = this.getSize();
      buf.writeVarInt(i);

      for(int j = 0; j < i; ++j) {
         buf.writeVarInt(this.idList.getRawId(this.map.get(j)));
      }

   }

   public int getPacketSize() {
      int i = VarInts.getSizeInBytes(this.getSize());

      for(int j = 0; j < this.getSize(); ++j) {
         i += VarInts.getSizeInBytes(this.idList.getRawId(this.map.get(j)));
      }

      return i;
   }

   public List getElements() {
      ArrayList arrayList = new ArrayList();
      Iterator var10000 = this.map.iterator();
      Objects.requireNonNull(arrayList);
      var10000.forEachRemaining(arrayList::add);
      return arrayList;
   }

   public int getSize() {
      return this.map.size();
   }

   public Palette copy(PaletteResizeListener resizeListener) {
      return new BiMapPalette(this.idList, this.indexBits, resizeListener, this.map.copy());
   }
}
