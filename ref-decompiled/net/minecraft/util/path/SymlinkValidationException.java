package net.minecraft.util.path;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class SymlinkValidationException extends Exception {
   private final Path path;
   private final List symlinks;

   public SymlinkValidationException(Path path, List symlinks) {
      this.path = path;
      this.symlinks = symlinks;
   }

   public String getMessage() {
      return getMessage(this.path, this.symlinks);
   }

   public static String getMessage(Path path, List symlinks) {
      String var10000 = String.valueOf(path);
      return "Failed to validate '" + var10000 + "'. Found forbidden symlinks: " + (String)symlinks.stream().map((symlink) -> {
         String var10000 = String.valueOf(symlink.link());
         return var10000 + "->" + String.valueOf(symlink.target());
      }).collect(Collectors.joining(", "));
   }
}
