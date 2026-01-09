package net.minecraft.world.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.util.collection.IndexedIterable;
import org.apache.commons.lang3.Validate;

public class ArrayPalette implements Palette {
   private final IndexedIterable idList;
   private final Object[] array;
   private final PaletteResizeListener listener;
   private final int indexBits;
   private int size;

   private ArrayPalette(IndexedIterable idList, int bits, PaletteResizeListener listener, List list) {
      this.idList = idList;
      this.array = new Object[1 << bits];
      this.indexBits = bits;
      this.listener = listener;
      Validate.isTrue(list.size() <= this.array.length, "Can't initialize LinearPalette of size %d with %d entries", new Object[]{this.array.length, list.size()});

      for(int i = 0; i < list.size(); ++i) {
         this.array[i] = list.get(i);
      }

      this.size = list.size();
   }

   private ArrayPalette(IndexedIterable idList, Object[] array, PaletteResizeListener listener, int indexBits, int size) {
      this.idList = idList;
      this.array = array;
      this.listener = listener;
      this.indexBits = indexBits;
      this.size = size;
   }

   public static Palette create(int bits, IndexedIterable idList, PaletteResizeListener listener, List list) {
      return new ArrayPalette(idList, bits, listener, list);
   }

   public int index(Object object) {
      int i;
      for(i = 0; i < this.size; ++i) {
         if (this.array[i] == object) {
            return i;
         }
      }

      i = this.size;
      if (i < this.array.length) {
         this.array[i] = object;
         ++this.size;
         return i;
      } else {
         return this.listener.onResize(this.indexBits + 1, object);
      }
   }

   public boolean hasAny(Predicate predicate) {
      for(int i = 0; i < this.size; ++i) {
         if (predicate.test(this.array[i])) {
            return true;
         }
      }

      return false;
   }

   public Object get(int id) {
      if (id >= 0 && id < this.size) {
         return this.array[id];
      } else {
         throw new EntryMissingException(id);
      }
   }

   public void readPacket(PacketByteBuf buf) {
      this.size = buf.readVarInt();

      for(int i = 0; i < this.size; ++i) {
         this.array[i] = this.idList.getOrThrow(buf.readVarInt());
      }

   }

   public void writePacket(PacketByteBuf buf) {
      buf.writeVarInt(this.size);

      for(int i = 0; i < this.size; ++i) {
         buf.writeVarInt(this.idList.getRawId(this.array[i]));
      }

   }

   public int getPacketSize() {
      int i = VarInts.getSizeInBytes(this.getSize());

      for(int j = 0; j < this.getSize(); ++j) {
         i += VarInts.getSizeInBytes(this.idList.getRawId(this.array[j]));
      }

      return i;
   }

   public int getSize() {
      return this.size;
   }

   public Palette copy(PaletteResizeListener resizeListener) {
      return new ArrayPalette(this.idList, (Object[])this.array.clone(), resizeListener, this.indexBits, this.size);
   }
}
