package net.minecraft.world.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.util.collection.IndexedIterable;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public class SingularPalette implements Palette {
   private final IndexedIterable idList;
   @Nullable
   private Object entry;
   private final PaletteResizeListener listener;

   public SingularPalette(IndexedIterable idList, PaletteResizeListener listener, List entries) {
      this.idList = idList;
      this.listener = listener;
      if (entries.size() > 0) {
         Validate.isTrue(entries.size() <= 1, "Can't initialize SingleValuePalette with %d values.", (long)entries.size());
         this.entry = entries.get(0);
      }

   }

   public static Palette create(int bitSize, IndexedIterable idList, PaletteResizeListener listener, List entries) {
      return new SingularPalette(idList, listener, entries);
   }

   public int index(Object object) {
      if (this.entry != null && this.entry != object) {
         return this.listener.onResize(1, object);
      } else {
         this.entry = object;
         return 0;
      }
   }

   public boolean hasAny(Predicate predicate) {
      if (this.entry == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return predicate.test(this.entry);
      }
   }

   public Object get(int id) {
      if (this.entry != null && id == 0) {
         return this.entry;
      } else {
         throw new IllegalStateException("Missing Palette entry for id " + id + ".");
      }
   }

   public void readPacket(PacketByteBuf buf) {
      this.entry = this.idList.getOrThrow(buf.readVarInt());
   }

   public void writePacket(PacketByteBuf buf) {
      if (this.entry == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         buf.writeVarInt(this.idList.getRawId(this.entry));
      }
   }

   public int getPacketSize() {
      if (this.entry == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return VarInts.getSizeInBytes(this.idList.getRawId(this.entry));
      }
   }

   public int getSize() {
      return 1;
   }

   public Palette copy(PaletteResizeListener resizeListener) {
      if (this.entry == null) {
         throw new IllegalStateException("Use of an uninitialized palette");
      } else {
         return this;
      }
   }
}
