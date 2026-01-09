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

public final class NbtByteArray implements AbstractNbtList {
   private static final int SIZE = 24;
   public static final NbtType TYPE = new NbtType.OfVariableSize() {
      public NbtByteArray read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
         return new NbtByteArray(readByteArray(dataInput, nbtSizeTracker));
      }

      public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
         return visitor.visitByteArray(readByteArray(input, tracker));
      }

      private static byte[] readByteArray(DataInput input, NbtSizeTracker tracker) throws IOException {
         tracker.add(24L);
         int i = input.readInt();
         tracker.add(1L, (long)i);
         byte[] bs = new byte[i];
         input.readFully(bs);
         return bs;
      }

      public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
         input.skipBytes(input.readInt() * 1);
      }

      public String getCrashReportName() {
         return "BYTE[]";
      }

      public String getCommandFeedbackName() {
         return "TAG_Byte_Array";
      }

      // $FF: synthetic method
      public NbtElement read(final DataInput input, final NbtSizeTracker tracker) throws IOException {
         return this.read(input, tracker);
      }
   };
   private byte[] value;

   public NbtByteArray(byte[] value) {
      this.value = value;
   }

   public void write(DataOutput output) throws IOException {
      output.writeInt(this.value.length);
      output.write(this.value);
   }

   public int getSizeInBytes() {
      return 24 + 1 * this.value.length;
   }

   public byte getType() {
      return 7;
   }

   public NbtType getNbtType() {
      return TYPE;
   }

   public String toString() {
      StringNbtWriter stringNbtWriter = new StringNbtWriter();
      stringNbtWriter.visitByteArray(this);
      return stringNbtWriter.getString();
   }

   public NbtElement copy() {
      byte[] bs = new byte[this.value.length];
      System.arraycopy(this.value, 0, bs, 0, this.value.length);
      return new NbtByteArray(bs);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o instanceof NbtByteArray && Arrays.equals(this.value, ((NbtByteArray)o).value);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.value);
   }

   public void accept(NbtElementVisitor visitor) {
      visitor.visitByteArray(this);
   }

   public byte[] getByteArray() {
      return this.value;
   }

   public int size() {
      return this.value.length;
   }

   public NbtByte method_10534(int i) {
      return NbtByte.of(this.value[i]);
   }

   public boolean setElement(int index, NbtElement element) {
      if (element instanceof AbstractNbtNumber abstractNbtNumber) {
         this.value[index] = abstractNbtNumber.byteValue();
         return true;
      } else {
         return false;
      }
   }

   public boolean addElement(int index, NbtElement element) {
      if (element instanceof AbstractNbtNumber abstractNbtNumber) {
         this.value = ArrayUtils.add(this.value, index, abstractNbtNumber.byteValue());
         return true;
      } else {
         return false;
      }
   }

   public NbtByte method_10536(int i) {
      byte b = this.value[i];
      this.value = ArrayUtils.remove(this.value, i);
      return NbtByte.of(b);
   }

   public void clear() {
      this.value = new byte[0];
   }

   public Optional asByteArray() {
      return Optional.of(this.value);
   }

   public NbtScanner.Result doAccept(NbtScanner visitor) {
      return visitor.visitByteArray(this.value);
   }

   // $FF: synthetic method
   public NbtElement method_10534(final int i) {
      return this.method_10534(i);
   }

   // $FF: synthetic method
   public NbtElement method_10536(final int i) {
      return this.method_10536(i);
   }
}
