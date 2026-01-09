package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.util.DelegatingDataOutput;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;

public class NbtIo {
   private static final OpenOption[] OPEN_OPTIONS;

   public static NbtCompound readCompressed(Path path, NbtSizeTracker tagSizeTracker) throws IOException {
      InputStream inputStream = Files.newInputStream(path);

      NbtCompound var4;
      try {
         InputStream inputStream2 = new FixedBufferInputStream(inputStream);

         try {
            var4 = readCompressed((InputStream)inputStream2, tagSizeTracker);
         } catch (Throwable var8) {
            try {
               inputStream2.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         inputStream2.close();
      } catch (Throwable var9) {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if (inputStream != null) {
         inputStream.close();
      }

      return var4;
   }

   private static DataInputStream decompress(InputStream stream) throws IOException {
      return new DataInputStream(new FixedBufferInputStream(new GZIPInputStream(stream)));
   }

   private static DataOutputStream compress(OutputStream stream) throws IOException {
      return new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(stream)));
   }

   public static NbtCompound readCompressed(InputStream stream, NbtSizeTracker tagSizeTracker) throws IOException {
      DataInputStream dataInputStream = decompress(stream);

      NbtCompound var3;
      try {
         var3 = readCompound(dataInputStream, tagSizeTracker);
      } catch (Throwable var6) {
         if (dataInputStream != null) {
            try {
               dataInputStream.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (dataInputStream != null) {
         dataInputStream.close();
      }

      return var3;
   }

   public static void scanCompressed(Path path, NbtScanner scanner, NbtSizeTracker tracker) throws IOException {
      InputStream inputStream = Files.newInputStream(path);

      try {
         InputStream inputStream2 = new FixedBufferInputStream(inputStream);

         try {
            scanCompressed((InputStream)inputStream2, scanner, tracker);
         } catch (Throwable var9) {
            try {
               inputStream2.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         inputStream2.close();
      } catch (Throwable var10) {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (inputStream != null) {
         inputStream.close();
      }

   }

   public static void scanCompressed(InputStream stream, NbtScanner scanner, NbtSizeTracker tracker) throws IOException {
      DataInputStream dataInputStream = decompress(stream);

      try {
         scan(dataInputStream, scanner, tracker);
      } catch (Throwable var7) {
         if (dataInputStream != null) {
            try {
               dataInputStream.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (dataInputStream != null) {
         dataInputStream.close();
      }

   }

   public static void writeCompressed(NbtCompound nbt, Path path) throws IOException {
      OutputStream outputStream = Files.newOutputStream(path, OPEN_OPTIONS);

      try {
         OutputStream outputStream2 = new BufferedOutputStream(outputStream);

         try {
            writeCompressed(nbt, (OutputStream)outputStream2);
         } catch (Throwable var8) {
            try {
               outputStream2.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         outputStream2.close();
      } catch (Throwable var9) {
         if (outputStream != null) {
            try {
               outputStream.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if (outputStream != null) {
         outputStream.close();
      }

   }

   public static void writeCompressed(NbtCompound nbt, OutputStream stream) throws IOException {
      DataOutputStream dataOutputStream = compress(stream);

      try {
         writeCompound(nbt, dataOutputStream);
      } catch (Throwable var6) {
         if (dataOutputStream != null) {
            try {
               dataOutputStream.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (dataOutputStream != null) {
         dataOutputStream.close();
      }

   }

   public static void write(NbtCompound nbt, Path path) throws IOException {
      OutputStream outputStream = Files.newOutputStream(path, OPEN_OPTIONS);

      try {
         OutputStream outputStream2 = new BufferedOutputStream(outputStream);

         try {
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream2);

            try {
               writeCompound(nbt, dataOutputStream);
            } catch (Throwable var10) {
               try {
                  dataOutputStream.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            dataOutputStream.close();
         } catch (Throwable var11) {
            try {
               outputStream2.close();
            } catch (Throwable var8) {
               var11.addSuppressed(var8);
            }

            throw var11;
         }

         outputStream2.close();
      } catch (Throwable var12) {
         if (outputStream != null) {
            try {
               outputStream.close();
            } catch (Throwable var7) {
               var12.addSuppressed(var7);
            }
         }

         throw var12;
      }

      if (outputStream != null) {
         outputStream.close();
      }

   }

   @Nullable
   public static NbtCompound read(Path path) throws IOException {
      if (!Files.exists(path, new LinkOption[0])) {
         return null;
      } else {
         InputStream inputStream = Files.newInputStream(path);

         NbtCompound var3;
         try {
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            try {
               var3 = readCompound(dataInputStream, NbtSizeTracker.ofUnlimitedBytes());
            } catch (Throwable var7) {
               try {
                  dataInputStream.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            dataInputStream.close();
         } catch (Throwable var8) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var5) {
                  var8.addSuppressed(var5);
               }
            }

            throw var8;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         return var3;
      }
   }

   public static NbtCompound readCompound(DataInput input) throws IOException {
      return readCompound(input, NbtSizeTracker.ofUnlimitedBytes());
   }

   public static NbtCompound readCompound(DataInput input, NbtSizeTracker tracker) throws IOException {
      NbtElement nbtElement = readElement(input, tracker);
      if (nbtElement instanceof NbtCompound) {
         return (NbtCompound)nbtElement;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void writeCompound(NbtCompound nbt, DataOutput output) throws IOException {
      write((NbtElement)nbt, (DataOutput)output);
   }

   public static void scan(DataInput input, NbtScanner scanner, NbtSizeTracker tracker) throws IOException {
      NbtType nbtType = NbtTypes.byId(input.readByte());
      if (nbtType == NbtEnd.TYPE) {
         if (scanner.start(NbtEnd.TYPE) == NbtScanner.Result.CONTINUE) {
            scanner.visitEnd();
         }

      } else {
         switch (scanner.start(nbtType)) {
            case HALT:
            default:
               break;
            case BREAK:
               NbtString.skip(input);
               nbtType.skip(input, tracker);
               break;
            case CONTINUE:
               NbtString.skip(input);
               nbtType.doAccept(input, scanner, tracker);
         }

      }
   }

   public static NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
      byte b = input.readByte();
      return (NbtElement)(b == 0 ? NbtEnd.INSTANCE : readElement(input, tracker, b));
   }

   public static void writeForPacket(NbtElement nbt, DataOutput output) throws IOException {
      output.writeByte(nbt.getType());
      if (nbt.getType() != 0) {
         nbt.write(output);
      }
   }

   public static void writeUnsafe(NbtElement nbt, DataOutput output) throws IOException {
      output.writeByte(nbt.getType());
      if (nbt.getType() != 0) {
         output.writeUTF("");
         nbt.write(output);
      }
   }

   public static void write(NbtElement nbt, DataOutput output) throws IOException {
      writeUnsafe(nbt, new InvalidUtfSkippingDataOutput(output));
   }

   @VisibleForTesting
   public static NbtElement readElement(DataInput input, NbtSizeTracker tracker) throws IOException {
      byte b = input.readByte();
      if (b == 0) {
         return NbtEnd.INSTANCE;
      } else {
         NbtString.skip(input);
         return readElement(input, tracker, b);
      }
   }

   private static NbtElement readElement(DataInput input, NbtSizeTracker tracker, byte typeId) {
      try {
         return NbtTypes.byId(typeId).read(input, tracker);
      } catch (IOException var6) {
         CrashReport crashReport = CrashReport.create(var6, "Loading NBT data");
         CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
         crashReportSection.add("Tag type", (Object)typeId);
         throw new NbtCrashException(crashReport);
      }
   }

   static {
      OPEN_OPTIONS = new OpenOption[]{StandardOpenOption.SYNC, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
   }

   public static class InvalidUtfSkippingDataOutput extends DelegatingDataOutput {
      public InvalidUtfSkippingDataOutput(DataOutput dataOutput) {
         super(dataOutput);
      }

      public void writeUTF(String string) throws IOException {
         try {
            super.writeUTF(string);
         } catch (UTFDataFormatException var3) {
            Util.logErrorOrPause("Failed to write NBT String", var3);
            super.writeUTF("");
         }

      }
   }
}
