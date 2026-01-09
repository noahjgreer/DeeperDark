package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.path.PathUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DefaultResourcePack implements ResourcePack {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ResourcePackInfo info;
   private final ResourceMetadataMap metadata;
   private final Set namespaces;
   private final List rootPaths;
   private final Map namespacePaths;

   DefaultResourcePack(ResourcePackInfo info, ResourceMetadataMap metadata, Set namespaces, List rootPaths, Map namespacePaths) {
      this.info = info;
      this.metadata = metadata;
      this.namespaces = namespaces;
      this.rootPaths = rootPaths;
      this.namespacePaths = namespacePaths;
   }

   @Nullable
   public InputSupplier openRoot(String... segments) {
      PathUtil.validatePath(segments);
      List list = List.of(segments);
      Iterator var3 = this.rootPaths.iterator();

      Path path2;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         Path path = (Path)var3.next();
         path2 = PathUtil.getPath(path, list);
      } while(!Files.exists(path2, new LinkOption[0]) || !DirectoryResourcePack.isValidPath(path2));

      return InputSupplier.create(path2);
   }

   public void forEachNamespacedPath(ResourceType type, Identifier path, Consumer consumer) {
      PathUtil.split(path.getPath()).ifSuccess((segments) -> {
         String string = path.getNamespace();
         Iterator var6 = ((List)this.namespacePaths.get(type)).iterator();

         while(var6.hasNext()) {
            Path pathx = (Path)var6.next();
            Path path2 = pathx.resolve(string);
            consumer.accept(PathUtil.getPath(path2, segments));
         }

      }).ifError((error) -> {
         LOGGER.error("Invalid path {}: {}", path, error.message());
      });
   }

   public void findResources(ResourceType type, String namespace, String prefix, ResourcePack.ResultConsumer consumer) {
      PathUtil.split(prefix).ifSuccess((segments) -> {
         List list = (List)this.namespacePaths.get(type);
         int i = list.size();
         if (i == 1) {
            collectIdentifiers(consumer, namespace, (Path)list.get(0), segments);
         } else if (i > 1) {
            Map map = new HashMap();

            for(int j = 0; j < i - 1; ++j) {
               Objects.requireNonNull(map);
               collectIdentifiers(map::putIfAbsent, namespace, (Path)list.get(j), segments);
            }

            Path path = (Path)list.get(i - 1);
            if (map.isEmpty()) {
               collectIdentifiers(consumer, namespace, path, segments);
            } else {
               Objects.requireNonNull(map);
               collectIdentifiers(map::putIfAbsent, namespace, path, segments);
               map.forEach(consumer);
            }
         }

      }).ifError((error) -> {
         LOGGER.error("Invalid path {}: {}", prefix, error.message());
      });
   }

   private static void collectIdentifiers(ResourcePack.ResultConsumer consumer, String namespace, Path root, List prefixSegments) {
      Path path = root.resolve(namespace);
      DirectoryResourcePack.findResources(namespace, path, prefixSegments, consumer);
   }

   @Nullable
   public InputSupplier open(ResourceType type, Identifier id) {
      return (InputSupplier)PathUtil.split(id.getPath()).mapOrElse((segments) -> {
         String string = id.getNamespace();
         Iterator var5 = ((List)this.namespacePaths.get(type)).iterator();

         Path path2;
         do {
            if (!var5.hasNext()) {
               return null;
            }

            Path path = (Path)var5.next();
            path2 = PathUtil.getPath(path.resolve(string), segments);
         } while(!Files.exists(path2, new LinkOption[0]) || !DirectoryResourcePack.isValidPath(path2));

         return InputSupplier.create(path2);
      }, (error) -> {
         LOGGER.error("Invalid path {}: {}", id, error.message());
         return null;
      });
   }

   public Set getNamespaces(ResourceType type) {
      return this.namespaces;
   }

   @Nullable
   public Object parseMetadata(ResourceMetadataSerializer metadataSerializer) {
      InputSupplier inputSupplier = this.openRoot("pack.mcmeta");
      if (inputSupplier != null) {
         try {
            InputStream inputStream = (InputStream)inputSupplier.get();

            Object var5;
            label54: {
               try {
                  Object object = AbstractFileResourcePack.parseMetadata(metadataSerializer, inputStream);
                  if (object != null) {
                     var5 = object;
                     break label54;
                  }
               } catch (Throwable var7) {
                  if (inputStream != null) {
                     try {
                        inputStream.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if (inputStream != null) {
                  inputStream.close();
               }

               return this.metadata.get(metadataSerializer);
            }

            if (inputStream != null) {
               inputStream.close();
            }

            return var5;
         } catch (IOException var8) {
         }
      }

      return this.metadata.get(metadataSerializer);
   }

   public ResourcePackInfo getInfo() {
      return this.info;
   }

   public void close() {
   }

   public ResourceFactory getFactory() {
      return (id) -> {
         return Optional.ofNullable(this.open(ResourceType.CLIENT_RESOURCES, id)).map((stream) -> {
            return new Resource(this, stream);
         });
      };
   }
}
