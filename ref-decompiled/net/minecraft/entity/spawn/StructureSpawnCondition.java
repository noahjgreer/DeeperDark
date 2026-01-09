package net.minecraft.entity.spawn;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;

public record StructureSpawnCondition(RegistryEntryList requiredStructures) implements SpawnCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.STRUCTURE).fieldOf("structures").forGetter(StructureSpawnCondition::requiredStructures)).apply(instance, StructureSpawnCondition::new);
   });

   public StructureSpawnCondition(RegistryEntryList registryEntryList) {
      this.requiredStructures = registryEntryList;
   }

   public boolean test(SpawnContext spawnContext) {
      return spawnContext.world().toServerWorld().getStructureAccessor().getStructureContaining(spawnContext.pos(), this.requiredStructures).hasChildren();
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public RegistryEntryList requiredStructures() {
      return this.requiredStructures;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((SpawnContext)context);
   }
}
