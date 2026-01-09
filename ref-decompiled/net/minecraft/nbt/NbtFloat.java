package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.util.math.MathHelper;

public record NbtFloat(float value) implements AbstractNbtNumber {
   private static final int SIZE = 12;
   public static final NbtFloat ZERO = new NbtFloat(0.0F);
   public static final NbtType TYPE = new NbtType.OfFixedSize() {
      public NbtFloat read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
         return NbtFloat.of(readFloat(dataInput, nbtSizeTracker));
      }

      public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
         return visitor.visitFloat(readFloat(input, tracker));
      }

      private static float readFloat(DataInput input, NbtSizeTracker tracker) throws IOException {
         tracker.add(12L);
         return input.readFloat();
      }

      public int getSizeInBytes() {
         return 4;
      }

      public String getCrashReportName() {
         return "FLOAT";
      }

      public String getCommandFeedbackName() {
         return "TAG_Float";
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
   public NbtFloat(float value) {
      this.value = value;
   }

   public static NbtFloat of(float value) {
      return value == 0.0F ? ZERO : new NbtFloat(value);
   }

   public void write(DataOutput output) throws IOException {
      output.writeFloat(this.value);
   }

   public int getSizeInBytes() {
      return 12;
   }

   public byte getType() {
      return 5;
   }

   public NbtType getNbtType() {
      return TYPE;
   }

   public NbtFloat copy() {
      return this;
   }

   public void accept(NbtElementVisitor visitor) {
      visitor.visitFloat(this);
   }

   public long longValue() {
      return (long)this.value;
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
      return (double)this.value;
   }

   public float floatValue() {
      return this.value;
   }

   public Number numberValue() {
      return this.value;
   }

   public NbtScanner.Result doAccept(NbtScanner visitor) {
      return visitor.visitFloat(this.value);
   }

   public String toString() {
      StringNbtWriter stringNbtWriter = new StringNbtWriter();
      stringNbtWriter.visitFloat(this);
      return stringNbtWriter.getString();
   }

   public float value() {
      return this.value;
   }

   // $FF: synthetic method
   public NbtElement copy() {
      return this.copy();
   }
}
