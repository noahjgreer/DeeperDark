package net.minecraft.resource;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.util.Util;
import net.minecraft.util.path.PathResolving;
import org.slf4j.Logger;

public class DefaultResourcePackBuilder {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static Consumer callback = (builder) -> {
   };
   private static final Map RESOURCE_TYPE_TO_PATH = (Map)Util.make(() -> {
      Class var0 = DefaultResourcePack.class;
      synchronized(DefaultResourcePack.class) {
         ImmutableMap.Builder builder = ImmutableMap.builder();
         ResourceType[] var2 = ResourceType.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ResourceType resourceType = var2[var4];
            String string = "/" + resourceType.getDirectory() + "/.mcassetsroot";
            URL uRL = DefaultResourcePack.class.getResource(string);
            if (uRL == null) {
               LOGGER.error("File {} does not exist in classpath", string);
            } else {
               try {
                  URI uRI = uRL.toURI();
                  String string2 = uRI.getScheme();
                  if (!"jar".equals(string2) && !"file".equals(string2)) {
                     LOGGER.warn("Assets URL '{}' uses unexpected schema", uRI);
                  }

                  Path path = PathResolving.toPath(uRI);
                  builder.put(resourceType, path.getParent());
               } catch (Exception var12) {
                  LOGGER.error("Couldn't resolve path to vanilla assets", var12);
               }
            }
         }

         return builder.build();
      }
   });
   private final Set rootPaths = new LinkedHashSet();
   private final Map paths = new EnumMap(ResourceType.class);
   private ResourceMetadataMap metadataMap = ResourceMetadataMap.of();
   private final Set namespaces = new HashSet();

   private boolean exists(Path path) {
      if (!Files.exists(path, new LinkOption[0])) {
         return false;
      } else if (!Files.isDirectory(path, new LinkOption[0])) {
         throw new IllegalArgumentException("Path " + String.valueOf(path.toAbsolutePath()) + " is not directory");
      } else {
         return true;
      }
   }

   private void addRootPath(Path path) {
      if (this.exists(path)) {
         this.rootPaths.add(path);
      }

   }

   private void addPath(ResourceType type, Path path) {
      if (this.exists(path)) {
         ((Set)this.paths.computeIfAbsent(type, (type2) -> {
            return new LinkedHashSet();
         })).add(path);
      }

   }

   public DefaultResourcePackBuilder withDefaultPaths() {
      RESOURCE_TYPE_TO_PATH.forEach((type, path) -> {
         this.addRootPath(path.getParent());
         this.addPath(type, path);
      });
      return this;
   }

   public DefaultResourcePackBuilder withPaths(ResourceType type, Class clazz) {
      Enumeration enumeration = null;

      try {
         enumeration = clazz.getClassLoader().getResources(type.getDirectory() + "/");
      } catch (IOException var8) {
      }

      while(enumeration != null && enumeration.hasMoreElements()) {
         URL uRL = (URL)enumeration.nextElement();

         try {
            URI uRI = uRL.toURI();
            if ("file".equals(uRI.getScheme())) {
               Path path = Paths.get(uRI);
               this.addRootPath(path.getParent());
               this.addPath(type, path);
            }
         } catch (Exception var7) {
            LOGGER.error("Failed to extract path from {}", uRL, var7);
         }
      }

      return this;
   }

   public DefaultResourcePackBuilder runCallback() {
      callback.accept(this);
      return this;
   }

   public DefaultResourcePackBuilder withRoot(Path root) {
      this.addRootPath(root);
      ResourceType[] var2 = ResourceType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ResourceType resourceType = var2[var4];
         this.addPath(resourceType, root.resolve(resourceType.getDirectory()));
      }

      return this;
   }

   public DefaultResourcePackBuilder withPath(ResourceType type, Path path) {
      this.addRootPath(path);
      this.addPath(type, path);
      return this;
   }

   public DefaultResourcePackBuilder withMetadataMap(ResourceMetadataMap metadataMap) {
      this.metadataMap = metadataMap;
      return this;
   }

   public DefaultResourcePackBuilder withNamespaces(String... namespaces) {
      this.namespaces.addAll(Arrays.asList(namespaces));
      return this;
   }

   public DefaultResourcePack build(ResourcePackInfo info) {
      return new DefaultResourcePack(info, this.metadataMap, Set.copyOf(this.namespaces), reverse(this.rootPaths), Util.mapEnum(ResourceType.class, (type) -> {
         return reverse((Collection)this.paths.getOrDefault(type, Set.of()));
      }));
   }

   private static List reverse(Collection paths) {
      List list = new ArrayList(paths);
      Collections.reverse(list);
      return List.copyOf(list);
   }
}
