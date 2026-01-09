package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

public record NbtLong(long value) implements AbstractNbtNumber {
   private static final int SIZE = 16;
   public static final NbtType TYPE = new NbtType.OfFixedSize() {
      public NbtLong read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
         return NbtLong.of(readLong(dataInput, nbtSizeTracker));
      }

      public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
         return visitor.visitLong(readLong(input, tracker));
      }

      private static long readLong(DataInput input, NbtSizeTracker tracker) throws IOException {
         tracker.add(16L);
         return input.readLong();
      }

      public int getSizeInBytes() {
         return 8;
      }

      public String getCrashReportName() {
         return "LONG";
      }

      public String getCommandFeedbackName() {
         return "TAG_Long";
      }

      // $FF: synthetic method
      public NbtElement read(final DataInput input, final NbtSizeTracker tracker) throws IOException {
         return this.read(input, tracker);
      }
   };

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public NbtLong(long value) {
      this.value = value;
   }

   public static NbtLong of(long value) {
      return value >= -128L && value <= 1024L ? NbtLong.Cache.VALUES[(int)value - -128] : new NbtLong(value);
   }

   public void write(DataOutput output) throws IOException {
      output.writeLong(this.value);
   }

   public int getSizeInBytes() {
      return 16;
   }

   public byte getType() {
      return 4;
   }

   public NbtType getNbtType() {
      return TYPE;
   }

   public NbtLong copy() {
      return this;
   }

   public void accept(NbtElementVisitor visitor) {
      visitor.visitLong(this);
   }

   public long longValue() {
      return this.value;
   }

   public int intValue() {
      return (int)(this.value & -1L);
   }

   public short shortValue() {
      return (short)((int)(this.value & 65535L));
   }

   public byte byteValue() {
      return (byte)((int)(this.value & 255L));
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public Number numberValue() {
      return this.value;
   }

   public NbtScanner.Result doAccept(NbtScanner visitor) {
      return visitor.visitLong(this.value);
   }

   public String toString() {
      StringNbtWriter stringNbtWriter = new StringNbtWriter();
      stringNbtWriter.visitLong(this);
      return stringNbtWriter.getString();
   }

   public long value() {
      return this.value;
   }

   // $FF: synthetic method
   public NbtElement copy() {
      return this.copy();
   }

   static class Cache {
      private static final int MAX = 1024;
      private static final int MIN = -128;
      static final NbtLong[] VALUES = new NbtLong[1153];

      private Cache() {
      }

      static {
         for(int i = 0; i < VALUES.length; ++i) {
            VALUES[i] = new NbtLong((long)(-128 + i));
         }

      }
   }
}
