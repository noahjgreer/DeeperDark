package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;

public interface NbtType {
   NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException;

   NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException;

   default void accept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
      switch (visitor.start(this)) {
         case CONTINUE:
            this.doAccept(input, visitor, tracker);
         case HALT:
         default:
            break;
         case BREAK:
            this.skip(input, tracker);
      }

   }

   void skip(DataInput input, int count, NbtSizeTracker tracker) throws IOException;

   void skip(DataInput input, NbtSizeTracker tracker) throws IOException;

   String getCrashReportName();

   String getCommandFeedbackName();

   static NbtType createInvalid(final int type) {
      return new NbtType() {
         private IOException createException() {
            return new IOException("Invalid tag id: " + type);
         }

         public NbtEnd read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
            throw this.createException();
         }

         public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
            throw this.createException();
         }

         public void skip(DataInput input, int count, NbtSizeTracker tracker) throws IOException {
            throw this.createException();
         }

         public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
            throw this.createException();
         }

         public String getCrashReportName() {
            return "INVALID[" + type + "]";
         }

         public String getCommandFeedbackName() {
            return "UNKNOWN_" + type;
         }

         // $FF: synthetic method
         public NbtElement read(final DataInput input, final NbtSizeTracker tracker) throws IOException {
            return this.read(input, tracker);
         }
      };
   }

   public interface OfVariableSize extends NbtType {
      default void skip(DataInput input, int count, NbtSizeTracker tracker) throws IOException {
         for(int i = 0; i < count; ++i) {
            this.skip(input, tracker);
         }

      }
   }

   public interface OfFixedSize extends NbtType {
      default void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
         input.skipBytes(this.getSizeInBytes());
      }

      default void skip(DataInput input, int count, NbtSizeTracker tracker) throws IOException {
         input.skipBytes(this.getSizeInBytes() * count);
      }

      int getSizeInBytes();
   }
}
