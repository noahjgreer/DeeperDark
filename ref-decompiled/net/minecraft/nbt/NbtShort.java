package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

public record NbtShort(short value) implements AbstractNbtNumber {
   private static final int SIZE = 10;
   public static final NbtType TYPE = new NbtType.OfFixedSize() {
      public NbtShort read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
         return NbtShort.of(readShort(dataInput, nbtSizeTracker));
      }

      public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
         return visitor.visitShort(readShort(input, tracker));
      }

      private static short readShort(DataInput input, NbtSizeTracker tracker) throws IOException {
         tracker.add(10L);
         return input.readShort();
      }

      public int getSizeInBytes() {
         return 2;
      }

      public String getCrashReportName() {
         return "SHORT";
      }

      public String getCommandFeedbackName() {
         return "TAG_Short";
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
   public NbtShort(short value) {
      this.value = value;
   }

   public static NbtShort of(short value) {
      return value >= -128 && value <= 1024 ? NbtShort.Cache.VALUES[value - -128] : new NbtShort(value);
   }

   public void write(DataOutput output) throws IOException {
      output.writeShort(this.value);
   }

   public int getSizeInBytes() {
      return 10;
   }

   public byte getType() {
      return 2;
   }

   public NbtType getNbtType() {
      return TYPE;
   }

   public NbtShort copy() {
      return this;
   }

   public void accept(NbtElementVisitor visitor) {
      visitor.visitShort(this);
   }

   public long longValue() {
      return (long)this.value;
   }

   public int intValue() {
      return this.value;
   }

   public short shortValue() {
      return this.value;
   }

   public byte byteValue() {
      return (byte)(this.value & 255);
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
      return visitor.visitShort(this.value);
   }

   public String toString() {
      StringNbtWriter stringNbtWriter = new StringNbtWriter();
      stringNbtWriter.visitShort(this);
      return stringNbtWriter.getString();
   }

   public short value() {
      return this.value;
   }

   // $FF: synthetic method
   public NbtElement copy() {
      return this.copy();
   }

   static class Cache {
      private static final int MAX = 1024;
      private static final int MIN = -128;
      static final NbtShort[] VALUES = new NbtShort[1153];

      private Cache() {
      }

      static {
         for(int i = 0; i < VALUES.length; ++i) {
            VALUES[i] = new NbtShort((short)(-128 + i));
         }

      }
   }
}
