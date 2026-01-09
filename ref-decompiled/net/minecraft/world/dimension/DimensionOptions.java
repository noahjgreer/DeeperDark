package net.minecraft.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public record DimensionOptions(RegistryEntry dimensionTypeEntry, ChunkGenerator chunkGenerator) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(DimensionType.REGISTRY_CODEC.fieldOf("type").forGetter(DimensionOptions::dimensionTypeEntry), ChunkGenerator.CODEC.fieldOf("generator").forGetter(DimensionOptions::chunkGenerator)).apply(instance, instance.stable(DimensionOptions::new));
   });
   public static final RegistryKey OVERWORLD;
   public static final RegistryKey NETHER;
   public static final RegistryKey END;

   public DimensionOptions(RegistryEntry dimensionTypeEntry, ChunkGenerator chunkGenerator) {
      this.dimensionTypeEntry = dimensionTypeEntry;
      this.chunkGenerator = chunkGenerator;
   }

   public RegistryEntry dimensionTypeEntry() {
      return this.dimensionTypeEntry;
   }

   public ChunkGenerator chunkGenerator() {
      return this.chunkGenerator;
   }

   static {
      OVERWORLD = RegistryKey.of(RegistryKeys.DIMENSION, Identifier.ofVanilla("overworld"));
      NETHER = RegistryKey.of(RegistryKeys.DIMENSION, Identifier.ofVanilla("the_nether"));
      END = RegistryKey.of(RegistryKeys.DIMENSION, Identifier.ofVanilla("the_end"));
   }
}
