package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

public final class NbtEnd implements NbtElement {
   private static final int SIZE = 8;
   public static final NbtType TYPE = new NbtType() {
      public NbtEnd read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) {
         nbtSizeTracker.add(8L);
         return NbtEnd.INSTANCE;
      }

      public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) {
         tracker.add(8L);
         return visitor.visitEnd();
      }

      public void skip(DataInput input, int count, NbtSizeTracker tracker) {
      }

      public void skip(DataInput input, NbtSizeTracker tracker) {
      }

      public String getCrashReportName() {
         return "END";
      }

      public String getCommandFeedbackName() {
         return "TAG_End";
      }

      // $FF: synthetic method
      public NbtElement read(final DataInput input, final NbtSizeTracker tracker) throws IOException {
         return this.read(input, tracker);
      }
   };
   public static final NbtEnd INSTANCE = new NbtEnd();

   private NbtEnd() {
   }

   public void write(DataOutput output) throws IOException {
   }

   public int getSizeInBytes() {
      return 8;
   }

   public byte getType() {
      return 0;
   }

   public NbtType getNbtType() {
      return TYPE;
   }

   public String toString() {
      StringNbtWriter stringNbtWriter = new StringNbtWriter();
      stringNbtWriter.visitEnd(this);
      return stringNbtWriter.getString();
   }

   public NbtEnd copy() {
      return this;
   }

   public void accept(NbtElementVisitor visitor) {
      visitor.visitEnd(this);
   }

   public NbtScanner.Result doAccept(NbtScanner visitor) {
      return visitor.visitEnd();
   }

   // $FF: synthetic method
   public NbtElement copy() {
      return this.copy();
   }
}
