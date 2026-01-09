package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class OceanRuinStructure extends Structure {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(configCodecBuilder(instance), OceanRuinStructure.BiomeTemperature.CODEC.fieldOf("biome_temp").forGetter((structure) -> {
         return structure.biomeTemperature;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("large_probability").forGetter((structure) -> {
         return structure.largeProbability;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("cluster_probability").forGetter((structure) -> {
         return structure.clusterProbability;
      })).apply(instance, OceanRuinStructure::new);
   });
   public final BiomeTemperature biomeTemperature;
   public final float largeProbability;
   public final float clusterProbability;

   public OceanRuinStructure(Structure.Config config, BiomeTemperature biomeTemperature, float largeProbability, float clusterProbability) {
      super(config);
      this.biomeTemperature = biomeTemperature;
      this.largeProbability = largeProbability;
      this.clusterProbability = clusterProbability;
   }

   public Optional getStructurePosition(Structure.Context context) {
      return getStructurePosition(context, Heightmap.Type.OCEAN_FLOOR_WG, (collector) -> {
         this.addPieces(collector, context);
      });
   }

   private void addPieces(StructurePiecesCollector collector, Structure.Context context) {
      BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
      BlockRotation blockRotation = BlockRotation.random(context.random());
      OceanRuinGenerator.addPieces(context.structureTemplateManager(), blockPos, blockRotation, collector, context.random(), this);
   }

   public StructureType getType() {
      return StructureType.OCEAN_RUIN;
   }

   public static enum BiomeTemperature implements StringIdentifiable {
      WARM("warm"),
      COLD("cold");

      public static final Codec CODEC = StringIdentifiable.createCodec(BiomeTemperature::values);
      /** @deprecated */
      @Deprecated
      public static final Codec ENUM_NAME_CODEC = Codecs.enumByName(BiomeTemperature::valueOf);
      private final String name;

      private BiomeTemperature(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static BiomeTemperature[] method_36760() {
         return new BiomeTemperature[]{WARM, COLD};
      }
   }
}
