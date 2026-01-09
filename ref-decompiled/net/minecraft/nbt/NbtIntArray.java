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

public final class NbtIntArray implements AbstractNbtList {
   private static final int SIZE = 24;
   public static final NbtType TYPE = new NbtType.OfVariableSize() {
      public NbtIntArray read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
         return new NbtIntArray(readIntArray(dataInput, nbtSizeTracker));
      }

      public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
         return visitor.visitIntArray(readIntArray(input, tracker));
      }

      private static int[] readIntArray(DataInput input, NbtSizeTracker tracker) throws IOException {
         tracker.add(24L);
         int i = input.readInt();
         tracker.add(4L, (long)i);
         int[] is = new int[i];

         for(int j = 0; j < i; ++j) {
            is[j] = input.readInt();
         }

         return is;
      }

      public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
         input.skipBytes(input.readInt() * 4);
      }

      public String getCrashReportName() {
         return "INT[]";
      }

      public String getCommandFeedbackName() {
         return "TAG_Int_Array";
      }

      // $FF: synthetic method
      public NbtElement read(final DataInput input, final NbtSizeTracker tracker) throws IOException {
         return this.read(input, tracker);
      }
   };
   private int[] value;

   public NbtIntArray(int[] value) {
      this.value = value;
   }

   public void write(DataOutput output) throws IOException {
      output.writeInt(this.value.length);
      int[] var2 = this.value;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int i = var2[var4];
         output.writeInt(i);
      }

   }

   public int getSizeInBytes() {
      return 24 + 4 * this.value.length;
   }

   public byte getType() {
      return 11;
   }

   public NbtType getNbtType() {
      return TYPE;
   }

   public String toString() {
      StringNbtWriter stringNbtWriter = new StringNbtWriter();
      stringNbtWriter.visitIntArray(this);
      return stringNbtWriter.getString();
   }

   public NbtIntArray copy() {
      int[] is = new int[this.value.length];
      System.arraycopy(this.value, 0, is, 0, this.value.length);
      return new NbtIntArray(is);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o instanceof NbtIntArray && Arrays.equals(this.value, ((NbtIntArray)o).value);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.value);
   }

   public int[] getIntArray() {
      return this.value;
   }

   public void accept(NbtElementVisitor visitor) {
      visitor.visitIntArray(this);
   }

   public int size() {
      return this.value.length;
   }

   public NbtInt method_10534(int i) {
      return NbtInt.of(this.value[i]);
   }

   public boolean setElement(int index, NbtElement element) {
      if (element instanceof AbstractNbtNumber abstractNbtNumber) {
         this.value[index] = abstractNbtNumber.intValue();
         return true;
      } else {
         return false;
      }
   }

   public boolean addElement(int index, NbtElement element) {
      if (element instanceof AbstractNbtNumber abstractNbtNumber) {
         this.value = ArrayUtils.add(this.value, index, abstractNbtNumber.intValue());
         return true;
      } else {
         return false;
      }
   }

   public NbtInt method_10536(int i) {
      int j = this.value[i];
      this.value = ArrayUtils.remove(this.value, i);
      return NbtInt.of(j);
   }

   public void clear() {
      this.value = new int[0];
   }

   public Optional asIntArray() {
      return Optional.of(this.value);
   }

   public NbtScanner.Result doAccept(NbtScanner visitor) {
      return visitor.visitIntArray(this.value);
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
