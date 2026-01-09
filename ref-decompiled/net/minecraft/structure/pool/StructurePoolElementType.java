package net.minecraft.structure.pool;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface StructurePoolElementType {
   StructurePoolElementType SINGLE_POOL_ELEMENT = register("single_pool_element", SinglePoolElement.CODEC);
   StructurePoolElementType LIST_POOL_ELEMENT = register("list_pool_element", ListPoolElement.CODEC);
   StructurePoolElementType FEATURE_POOL_ELEMENT = register("feature_pool_element", FeaturePoolElement.CODEC);
   StructurePoolElementType EMPTY_POOL_ELEMENT = register("empty_pool_element", EmptyPoolElement.CODEC);
   StructurePoolElementType LEGACY_SINGLE_POOL_ELEMENT = register("legacy_single_pool_element", LegacySinglePoolElement.CODEC);

   MapCodec codec();

   static StructurePoolElementType register(String id, MapCodec codec) {
      return (StructurePoolElementType)Registry.register(Registries.STRUCTURE_POOL_ELEMENT, (String)id, () -> {
         return codec;
      });
   }
}
