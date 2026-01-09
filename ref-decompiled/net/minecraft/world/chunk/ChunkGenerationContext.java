package net.minecraft.world.chunk;

import java.util.concurrent.Executor;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public record ChunkGenerationContext(ServerWorld world, ChunkGenerator generator, StructureTemplateManager structureManager, ServerLightingProvider lightingProvider, Executor mainThreadExecutor, WorldChunk.UnsavedListener unsavedListener) {
   public ChunkGenerationContext(ServerWorld serverWorld, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, ServerLightingProvider serverLightingProvider, Executor executor, WorldChunk.UnsavedListener unsavedListener) {
      this.world = serverWorld;
      this.generator = chunkGenerator;
      this.structureManager = structureTemplateManager;
      this.lightingProvider = serverLightingProvider;
      this.mainThreadExecutor = executor;
      this.unsavedListener = unsavedListener;
   }

   public ServerWorld world() {
      return this.world;
   }

   public ChunkGenerator generator() {
      return this.generator;
   }

   public StructureTemplateManager structureManager() {
      return this.structureManager;
   }

   public ServerLightingProvider lightingProvider() {
      return this.lightingProvider;
   }

   public Executor mainThreadExecutor() {
      return this.mainThreadExecutor;
   }

   public WorldChunk.UnsavedListener unsavedListener() {
      return this.unsavedListener;
   }
}
