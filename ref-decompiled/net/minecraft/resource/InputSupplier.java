package net.minecraft.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@FunctionalInterface
public interface InputSupplier {
   static InputSupplier create(Path path) {
      return () -> {
         return Files.newInputStream(path);
      };
   }

   static InputSupplier create(ZipFile zipFile, ZipEntry zipEntry) {
      return () -> {
         return zipFile.getInputStream(zipEntry);
      };
   }

   Object get() throws IOException;
}
