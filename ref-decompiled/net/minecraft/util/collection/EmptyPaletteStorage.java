package net.minecraft.util.collection;

import java.util.Arrays;
import java.util.function.IntConsumer;
import org.apache.commons.lang3.Validate;

public class EmptyPaletteStorage implements PaletteStorage {
   public static final long[] EMPTY_DATA = new long[0];
   private final int size;

   public EmptyPaletteStorage(int size) {
      this.size = size;
   }

   public int swap(int index, int value) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
      Validate.inclusiveBetween(0L, 0L, (long)value);
      return 0;
   }

   public void set(int index, int value) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
      Validate.inclusiveBetween(0L, 0L, (long)value);
   }

   public int get(int index) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
      return 0;
   }

   public long[] getData() {
      return EMPTY_DATA;
   }

   public int getSize() {
      return this.size;
   }

   public int getElementBits() {
      return 0;
   }

   public void forEach(IntConsumer action) {
      for(int i = 0; i < this.size; ++i) {
         action.accept(0);
      }

   }

   public void writePaletteIndices(int[] out) {
      Arrays.fill(out, 0, this.size, 0);
   }

   public PaletteStorage copy() {
      return this;
   }
}
