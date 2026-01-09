package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;

public class FossilFeatureConfig implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Identifier.CODEC.listOf().fieldOf("fossil_structures").forGetter((config) -> {
         return config.fossilStructures;
      }), Identifier.CODEC.listOf().fieldOf("overlay_structures").forGetter((config) -> {
         return config.overlayStructures;
      }), StructureProcessorType.REGISTRY_CODEC.fieldOf("fossil_processors").forGetter((config) -> {
         return config.fossilProcessors;
      }), StructureProcessorType.REGISTRY_CODEC.fieldOf("overlay_processors").forGetter((config) -> {
         return config.overlayProcessors;
      }), Codec.intRange(0, 7).fieldOf("max_empty_corners_allowed").forGetter((config) -> {
         return config.maxEmptyCorners;
      })).apply(instance, FossilFeatureConfig::new);
   });
   public final List fossilStructures;
   public final List overlayStructures;
   public final RegistryEntry fossilProcessors;
   public final RegistryEntry overlayProcessors;
   public final int maxEmptyCorners;

   public FossilFeatureConfig(List fossilStructures, List overlayStructures, RegistryEntry fossilProcessors, RegistryEntry overlayProcessors, int maxEmptyCorners) {
      if (fossilStructures.isEmpty()) {
         throw new IllegalArgumentException("Fossil structure lists need at least one entry");
      } else if (fossilStructures.size() != overlayStructures.size()) {
         throw new IllegalArgumentException("Fossil structure lists must be equal lengths");
      } else {
         this.fossilStructures = fossilStructures;
         this.overlayStructures = overlayStructures;
         this.fossilProcessors = fossilProcessors;
         this.overlayProcessors = overlayProcessors;
         this.maxEmptyCorners = maxEmptyCorners;
      }
   }
}
