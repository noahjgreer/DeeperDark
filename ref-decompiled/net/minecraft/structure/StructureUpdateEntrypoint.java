package net.minecraft.structure;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import net.minecraft.Bootstrap;
import net.minecraft.MinecraftVersion;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataWriter;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;

public class StructureUpdateEntrypoint {
   public static void main(String[] args) throws IOException {
      SharedConstants.setGameVersion(MinecraftVersion.CURRENT);
      Bootstrap.initialize();
      String[] var1 = args;
      int var2 = args.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String string = var1[var3];
         update(string);
      }

   }

   private static void update(String directory) throws IOException {
      Stream stream = Files.walk(Paths.get(directory));

      try {
         stream.filter((path) -> {
            return path.toString().endsWith(".snbt");
         }).forEach((path) -> {
            try {
               String string = Files.readString(path);
               NbtCompound nbtCompound = NbtHelper.fromNbtProviderString(string);
               NbtCompound nbtCompound2 = StructureValidatorProvider.update(path.toString(), nbtCompound);
               NbtProvider.writeTo(DataWriter.UNCACHED, path, NbtHelper.toNbtProviderString(nbtCompound2));
            } catch (IOException | CommandSyntaxException var4) {
               throw new RuntimeException(var4);
            }
         });
      } catch (Throwable var5) {
         if (stream != null) {
            try {
               stream.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (stream != null) {
         stream.close();
      }

   }
}
