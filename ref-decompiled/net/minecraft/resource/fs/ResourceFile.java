package net.minecraft.resource.fs;

import java.nio.file.Path;
import java.util.Map;

interface ResourceFile {
   ResourceFile EMPTY = new ResourceFile() {
      public String toString() {
         return "empty";
      }
   };
   ResourceFile RELATIVE = new ResourceFile() {
      public String toString() {
         return "relative";
      }
   };

   public static record Directory(Map children) implements ResourceFile {
      public Directory(Map map) {
         this.children = map;
      }

      public Map children() {
         return this.children;
      }
   }

   public static record File(Path contents) implements ResourceFile {
      public File(Path path) {
         this.contents = path;
      }

      public Path contents() {
         return this.contents;
      }
   }
}
