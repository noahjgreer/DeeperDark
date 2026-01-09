package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import org.apache.commons.lang3.mutable.MutableBoolean;

public record PlacedFeature(RegistryEntry feature, List placementModifiers) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature").forGetter((placedFeature) -> {
         return placedFeature.feature;
      }), PlacementModifier.CODEC.listOf().fieldOf("placement").forGetter((placedFeature) -> {
         return placedFeature.placementModifiers;
      })).apply(instance, PlacedFeature::new);
   });
   public static final Codec REGISTRY_CODEC;
   public static final Codec LIST_CODEC;
   public static final Codec LISTS_CODEC;

   public PlacedFeature(RegistryEntry registryEntry, List placementModifiers) {
      this.feature = registryEntry;
      this.placementModifiers = placementModifiers;
   }

   public boolean generateUnregistered(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos) {
      return this.generate(new FeaturePlacementContext(world, generator, Optional.empty()), random, pos);
   }

   public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos) {
      return this.generate(new FeaturePlacementContext(world, generator, Optional.of(this)), random, pos);
   }

   private boolean generate(FeaturePlacementContext context, Random random, BlockPos pos) {
      Stream stream = Stream.of(pos);

      PlacementModifier placementModifier;
      for(Iterator var5 = this.placementModifiers.iterator(); var5.hasNext(); stream = stream.flatMap((posx) -> {
         return placementModifier.getPositions(context, random, posx);
      })) {
         placementModifier = (PlacementModifier)var5.next();
      }

      ConfiguredFeature configuredFeature = (ConfiguredFeature)this.feature.value();
      MutableBoolean mutableBoolean = new MutableBoolean();
      stream.forEach((placedPos) -> {
         if (configuredFeature.generate(context.getWorld(), context.getChunkGenerator(), random, placedPos)) {
            mutableBoolean.setTrue();
         }

      });
      return mutableBoolean.isTrue();
   }

   public Stream getDecoratedFeatures() {
      return ((ConfiguredFeature)this.feature.value()).getDecoratedFeatures();
   }

   public String toString() {
      return "Placed " + String.valueOf(this.feature);
   }

   public RegistryEntry feature() {
      return this.feature;
   }

   public List placementModifiers() {
      return this.placementModifiers;
   }

   static {
      REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.PLACED_FEATURE, CODEC);
      LIST_CODEC = RegistryCodecs.entryList(RegistryKeys.PLACED_FEATURE, CODEC);
      LISTS_CODEC = RegistryCodecs.entryList(RegistryKeys.PLACED_FEATURE, CODEC, true).listOf();
   }
}
