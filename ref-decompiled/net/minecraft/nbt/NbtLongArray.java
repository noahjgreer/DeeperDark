package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;
import org.apache.commons.lang3.ArrayUtils;

public final class NbtLongArray implements AbstractNbtList {
   private static final int SIZE = 24;
   public static final NbtType TYPE = new NbtType.OfVariableSize() {
      public NbtLongArray read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
         return new NbtLongArray(readLongArray(dataInput, nbtSizeTracker));
      }

      public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
         return visitor.visitLongArray(readLongArray(input, tracker));
      }

      private static long[] readLongArray(DataInput input, NbtSizeTracker tracker) throws IOException {
         tracker.add(24L);
         int i = input.readInt();
         tracker.add(8L, (long)i);
         long[] ls = new long[i];

         for(int j = 0; j < i; ++j) {
            ls[j] = input.readLong();
         }

         return ls;
      }

      public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
         input.skipBytes(input.readInt() * 8);
      }

      public String getCrashReportName() {
         return "LONG[]";
      }

      public String getCommandFeedbackName() {
         return "TAG_Long_Array";
      }

      // $FF: synthetic method
      public NbtElement read(final DataInput input, final NbtSizeTracker tracker) throws IOException {
         return this.read(input, tracker);
      }
   };
   private long[] value;

   public NbtLongArray(long[] value) {
      this.value = value;
   }

   public void write(DataOutput output) throws IOException {
      output.writeInt(this.value.length);
      long[] var2 = this.value;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long l = var2[var4];
         output.writeLong(l);
      }

   }

   public int getSizeInBytes() {
      return 24 + 8 * this.value.length;
   }

   public byte getType() {
      return 12;
   }

   public NbtType getNbtType() {
      return TYPE;
   }

   public String toString() {
      StringNbtWriter stringNbtWriter = new StringNbtWriter();
      stringNbtWriter.visitLongArray(this);
      return stringNbtWriter.getString();
   }

   public NbtLongArray copy() {
      long[] ls = new long[this.value.length];
      System.arraycopy(this.value, 0, ls, 0, this.value.length);
      return new NbtLongArray(ls);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o instanceof NbtLongArray && Arrays.equals(this.value, ((NbtLongArray)o).value);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.value);
   }

   public void accept(NbtElementVisitor visitor) {
      visitor.visitLongArray(this);
   }

   public long[] getLongArray() {
      return this.value;
   }

   public int size() {
      return this.value.length;
   }

   public NbtLong method_10534(int i) {
      return NbtLong.of(this.value[i]);
   }

   public boolean setElement(int index, NbtElement element) {
      if (element instanceof AbstractNbtNumber abstractNbtNumber) {
         this.value[index] = abstractNbtNumber.longValue();
         return true;
      } else {
         return false;
      }
   }

   public boolean addElement(int index, NbtElement element) {
      if (element instanceof AbstractNbtNumber abstractNbtNumber) {
         this.value = ArrayUtils.add(this.value, index, abstractNbtNumber.longValue());
         return true;
      } else {
         return false;
      }
   }

   public NbtLong method_10536(int i) {
      long l = this.value[i];
      this.value = ArrayUtils.remove(this.value, i);
      return NbtLong.of(l);
   }

   public void clear() {
      this.value = new long[0];
   }

   public Optional asLongArray() {
      return Optional.of(this.value);
   }

   public NbtScanner.Result doAccept(NbtScanner visitor) {
      return visitor.visitLongArray(this.value);
   }

   // $FF: synthetic method
   public NbtElement method_10534(final int i) {
      return this.method_10534(i);
   }

   // $FF: synthetic method
   public NbtElement method_10536(final int i) {
      return this.method_10536(i);
   }

   // $FF: synthetic method
   public NbtElement copy() {
      return this.copy();
   }
}
