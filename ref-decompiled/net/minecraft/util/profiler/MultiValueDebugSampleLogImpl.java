package net.minecraft.util.profiler;

import net.minecraft.util.profiler.log.ArrayDebugSampleLog;
import net.minecraft.util.profiler.log.MultiValueDebugSampleLog;

public class MultiValueDebugSampleLogImpl extends ArrayDebugSampleLog implements MultiValueDebugSampleLog {
   public static final int LOG_SIZE = 240;
   private final long[][] multiValues;
   private int start;
   private int length;

   public MultiValueDebugSampleLogImpl(int dimensions) {
      this(dimensions, new long[dimensions]);
   }

   public MultiValueDebugSampleLogImpl(int i, long[] ls) {
      super(i, ls);
      this.multiValues = new long[240][i];
   }

   protected void onPush() {
      int i = this.wrap(this.start + this.length);
      System.arraycopy(this.values, 0, this.multiValues[i], 0, this.values.length);
      if (this.length < 240) {
         ++this.length;
      } else {
         this.start = this.wrap(this.start + 1);
      }

   }

   public int getDimension() {
      return this.multiValues.length;
   }

   public int getLength() {
      return this.length;
   }

   public long get(int index) {
      return this.get(index, 0);
   }

   public long get(int index, int dimension) {
      if (index >= 0 && index < this.length) {
         long[] ls = this.multiValues[this.wrap(this.start + index)];
         if (dimension >= 0 && dimension < ls.length) {
            return ls[dimension];
         } else {
            throw new IndexOutOfBoundsException("" + dimension + " out of bounds for dimensions " + ls.length);
         }
      } else {
         throw new IndexOutOfBoundsException("" + index + " out of bounds for length " + this.length);
      }
   }

   private int wrap(int index) {
      return index % 240;
   }

   public void clear() {
      this.start = 0;
      this.length = 0;
   }
}
