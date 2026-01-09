package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class ResourceFilter {
   private static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.list(BlockEntry.CODEC).fieldOf("block").forGetter((filter) -> {
         return filter.blocks;
      })).apply(instance, ResourceFilter::new);
   });
   public static final ResourceMetadataSerializer SERIALIZER;
   private final List blocks;

   public ResourceFilter(List blocks) {
      this.blocks = List.copyOf(blocks);
   }

   public boolean isNamespaceBlocked(String namespace) {
      return this.blocks.stream().anyMatch((block) -> {
         return block.getNamespacePredicate().test(namespace);
      });
   }

   public boolean isPathBlocked(String namespace) {
      return this.blocks.stream().anyMatch((block) -> {
         return block.getPathPredicate().test(namespace);
      });
   }

   static {
      SERIALIZER = new ResourceMetadataSerializer("filter", CODEC);
   }
}
