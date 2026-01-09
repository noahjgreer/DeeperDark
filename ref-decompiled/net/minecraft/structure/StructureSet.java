package net.minecraft.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.structure.Structure;

public record StructureSet(List structures, StructurePlacement placement) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(StructureSet.WeightedEntry.CODEC.listOf().fieldOf("structures").forGetter(StructureSet::structures), StructurePlacement.TYPE_CODEC.fieldOf("placement").forGetter(StructureSet::placement)).apply(instance, StructureSet::new);
   });
   public static final Codec REGISTRY_CODEC;

   public StructureSet(RegistryEntry structure, StructurePlacement placement) {
      this(List.of(new WeightedEntry(structure, 1)), placement);
   }

   public StructureSet(List list, StructurePlacement structurePlacement) {
      this.structures = list;
      this.placement = structurePlacement;
   }

   public static WeightedEntry createEntry(RegistryEntry structure, int weight) {
      return new WeightedEntry(structure, weight);
   }

   public static WeightedEntry createEntry(RegistryEntry structure) {
      return new WeightedEntry(structure, 1);
   }

   public List structures() {
      return this.structures;
   }

   public StructurePlacement placement() {
      return this.placement;
   }

   static {
      REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.STRUCTURE_SET, CODEC);
   }

   public static record WeightedEntry(RegistryEntry structure, int weight) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Structure.ENTRY_CODEC.fieldOf("structure").forGetter(WeightedEntry::structure), Codecs.POSITIVE_INT.fieldOf("weight").forGetter(WeightedEntry::weight)).apply(instance, WeightedEntry::new);
      });

      public WeightedEntry(RegistryEntry registryEntry, int i) {
         this.structure = registryEntry;
         this.weight = i;
      }

      public RegistryEntry structure() {
         return this.structure;
      }

      public int weight() {
         return this.weight;
      }
   }
}
