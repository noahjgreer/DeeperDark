package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public record ChunkGenerationSteps(ImmutableList steps) {
   public static final ChunkGenerationSteps GENERATION;
   public static final ChunkGenerationSteps LOADING;

   public ChunkGenerationSteps(ImmutableList immutableList) {
      this.steps = immutableList;
   }

   public ChunkGenerationStep get(ChunkStatus status) {
      return (ChunkGenerationStep)this.steps.get(status.getIndex());
   }

   public ImmutableList steps() {
      return this.steps;
   }

   static {
      GENERATION = (new Builder()).then(ChunkStatus.EMPTY, (builder) -> {
         return builder;
      }).then(ChunkStatus.STRUCTURE_STARTS, (builder) -> {
         return builder.task(ChunkGenerating::generateStructures);
      }).then(ChunkStatus.STRUCTURE_REFERENCES, (builder) -> {
         return builder.dependsOn(ChunkStatus.STRUCTURE_STARTS, 8).task(ChunkGenerating::generateStructureReferences);
      }).then(ChunkStatus.BIOMES, (builder) -> {
         return builder.dependsOn(ChunkStatus.STRUCTURE_STARTS, 8).task(ChunkGenerating::populateBiomes);
      }).then(ChunkStatus.NOISE, (builder) -> {
         return builder.dependsOn(ChunkStatus.STRUCTURE_STARTS, 8).dependsOn(ChunkStatus.BIOMES, 1).blockStateWriteRadius(0).task(ChunkGenerating::populateNoise);
      }).then(ChunkStatus.SURFACE, (builder) -> {
         return builder.dependsOn(ChunkStatus.STRUCTURE_STARTS, 8).dependsOn(ChunkStatus.BIOMES, 1).blockStateWriteRadius(0).task(ChunkGenerating::buildSurface);
      }).then(ChunkStatus.CARVERS, (builder) -> {
         return builder.dependsOn(ChunkStatus.STRUCTURE_STARTS, 8).blockStateWriteRadius(0).task(ChunkGenerating::carve);
      }).then(ChunkStatus.FEATURES, (builder) -> {
         return builder.dependsOn(ChunkStatus.STRUCTURE_STARTS, 8).dependsOn(ChunkStatus.CARVERS, 1).blockStateWriteRadius(1).task(ChunkGenerating::generateFeatures);
      }).then(ChunkStatus.INITIALIZE_LIGHT, (builder) -> {
         return builder.task(ChunkGenerating::initializeLight);
      }).then(ChunkStatus.LIGHT, (builder) -> {
         return builder.dependsOn(ChunkStatus.INITIALIZE_LIGHT, 1).task(ChunkGenerating::light);
      }).then(ChunkStatus.SPAWN, (builder) -> {
         return builder.dependsOn(ChunkStatus.BIOMES, 1).task(ChunkGenerating::generateEntities);
      }).then(ChunkStatus.FULL, (builder) -> {
         return builder.task(ChunkGenerating::convertToFullChunk);
      }).build();
      LOADING = (new Builder()).then(ChunkStatus.EMPTY, (builder) -> {
         return builder;
      }).then(ChunkStatus.STRUCTURE_STARTS, (builder) -> {
         return builder.task(ChunkGenerating::loadStructures);
      }).then(ChunkStatus.STRUCTURE_REFERENCES, (builder) -> {
         return builder;
      }).then(ChunkStatus.BIOMES, (builder) -> {
         return builder;
      }).then(ChunkStatus.NOISE, (builder) -> {
         return builder;
      }).then(ChunkStatus.SURFACE, (builder) -> {
         return builder;
      }).then(ChunkStatus.CARVERS, (builder) -> {
         return builder;
      }).then(ChunkStatus.FEATURES, (builder) -> {
         return builder;
      }).then(ChunkStatus.INITIALIZE_LIGHT, (builder) -> {
         return builder.task(ChunkGenerating::initializeLight);
      }).then(ChunkStatus.LIGHT, (builder) -> {
         return builder.dependsOn(ChunkStatus.INITIALIZE_LIGHT, 1).task(ChunkGenerating::light);
      }).then(ChunkStatus.SPAWN, (builder) -> {
         return builder;
      }).then(ChunkStatus.FULL, (builder) -> {
         return builder.task(ChunkGenerating::convertToFullChunk);
      }).build();
   }

   public static class Builder {
      private final List steps = new ArrayList();

      public ChunkGenerationSteps build() {
         return new ChunkGenerationSteps(ImmutableList.copyOf(this.steps));
      }

      public Builder then(ChunkStatus status, UnaryOperator stepFactory) {
         ChunkGenerationStep.Builder builder;
         if (this.steps.isEmpty()) {
            builder = new ChunkGenerationStep.Builder(status);
         } else {
            builder = new ChunkGenerationStep.Builder(status, (ChunkGenerationStep)this.steps.getLast());
         }

         this.steps.add(((ChunkGenerationStep.Builder)stepFactory.apply(builder)).build());
         return this;
      }
   }
}
