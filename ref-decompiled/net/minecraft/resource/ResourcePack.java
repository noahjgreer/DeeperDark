package net.minecraft.resource;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface ResourcePack extends AutoCloseable {
   String METADATA_PATH_SUFFIX = ".mcmeta";
   String PACK_METADATA_NAME = "pack.mcmeta";

   @Nullable
   InputSupplier openRoot(String... segments);

   @Nullable
   InputSupplier open(ResourceType type, Identifier id);

   void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer);

   Set getNamespaces(ResourceType type);

   @Nullable
   Object parseMetadata(ResourceMetadataSerializer metadataSerializer) throws IOException;

   ResourcePackInfo getInfo();

   default String getId() {
      return this.getInfo().id();
   }

   default Optional getKnownPackInfo() {
      return this.getInfo().knownPackInfo();
   }

   void close();

   @FunctionalInterface
   public interface ResultConsumer extends BiConsumer {
   }
}
