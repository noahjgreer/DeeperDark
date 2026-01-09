package net.minecraft.world.gen.chunk.placement;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;

public class ConcentricRingsStructurePlacement extends StructurePlacement {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return buildConcentricRingsCodec(instance).apply(instance, ConcentricRingsStructurePlacement::new);
   });
   private final int distance;
   private final int spread;
   private final int count;
   private final RegistryEntryList preferredBiomes;

   private static Products.P9 buildConcentricRingsCodec(RecordCodecBuilder.Instance instance) {
      Products.P5 p5 = buildCodec(instance);
      Products.P4 p4 = instance.group(Codec.intRange(0, 1023).fieldOf("distance").forGetter(ConcentricRingsStructurePlacement::getDistance), Codec.intRange(0, 1023).fieldOf("spread").forGetter(ConcentricRingsStructurePlacement::getSpread), Codec.intRange(1, 4095).fieldOf("count").forGetter(ConcentricRingsStructurePlacement::getCount), RegistryCodecs.entryList(RegistryKeys.BIOME).fieldOf("preferred_biomes").forGetter(ConcentricRingsStructurePlacement::getPreferredBiomes));
      return new Products.P9(p5.t1(), p5.t2(), p5.t3(), p5.t4(), p5.t5(), p4.t1(), p4.t2(), p4.t3(), p4.t4());
   }

   public ConcentricRingsStructurePlacement(Vec3i locateOffset, StructurePlacement.FrequencyReductionMethod generationPredicateType, float frequency, int salt, Optional exclusionZone, int distance, int spread, int structureCount, RegistryEntryList preferredBiomes) {
      super(locateOffset, generationPredicateType, frequency, salt, exclusionZone);
      this.distance = distance;
      this.spread = spread;
      this.count = structureCount;
      this.preferredBiomes = preferredBiomes;
   }

   public ConcentricRingsStructurePlacement(int distance, int spread, int structureCount, RegistryEntryList preferredBiomes) {
      this(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F, 0, Optional.empty(), distance, spread, structureCount, preferredBiomes);
   }

   public int getDistance() {
      return this.distance;
   }

   public int getSpread() {
      return this.spread;
   }

   public int getCount() {
      return this.count;
   }

   public RegistryEntryList getPreferredBiomes() {
      return this.preferredBiomes;
   }

   protected boolean isStartChunk(StructurePlacementCalculator calculator, int chunkX, int chunkZ) {
      List list = calculator.getPlacementPositions(this);
      return list == null ? false : list.contains(new ChunkPos(chunkX, chunkZ));
   }

   public StructurePlacementType getType() {
      return StructurePlacementType.CONCENTRIC_RINGS;
   }
}
