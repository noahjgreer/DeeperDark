package net.minecraft.structure.pool.alias;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;

public class StructurePoolAliasBindings {
   public static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"random", RandomStructurePoolAliasBinding.CODEC);
      Registry.register(registry, (String)"random_group", RandomGroupStructurePoolAliasBinding.CODEC);
      return (MapCodec)Registry.register(registry, (String)"direct", DirectStructurePoolAliasBinding.CODEC);
   }

   public static void registerPools(Registerable pools, RegistryEntry base, List aliases) {
      aliases.stream().flatMap(StructurePoolAliasBinding::streamTargets).map((target) -> {
         return target.getValue().getPath();
      }).forEach((path) -> {
         StructurePools.register(pools, path, new StructurePool(base, List.of(Pair.of(StructurePoolElement.ofSingle(path), 1)), StructurePool.Projection.RIGID));
      });
   }
}
