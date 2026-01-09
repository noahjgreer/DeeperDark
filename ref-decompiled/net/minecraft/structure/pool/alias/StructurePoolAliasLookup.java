package net.minecraft.structure.pool.alias;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

@FunctionalInterface
public interface StructurePoolAliasLookup {
   StructurePoolAliasLookup EMPTY = (pool) -> {
      return pool;
   };

   RegistryKey lookup(RegistryKey pool);

   static StructurePoolAliasLookup create(List bindings, BlockPos pos, long seed) {
      if (bindings.isEmpty()) {
         return EMPTY;
      } else {
         Random random = Random.create(seed).nextSplitter().split(pos);
         ImmutableMap.Builder builder = ImmutableMap.builder();
         bindings.forEach((binding) -> {
            Objects.requireNonNull(builder);
            binding.forEach(random, builder::put);
         });
         Map map = builder.build();
         return (alias) -> {
            return (RegistryKey)Objects.requireNonNull((RegistryKey)map.getOrDefault(alias, alias), () -> {
               return "alias " + String.valueOf(alias.getValue()) + " was mapped to null value";
            });
         };
      }
   }
}
