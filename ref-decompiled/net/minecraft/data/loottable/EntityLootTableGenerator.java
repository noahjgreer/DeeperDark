package net.minecraft.data.loottable;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.datagen.v1.loot.FabricEntityLootTableGenerator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.predicate.component.ComponentPredicateTypes;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.SheepPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.DyeColor;

public abstract class EntityLootTableGenerator implements LootTableGenerator, FabricEntityLootTableGenerator {
   protected final RegistryWrapper.WrapperLookup registries;
   private final FeatureSet requiredFeatures;
   private final FeatureSet featureSet;
   private final Map lootTables;

   protected final AnyOfLootCondition.Builder createSmeltLootCondition() {
      RegistryWrapper.Impl impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
      return AnyOfLootCondition.builder(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true))), EntityPropertiesLootCondition.builder(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.create().equipment(EntityEquipmentPredicate.Builder.create().mainhand(ItemPredicate.Builder.create().components(ComponentsPredicate.Builder.create().partial(ComponentPredicateTypes.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(impl.getOrThrow(EnchantmentTags.SMELTS_LOOT), NumberRange.IntRange.ANY)))).build())))));
   }

   protected EntityLootTableGenerator(FeatureSet requiredFeatures, RegistryWrapper.WrapperLookup registries) {
      this(requiredFeatures, requiredFeatures, registries);
   }

   protected EntityLootTableGenerator(FeatureSet requiredFeatures, FeatureSet featureSet, RegistryWrapper.WrapperLookup registries) {
      this.lootTables = Maps.newHashMap();
      this.requiredFeatures = requiredFeatures;
      this.featureSet = featureSet;
      this.registries = registries;
   }

   public static LootPool.Builder createForSheep(Map colorLootTables) {
      AlternativeEntry.Builder builder = AlternativeEntry.builder();

      Map.Entry entry;
      for(Iterator var2 = colorLootTables.entrySet().iterator(); var2.hasNext(); builder = builder.alternatively(LootTableEntry.builder((RegistryKey)entry.getValue()).conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().components(ComponentsPredicate.Builder.create().exact(ComponentMapPredicate.of(DataComponentTypes.SHEEP_COLOR, (DyeColor)entry.getKey())).build()).typeSpecific(SheepPredicate.unsheared()))))) {
         entry = (Map.Entry)var2.next();
      }

      return LootPool.builder().with(builder);
   }

   public abstract void generate();

   public void accept(BiConsumer lootTableBiConsumer) {
      this.generate();
      Set set = new HashSet();
      Registries.ENTITY_TYPE.streamEntries().forEach((entityType) -> {
         EntityType entityType2 = (EntityType)entityType.value();
         if (entityType2.isEnabled(this.requiredFeatures)) {
            Optional optional = entityType2.getLootTableKey();
            Map map;
            if (optional.isPresent()) {
               map = (Map)this.lootTables.remove(entityType2);
               if (entityType2.isEnabled(this.featureSet) && (map == null || !map.containsKey(optional.get()))) {
                  throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", optional.get(), entityType.registryKey().getValue()));
               }

               if (map != null) {
                  map.forEach((tableKey, lootTableBuilder) -> {
                     if (!set.add(tableKey)) {
                        throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", tableKey, entityType.registryKey().getValue()));
                     } else {
                        lootTableBiConsumer.accept(tableKey, lootTableBuilder);
                     }
                  });
               }
            } else {
               map = (Map)this.lootTables.remove(entityType2);
               if (map != null) {
                  throw new IllegalStateException(String.format(Locale.ROOT, "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", map.keySet().stream().map((key) -> {
                     return key.getValue().toString();
                  }).collect(Collectors.joining(",")), entityType.registryKey().getValue()));
               }
            }

         }
      });
      if (!this.lootTables.isEmpty()) {
         throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + String.valueOf(this.lootTables.keySet()));
      }
   }

   protected LootCondition.Builder killedByFrog(RegistryEntryLookup entityTypeLookup) {
      return DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().sourceEntity(EntityPredicate.Builder.create().type(entityTypeLookup, EntityType.FROG)));
   }

   protected LootCondition.Builder killedByFrog(RegistryEntryLookup entityTypeLookup, RegistryEntryLookup frogVariantLookup, RegistryKey frogVariant) {
      return DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().sourceEntity(EntityPredicate.Builder.create().type(entityTypeLookup, EntityType.FROG).components(ComponentsPredicate.Builder.create().exact(ComponentMapPredicate.of(DataComponentTypes.FROG_VARIANT, frogVariantLookup.getOrThrow(frogVariant))).build())));
   }

   public void register(EntityType entityType, LootTable.Builder lootTable) {
      this.register(entityType, (RegistryKey)entityType.getLootTableKey().orElseThrow(() -> {
         return new IllegalStateException("Entity " + String.valueOf(entityType) + " has no loot table");
      }), lootTable);
   }

   public void register(EntityType entityType, RegistryKey tableKey, LootTable.Builder lootTable) {
      ((Map)this.lootTables.computeIfAbsent(entityType, (type) -> {
         return new HashMap();
      })).put(tableKey, lootTable);
   }
}
