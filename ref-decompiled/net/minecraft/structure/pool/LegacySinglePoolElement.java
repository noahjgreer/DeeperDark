package net.minecraft.structure.pool;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;

public class LegacySinglePoolElement extends SinglePoolElement {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(locationGetter(), processorsGetter(), projectionGetter(), overrideLiquidSettingsGetter()).apply(instance, LegacySinglePoolElement::new);
   });

   protected LegacySinglePoolElement(Either either, RegistryEntry registryEntry, StructurePool.Projection projection, Optional optional) {
      super(either, registryEntry, projection, optional);
   }

   protected StructurePlacementData createPlacementData(BlockRotation rotation, BlockBox box, StructureLiquidSettings liquidSettings, boolean keepJigsaws) {
      StructurePlacementData structurePlacementData = super.createPlacementData(rotation, box, liquidSettings, keepJigsaws);
      structurePlacementData.removeProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
      structurePlacementData.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
      return structurePlacementData;
   }

   public StructurePoolElementType getType() {
      return StructurePoolElementType.LEGACY_SINGLE_POOL_ELEMENT;
   }

   public String toString() {
      return "LegacySingle[" + String.valueOf(this.location) + "]";
   }
}
