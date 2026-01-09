package net.minecraft.world.gen.chunk.placement;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;

public abstract class StructurePlacement {
   public static final Codec TYPE_CODEC;
   private static final int ARBITRARY_SALT = 10387320;
   private final Vec3i locateOffset;
   private final FrequencyReductionMethod frequencyReductionMethod;
   private final float frequency;
   private final int salt;
   private final Optional exclusionZone;

   protected static Products.P5 buildCodec(RecordCodecBuilder.Instance instance) {
      return instance.group(Vec3i.createOffsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(StructurePlacement::getLocateOffset), StructurePlacement.FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", StructurePlacement.FrequencyReductionMethod.DEFAULT).forGetter(StructurePlacement::getFrequencyReductionMethod), Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(StructurePlacement::getFrequency), Codecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(StructurePlacement::getSalt), StructurePlacement.ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(StructurePlacement::getExclusionZone));
   }

   protected StructurePlacement(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional exclusionZone) {
      this.locateOffset = locateOffset;
      this.frequencyReductionMethod = frequencyReductionMethod;
      this.frequency = frequency;
      this.salt = salt;
      this.exclusionZone = exclusionZone;
   }

   protected Vec3i getLocateOffset() {
      return this.locateOffset;
   }

   protected FrequencyReductionMethod getFrequencyReductionMethod() {
      return this.frequencyReductionMethod;
   }

   protected float getFrequency() {
      return this.frequency;
   }

   protected int getSalt() {
      return this.salt;
   }

   protected Optional getExclusionZone() {
      return this.exclusionZone;
   }

   public boolean shouldGenerate(StructurePlacementCalculator calculator, int chunkX, int chunkZ) {
      return this.isStartChunk(calculator, chunkX, chunkZ) && this.applyFrequencyReduction(chunkX, chunkZ, calculator.getStructureSeed()) && this.applyExclusionZone(calculator, chunkX, chunkZ);
   }

   public boolean applyFrequencyReduction(int chunkX, int chunkZ, long seed) {
      return !(this.frequency < 1.0F) || this.frequencyReductionMethod.shouldGenerate(seed, this.salt, chunkX, chunkZ, this.frequency);
   }

   public boolean applyExclusionZone(StructurePlacementCalculator calculator, int centerChunkX, int centerChunkZ) {
      return !this.exclusionZone.isPresent() || !((ExclusionZone)this.exclusionZone.get()).shouldExclude(calculator, centerChunkX, centerChunkZ);
   }

   protected abstract boolean isStartChunk(StructurePlacementCalculator calculator, int chunkX, int chunkZ);

   public BlockPos getLocatePos(ChunkPos chunkPos) {
      return (new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ())).add(this.getLocateOffset());
   }

   public abstract StructurePlacementType getType();

   private static boolean defaultShouldGenerate(long seed, int salt, int chunkX, int chunkZ, float frequency) {
      ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
      chunkRandom.setRegionSeed(seed, salt, chunkX, chunkZ);
      return chunkRandom.nextFloat() < frequency;
   }

   private static boolean legacyType3ShouldGenerate(long seed, int salt, int chunkX, int chunkZ, float frequency) {
      ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
      chunkRandom.setCarverSeed(seed, chunkX, chunkZ);
      return chunkRandom.nextDouble() < (double)frequency;
   }

   private static boolean legacyType2ShouldGenerate(long seed, int salt, int chunkX, int chunkZ, float frequency) {
      ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
      chunkRandom.setRegionSeed(seed, chunkX, chunkZ, 10387320);
      return chunkRandom.nextFloat() < frequency;
   }

   private static boolean legacyType1ShouldGenerate(long seed, int salt, int chunkX, int chunkZ, float frequency) {
      int i = chunkX >> 4;
      int j = chunkZ >> 4;
      ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
      chunkRandom.setSeed((long)(i ^ j << 4) ^ seed);
      chunkRandom.nextInt();
      return chunkRandom.nextInt((int)(1.0F / frequency)) == 0;
   }

   static {
      TYPE_CODEC = Registries.STRUCTURE_PLACEMENT.getCodec().dispatch(StructurePlacement::getType, StructurePlacementType::codec);
   }

   public static enum FrequencyReductionMethod implements StringIdentifiable {
      DEFAULT("default", StructurePlacement::defaultShouldGenerate),
      LEGACY_TYPE_1("legacy_type_1", StructurePlacement::legacyType1ShouldGenerate),
      LEGACY_TYPE_2("legacy_type_2", StructurePlacement::legacyType2ShouldGenerate),
      LEGACY_TYPE_3("legacy_type_3", StructurePlacement::legacyType3ShouldGenerate);

      public static final Codec CODEC = StringIdentifiable.createCodec(FrequencyReductionMethod::values);
      private final String name;
      private final GenerationPredicate generationPredicate;

      private FrequencyReductionMethod(final String name, final GenerationPredicate generationPredicate) {
         this.name = name;
         this.generationPredicate = generationPredicate;
      }

      public boolean shouldGenerate(long seed, int salt, int chunkX, int chunkZ, float chance) {
         return this.generationPredicate.shouldGenerate(seed, salt, chunkX, chunkZ, chance);
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static FrequencyReductionMethod[] method_41649() {
         return new FrequencyReductionMethod[]{DEFAULT, LEGACY_TYPE_1, LEGACY_TYPE_2, LEGACY_TYPE_3};
      }
   }

   /** @deprecated */
   @Deprecated
   public static record ExclusionZone(RegistryEntry otherSet, int chunkCount) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(RegistryElementCodec.of(RegistryKeys.STRUCTURE_SET, StructureSet.CODEC, false).fieldOf("other_set").forGetter(ExclusionZone::otherSet), Codec.intRange(1, 16).fieldOf("chunk_count").forGetter(ExclusionZone::chunkCount)).apply(instance, ExclusionZone::new);
      });

      public ExclusionZone(RegistryEntry registryEntry, int i) {
         this.otherSet = registryEntry;
         this.chunkCount = i;
      }

      boolean shouldExclude(StructurePlacementCalculator calculator, int centerChunkX, int centerChunkZ) {
         return calculator.canGenerate(this.otherSet, centerChunkX, centerChunkZ, this.chunkCount);
      }

      public RegistryEntry otherSet() {
         return this.otherSet;
      }

      public int chunkCount() {
         return this.chunkCount;
      }
   }

   @FunctionalInterface
   public interface GenerationPredicate {
      boolean shouldGenerate(long seed, int salt, int chunkX, int chunkZ, float chance);
   }
}
