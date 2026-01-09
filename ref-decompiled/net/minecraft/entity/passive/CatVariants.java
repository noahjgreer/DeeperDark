package net.minecraft.entity.passive;

import java.util.List;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.entity.spawn.MoonBrightnessSpawnCondition;
import net.minecraft.entity.spawn.SpawnConditionSelectors;
import net.minecraft.entity.spawn.StructureSpawnCondition;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

public interface CatVariants {
   RegistryKey TABBY = of("tabby");
   RegistryKey BLACK = of("black");
   RegistryKey RED = of("red");
   RegistryKey SIAMESE = of("siamese");
   RegistryKey BRITISH_SHORTHAIR = of("british_shorthair");
   RegistryKey CALICO = of("calico");
   RegistryKey PERSIAN = of("persian");
   RegistryKey RAGDOLL = of("ragdoll");
   RegistryKey WHITE = of("white");
   RegistryKey JELLIE = of("jellie");
   RegistryKey ALL_BLACK = of("all_black");

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.CAT_VARIANT, Identifier.ofVanilla(id));
   }

   static void bootstrap(Registerable registry) {
      RegistryEntryLookup registryEntryLookup = registry.getRegistryLookup(RegistryKeys.STRUCTURE);
      register(registry, TABBY, "entity/cat/tabby");
      register(registry, BLACK, "entity/cat/black");
      register(registry, RED, "entity/cat/red");
      register(registry, SIAMESE, "entity/cat/siamese");
      register(registry, BRITISH_SHORTHAIR, "entity/cat/british_shorthair");
      register(registry, CALICO, "entity/cat/calico");
      register(registry, PERSIAN, "entity/cat/persian");
      register(registry, RAGDOLL, "entity/cat/ragdoll");
      register(registry, WHITE, "entity/cat/white");
      register(registry, JELLIE, "entity/cat/jellie");
      register(registry, ALL_BLACK, "entity/cat/all_black", new SpawnConditionSelectors(List.of(new VariantSelectorProvider.Selector(new StructureSpawnCondition(registryEntryLookup.getOrThrow(StructureTags.CATS_SPAWN_AS_BLACK)), 1), new VariantSelectorProvider.Selector(new MoonBrightnessSpawnCondition(NumberRange.DoubleRange.atLeast(0.9)), 0))));
   }

   private static void register(Registerable registry, RegistryKey key, String assetId) {
      register(registry, key, assetId, SpawnConditionSelectors.createFallback(0));
   }

   private static void register(Registerable registry, RegistryKey key, String assetId, SpawnConditionSelectors spawnConditions) {
      registry.register(key, new CatVariant(new AssetInfo(Identifier.ofVanilla(assetId)), spawnConditions));
   }
}
