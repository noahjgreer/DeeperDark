package net.minecraft.entity.spawn;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;

public record SpawnContext(BlockPos pos, ServerWorldAccess world, RegistryEntry biome) {
   public SpawnContext(BlockPos blockPos, ServerWorldAccess serverWorldAccess, RegistryEntry registryEntry) {
      this.pos = blockPos;
      this.world = serverWorldAccess;
      this.biome = registryEntry;
   }

   public static SpawnContext of(ServerWorldAccess world, BlockPos pos) {
      RegistryEntry registryEntry = world.getBiome(pos);
      return new SpawnContext(pos, world, registryEntry);
   }

   public BlockPos pos() {
      return this.pos;
   }

   public ServerWorldAccess world() {
      return this.world;
   }

   public RegistryEntry biome() {
      return this.biome;
   }
}
