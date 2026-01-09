package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.util.math.MathHelper;

public record NbtDouble(double value) implements AbstractNbtNumber {
   private static final int SIZE = 16;
   public static final NbtDouble ZERO = new NbtDouble(0.0);
   public static final NbtType TYPE = new NbtType.OfFixedSize() {
      public NbtDouble read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
         return NbtDouble.of(readDouble(dataInput, nbtSizeTracker));
      }

      public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
         return visitor.visitDouble(readDouble(input, tracker));
      }

      private static double readDouble(DataInput input, NbtSizeTracker tracker) throws IOException {
         tracker.add(16L);
         return input.readDouble();
      }

      public int getSizeInBytes() {
         return 8;
      }

      public String getCrashReportName() {
         return "DOUBLE";
      }

      public String getCommandFeedbackName() {
         return "TAG_Double";
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
   public NbtDouble(double value) {
      this.value = value;
   }

   public static NbtDouble of(double value) {
      return value == 0.0 ? ZERO : new NbtDouble(value);
   }

   public void write(DataOutput output) throws IOException {
      output.writeDouble(this.value);
   }

   public int getSizeInBytes() {
      return 16;
   }

   public byte getType() {
      return 6;
   }

   public NbtType getNbtType() {
      return TYPE;
   }

   public NbtDouble copy() {
      return this;
   }

   public void accept(NbtElementVisitor visitor) {
      visitor.visitDouble(this);
   }

   public long longValue() {
      return (long)Math.floor(this.value);
   }

   public int intValue() {
      return MathHelper.floor(this.value);
   }

   public short shortValue() {
      return (short)(MathHelper.floor(this.value) & '\uffff');
   }

   public byte byteValue() {
      return (byte)(MathHelper.floor(this.value) & 255);
   }

   public double doubleValue() {
      return this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public Number numberValue() {
      return this.value;
   }

   public NbtScanner.Result doAccept(NbtScanner visitor) {
      return visitor.visitDouble(this.value);
   }

   public String toString() {
      StringNbtWriter stringNbtWriter = new StringNbtWriter();
      stringNbtWriter.visitDouble(this);
      return stringNbtWriter.getString();
   }

   public double value() {
      return this.value;
   }

   // $FF: synthetic method
   public NbtElement copy() {
      return this.copy();
   }
}
