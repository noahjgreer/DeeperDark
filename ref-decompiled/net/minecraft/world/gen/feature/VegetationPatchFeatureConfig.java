package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class VegetationPatchFeatureConfig implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(TagKey.codec(RegistryKeys.BLOCK).fieldOf("replaceable").forGetter((config) -> {
         return config.replaceable;
      }), BlockStateProvider.TYPE_CODEC.fieldOf("ground_state").forGetter((config) -> {
         return config.groundState;
      }), PlacedFeature.REGISTRY_CODEC.fieldOf("vegetation_feature").forGetter((config) -> {
         return config.vegetationFeature;
      }), VerticalSurfaceType.CODEC.fieldOf("surface").forGetter((config) -> {
         return config.surface;
      }), IntProvider.createValidatingCodec(1, 128).fieldOf("depth").forGetter((config) -> {
         return config.depth;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("extra_bottom_block_chance").forGetter((config) -> {
         return config.extraBottomBlockChance;
      }), Codec.intRange(1, 256).fieldOf("vertical_range").forGetter((config) -> {
         return config.verticalRange;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("vegetation_chance").forGetter((config) -> {
         return config.vegetationChance;
      }), IntProvider.VALUE_CODEC.fieldOf("xz_radius").forGetter((config) -> {
         return config.horizontalRadius;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("extra_edge_column_chance").forGetter((config) -> {
         return config.extraEdgeColumnChance;
      })).apply(instance, VegetationPatchFeatureConfig::new);
   });
   public final TagKey replaceable;
   public final BlockStateProvider groundState;
   public final RegistryEntry vegetationFeature;
   public final VerticalSurfaceType surface;
   public final IntProvider depth;
   public final float extraBottomBlockChance;
   public final int verticalRange;
   public final float vegetationChance;
   public final IntProvider horizontalRadius;
   public final float extraEdgeColumnChance;

   public VegetationPatchFeatureConfig(TagKey replaceable, BlockStateProvider groundState, RegistryEntry vegetationFeature, VerticalSurfaceType surface, IntProvider depth, float extraBottomBlockChance, int verticalRange, float vegetationChance, IntProvider horizontalRadius, float extraEdgeColumnChance) {
      this.replaceable = replaceable;
      this.groundState = groundState;
      this.vegetationFeature = vegetationFeature;
      this.surface = surface;
      this.depth = depth;
      this.extraBottomBlockChance = extraBottomBlockChance;
      this.verticalRange = verticalRange;
      this.vegetationChance = vegetationChance;
      this.horizontalRadius = horizontalRadius;
      this.extraEdgeColumnChance = extraEdgeColumnChance;
   }
}
