package net.minecraft.resource.fs;

import com.google.common.base.Splitter;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public class ResourceFileSystem extends FileSystem {
   private static final Set SUPPORTED_FILE_ATTRIBUTE_VIEWS = Set.of("basic");
   public static final String SEPARATOR = "/";
   private static final Splitter SEPARATOR_SPLITTER = Splitter.on('/');
   private final FileStore store;
   private final FileSystemProvider fileSystemProvider = new ResourceFileSystemProvider();
   private final ResourcePath root;

   ResourceFileSystem(String name, Directory root) {
      this.store = new ResourceFileStore(name);
      this.root = toResourcePath(root, this, "", (ResourcePath)null);
   }

   private static ResourcePath toResourcePath(Directory root, ResourceFileSystem fileSystem, String name, @Nullable ResourcePath parent) {
      Object2ObjectOpenHashMap object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
      ResourcePath resourcePath = new ResourcePath(fileSystem, name, parent, new ResourceFile.Directory(object2ObjectOpenHashMap));
      root.files.forEach((fileName, path) -> {
         object2ObjectOpenHashMap.put(fileName, new ResourcePath(fileSystem, fileName, resourcePath, new ResourceFile.File(path)));
      });
      root.children.forEach((directoryName, directory) -> {
         object2ObjectOpenHashMap.put(directoryName, toResourcePath(directory, fileSystem, directoryName, resourcePath));
      });
      object2ObjectOpenHashMap.trim();
      return resourcePath;
   }

   public FileSystemProvider provider() {
      return this.fileSystemProvider;
   }

   public void close() {
   }

   public boolean isOpen() {
      return true;
   }

   public boolean isReadOnly() {
      return true;
   }

   public String getSeparator() {
      return "/";
   }

   public Iterable getRootDirectories() {
      return List.of(this.root);
   }

   public Iterable getFileStores() {
      return List.of(this.store);
   }

   public Set supportedFileAttributeViews() {
      return SUPPORTED_FILE_ATTRIBUTE_VIEWS;
   }

   public Path getPath(String first, String... more) {
      Stream stream = Stream.of(first);
      if (more.length > 0) {
         stream = Stream.concat(stream, Stream.of(more));
      }

      String string = (String)stream.collect(Collectors.joining("/"));
      if (string.equals("/")) {
         return this.root;
      } else {
         ResourcePath resourcePath;
         Iterator var6;
         String string2;
         if (string.startsWith("/")) {
            resourcePath = this.root;

            for(var6 = SEPARATOR_SPLITTER.split(string.substring(1)).iterator(); var6.hasNext(); resourcePath = resourcePath.get(string2)) {
               string2 = (String)var6.next();
               if (string2.isEmpty()) {
                  throw new IllegalArgumentException("Empty paths not allowed");
               }
            }

            return resourcePath;
         } else {
            resourcePath = null;

            for(var6 = SEPARATOR_SPLITTER.split(string).iterator(); var6.hasNext(); resourcePath = new ResourcePath(this, string2, resourcePath, ResourceFile.RELATIVE)) {
               string2 = (String)var6.next();
               if (string2.isEmpty()) {
                  throw new IllegalArgumentException("Empty paths not allowed");
               }
            }

            if (resourcePath == null) {
               throw new IllegalArgumentException("Empty paths not allowed");
            } else {
               return resourcePath;
            }
         }
      }
   }

   public PathMatcher getPathMatcher(String syntaxAndPattern) {
      throw new UnsupportedOperationException();
   }

   public UserPrincipalLookupService getUserPrincipalLookupService() {
      throw new UnsupportedOperationException();
   }

   public WatchService newWatchService() {
      throw new UnsupportedOperationException();
   }

   public FileStore getStore() {
      return this.store;
   }

   public ResourcePath getRoot() {
      return this.root;
   }

   public static Builder builder() {
      return new Builder();
   }

   private static record Directory(Map children, Map files) {
      final Map children;
      final Map files;

      public Directory() {
         this(new HashMap(), new HashMap());
      }

      private Directory(Map map, Map map2) {
         this.children = map;
         this.files = map2;
      }

      public Map children() {
         return this.children;
      }

      public Map files() {
         return this.files;
      }
   }

   public static class Builder {
      private final Directory root = new Directory();

      public Builder withFile(List directories, String name, Path path) {
         Directory directory = this.root;

         String string;
         for(Iterator var5 = directories.iterator(); var5.hasNext(); directory = (Directory)directory.children.computeIfAbsent(string, (directoryx) -> {
            return new Directory();
         })) {
            string = (String)var5.next();
         }

         directory.files.put(name, path);
         return this;
      }

      public Builder withFile(List directories, Path path) {
         if (directories.isEmpty()) {
            throw new IllegalArgumentException("Path can't be empty");
         } else {
            int i = directories.size() - 1;
            return this.withFile(directories.subList(0, i), (String)directories.get(i), path);
         }
      }

      public FileSystem build(String name) {
         return new ResourceFileSystem(name, this.root);
      }
   }
}
