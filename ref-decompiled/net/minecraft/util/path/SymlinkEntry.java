package net.minecraft.util.path;

import java.nio.file.Path;

public record SymlinkEntry(Path link, Path target) {
   public SymlinkEntry(Path path, Path path2) {
      this.link = path;
      this.target = path2;
   }

   public Path link() {
      return this.link;
   }

   public Path target() {
      return this.target;
   }
}
