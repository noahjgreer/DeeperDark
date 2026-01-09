package net.minecraft.util.path;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;

public class CacheFiles {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void clear(Path directory, int maxRetained) {
      try {
         List list = findCacheFiles(directory);
         int i = list.size() - maxRetained;
         if (i <= 0) {
            return;
         }

         list.sort(CacheFiles.CacheFile.COMPARATOR);
         List list2 = toCacheEntries(list);
         Collections.reverse(list2);
         list2.sort(CacheFiles.CacheEntry.COMPARATOR);
         Set set = new HashSet();

         for(int j = 0; j < i; ++j) {
            CacheEntry cacheEntry = (CacheEntry)list2.get(j);
            Path path = cacheEntry.path;

            try {
               Files.delete(path);
               if (cacheEntry.removalPriority == 0) {
                  set.add(path.getParent());
               }
            } catch (IOException var12) {
               LOGGER.warn("Failed to delete cache file {}", path, var12);
            }
         }

         set.remove(directory);
         Iterator var14 = set.iterator();

         while(var14.hasNext()) {
            Path path2 = (Path)var14.next();

            try {
               Files.delete(path2);
            } catch (DirectoryNotEmptyException var10) {
            } catch (IOException var11) {
               LOGGER.warn("Failed to delete empty(?) cache directory {}", path2, var11);
            }
         }
      } catch (UncheckedIOException | IOException var13) {
         LOGGER.error("Failed to vacuum cache dir {}", directory, var13);
      }

   }

   private static List findCacheFiles(final Path directory) throws IOException {
      try {
         final List list = new ArrayList();
         Files.walkFileTree(directory, new SimpleFileVisitor() {
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
               if (basicFileAttributes.isRegularFile() && !path.getParent().equals(directory)) {
                  FileTime fileTime = basicFileAttributes.lastModifiedTime();
                  list.add(new CacheFile(path, fileTime));
               }

               return FileVisitResult.CONTINUE;
            }

            // $FF: synthetic method
            public FileVisitResult visitFile(final Object path, final BasicFileAttributes attributes) throws IOException {
               return this.visitFile((Path)path, attributes);
            }
         });
         return list;
      } catch (NoSuchFileException var2) {
         return List.of();
      }
   }

   private static List toCacheEntries(List files) {
      List list = new ArrayList();
      Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
      Iterator var3 = files.iterator();

      while(var3.hasNext()) {
         CacheFile cacheFile = (CacheFile)var3.next();
         int i = object2IntOpenHashMap.addTo(cacheFile.path.getParent(), 1);
         list.add(new CacheEntry(cacheFile.path, i));
      }

      return list;
   }

   private static record CacheFile(Path path, FileTime modifiedTime) {
      final Path path;
      public static final Comparator COMPARATOR = Comparator.comparing(CacheFile::modifiedTime).reversed();

      CacheFile(Path path, FileTime fileTime) {
         this.path = path;
         this.modifiedTime = fileTime;
      }

      public Path path() {
         return this.path;
      }

      public FileTime modifiedTime() {
         return this.modifiedTime;
      }
   }

   static record CacheEntry(Path path, int removalPriority) {
      final Path path;
      final int removalPriority;
      public static final Comparator COMPARATOR = Comparator.comparing(CacheEntry::removalPriority).reversed();

      CacheEntry(Path path, int i) {
         this.path = path;
         this.removalPriority = i;
      }

      public Path path() {
         return this.path;
      }

      public int removalPriority() {
         return this.removalPriority;
      }
   }
}
