package net.minecraft.registry;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.Bootstrap;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockTypes;
import net.minecraft.block.Blocks;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.dialog.DialogActionTypes;
import net.minecraft.dialog.DialogBodyTypes;
import net.minecraft.dialog.DialogTypes;
import net.minecraft.dialog.InputControlTypes;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.enchantment.effect.EnchantmentLocationBasedEffect;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.enchantment.provider.EnchantmentProviderType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.spawn.SpawnConditions;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.loot.provider.score.LootScoreProviderTypes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.component.ComponentPredicateTypes;
import net.minecraft.predicate.entity.EntitySubPredicateTypes;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.display.RecipeDisplayBootstrap;
import net.minecraft.recipe.display.SlotDisplays;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.pool.alias.StructurePoolAliasBindings;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.test.BuiltinTestFunctions;
import net.minecraft.test.TestEnvironmentDefinition;
import net.minecraft.test.TestInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.source.BiomeSources;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSourceType;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.chunk.ChunkGenerators;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import net.minecraft.world.gen.root.RootPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class Registries {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map DEFAULT_ENTRIES = Maps.newLinkedHashMap();
   private static final MutableRegistry ROOT;
   public static final DefaultedRegistry GAME_EVENT;
   public static final Registry SOUND_EVENT;
   public static final DefaultedRegistry FLUID;
   public static final Registry STATUS_EFFECT;
   public static final DefaultedRegistry BLOCK;
   public static final DefaultedRegistry ENTITY_TYPE;
   public static final DefaultedRegistry ITEM;
   public static final Registry POTION;
   public static final Registry PARTICLE_TYPE;
   public static final Registry BLOCK_ENTITY_TYPE;
   public static final Registry CUSTOM_STAT;
   public static final DefaultedRegistry CHUNK_STATUS;
   public static final Registry RULE_TEST;
   public static final Registry RULE_BLOCK_ENTITY_MODIFIER;
   public static final Registry POS_RULE_TEST;
   public static final Registry SCREEN_HANDLER;
   public static final Registry RECIPE_TYPE;
   public static final Registry RECIPE_SERIALIZER;
   public static final Registry ATTRIBUTE;
   public static final Registry POSITION_SOURCE_TYPE;
   public static final Registry COMMAND_ARGUMENT_TYPE;
   public static final Registry STAT_TYPE;
   public static final DefaultedRegistry VILLAGER_TYPE;
   public static final DefaultedRegistry VILLAGER_PROFESSION;
   public static final Registry POINT_OF_INTEREST_TYPE;
   public static final DefaultedRegistry MEMORY_MODULE_TYPE;
   public static final DefaultedRegistry SENSOR_TYPE;
   public static final Registry SCHEDULE;
   public static final Registry ACTIVITY;
   public static final Registry LOOT_POOL_ENTRY_TYPE;
   public static final Registry LOOT_FUNCTION_TYPE;
   public static final Registry LOOT_CONDITION_TYPE;
   public static final Registry LOOT_NUMBER_PROVIDER_TYPE;
   public static final Registry LOOT_NBT_PROVIDER_TYPE;
   public static final Registry LOOT_SCORE_PROVIDER_TYPE;
   public static final Registry FLOAT_PROVIDER_TYPE;
   public static final Registry INT_PROVIDER_TYPE;
   public static final Registry HEIGHT_PROVIDER_TYPE;
   public static final Registry BLOCK_PREDICATE_TYPE;
   public static final Registry CARVER;
   public static final Registry FEATURE;
   public static final Registry STRUCTURE_PLACEMENT;
   public static final Registry STRUCTURE_PIECE;
   public static final Registry STRUCTURE_TYPE;
   public static final Registry PLACEMENT_MODIFIER_TYPE;
   public static final Registry BLOCK_STATE_PROVIDER_TYPE;
   public static final Registry FOLIAGE_PLACER_TYPE;
   public static final Registry TRUNK_PLACER_TYPE;
   public static final Registry ROOT_PLACER_TYPE;
   public static final Registry TREE_DECORATOR_TYPE;
   public static final Registry FEATURE_SIZE_TYPE;
   public static final Registry BIOME_SOURCE;
   public static final Registry CHUNK_GENERATOR;
   public static final Registry MATERIAL_CONDITION;
   public static final Registry MATERIAL_RULE;
   public static final Registry DENSITY_FUNCTION_TYPE;
   public static final Registry BLOCK_TYPE;
   public static final Registry STRUCTURE_PROCESSOR;
   public static final Registry STRUCTURE_POOL_ELEMENT;
   public static final Registry POOL_ALIAS_BINDING;
   public static final Registry DECORATED_POT_PATTERN;
   public static final Registry ITEM_GROUP;
   public static final Registry CRITERION;
   public static final Registry NUMBER_FORMAT_TYPE;
   public static final Registry DATA_COMPONENT_TYPE;
   public static final Registry ENTITY_SUB_PREDICATE_TYPE;
   public static final Registry DATA_COMPONENT_PREDICATE_TYPE;
   public static final Registry MAP_DECORATION_TYPE;
   public static final Registry ENCHANTMENT_EFFECT_COMPONENT_TYPE;
   public static final Registry ENCHANTMENT_LEVEL_BASED_VALUE_TYPE;
   public static final Registry ENCHANTMENT_ENTITY_EFFECT_TYPE;
   public static final Registry ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE;
   public static final Registry ENCHANTMENT_VALUE_EFFECT_TYPE;
   public static final Registry ENCHANTMENT_PROVIDER_TYPE;
   public static final Registry CONSUME_EFFECT_TYPE;
   public static final Registry RECIPE_DISPLAY;
   public static final Registry SLOT_DISPLAY;
   public static final Registry RECIPE_BOOK_CATEGORY;
   public static final Registry TICKET_TYPE;
   public static final Registry TEST_ENVIRONMENT_DEFINITION_TYPE;
   public static final Registry TEST_INSTANCE_TYPE;
   public static final Registry SPAWN_CONDITION_TYPE;
   public static final Registry DIALOG_TYPE;
   public static final Registry DIALOG_ACTION_TYPE;
   public static final Registry INPUT_CONTROL_TYPE;
   public static final Registry DIALOG_BODY_TYPE;
   public static final Registry TEST_FUNCTION;
   public static final Registry REGISTRIES;

   private static Registry create(RegistryKey key, Initializer initializer) {
      return create(key, (MutableRegistry)(new SimpleRegistry(key, Lifecycle.stable(), false)), initializer);
   }

   private static Registry createIntrusive(RegistryKey key, Initializer initializer) {
      return create(key, (MutableRegistry)(new SimpleRegistry(key, Lifecycle.stable(), true)), initializer);
   }

   private static DefaultedRegistry create(RegistryKey key, String defaultId, Initializer initializer) {
      return (DefaultedRegistry)create(key, (MutableRegistry)(new SimpleDefaultedRegistry(defaultId, key, Lifecycle.stable(), false)), initializer);
   }

   private static DefaultedRegistry createIntrusive(RegistryKey key, String defaultId, Initializer initializer) {
      return (DefaultedRegistry)create(key, (MutableRegistry)(new SimpleDefaultedRegistry(defaultId, key, Lifecycle.stable(), true)), initializer);
   }

   private static MutableRegistry create(RegistryKey key, MutableRegistry registry, Initializer initializer) {
      Bootstrap.ensureBootstrapped(() -> {
         return "registry " + String.valueOf(key.getValue());
      });
      Identifier identifier = key.getValue();
      DEFAULT_ENTRIES.put(identifier, () -> {
         return initializer.run(registry);
      });
      ROOT.add(key, registry, RegistryEntryInfo.DEFAULT);
      return registry;
   }

   public static void bootstrap() {
      init();
      freezeRegistries();
      validate(REGISTRIES);
   }

   private static void init() {
      DEFAULT_ENTRIES.forEach((id, initializer) -> {
         if (initializer.get() == null) {
            LOGGER.error("Unable to bootstrap registry '{}'", id);
         }

      });
   }

   private static void freezeRegistries() {
      REGISTRIES.freeze();
      Iterator var0 = REGISTRIES.iterator();

      while(var0.hasNext()) {
         Registry registry = (Registry)var0.next();
         resetTagEntries(registry);
         registry.freeze();
      }

   }

   private static void validate(Registry registries) {
      registries.forEach((registry) -> {
         if (registry.getIds().isEmpty()) {
            Identifier var10000 = registries.getId(registry);
            Util.logErrorOrPause("Registry '" + String.valueOf(var10000) + "' was empty after loading");
         }

         if (registry instanceof DefaultedRegistry) {
            Identifier identifier = ((DefaultedRegistry)registry).getDefaultId();
            Validate.notNull(registry.get(identifier), "Missing default of DefaultedMappedRegistry: " + String.valueOf(identifier), new Object[0]);
         }

      });
   }

   public static RegistryEntryLookup createEntryLookup(Registry registry) {
      return ((MutableRegistry)registry).createMutableRegistryLookup();
   }

   private static void resetTagEntries(Registry registry) {
      ((SimpleRegistry)registry).resetTagEntries();
   }

   static {
      ROOT = new SimpleRegistry(RegistryKey.ofRegistry(RegistryKeys.ROOT), Lifecycle.stable());
      GAME_EVENT = create(RegistryKeys.GAME_EVENT, "step", GameEvent::registerAndGetDefault);
      SOUND_EVENT = create(RegistryKeys.SOUND_EVENT, (registry) -> {
         return SoundEvents.ENTITY_ITEM_PICKUP;
      });
      FLUID = createIntrusive(RegistryKeys.FLUID, "empty", (registry) -> {
         return Fluids.EMPTY;
      });
      STATUS_EFFECT = create(RegistryKeys.STATUS_EFFECT, StatusEffects::registerAndGetDefault);
      BLOCK = createIntrusive(RegistryKeys.BLOCK, "air", (registry) -> {
         return Blocks.AIR;
      });
      ENTITY_TYPE = createIntrusive(RegistryKeys.ENTITY_TYPE, "pig", (registry) -> {
         return EntityType.PIG;
      });
      ITEM = createIntrusive(RegistryKeys.ITEM, "air", (registry) -> {
         return Items.AIR;
      });
      POTION = create(RegistryKeys.POTION, Potions::registerAndGetDefault);
      PARTICLE_TYPE = create(RegistryKeys.PARTICLE_TYPE, (registry) -> {
         return ParticleTypes.BLOCK;
      });
      BLOCK_ENTITY_TYPE = createIntrusive(RegistryKeys.BLOCK_ENTITY_TYPE, (registry) -> {
         return BlockEntityType.FURNACE;
      });
      CUSTOM_STAT = create(RegistryKeys.CUSTOM_STAT, (registry) -> {
         return Stats.JUMP;
      });
      CHUNK_STATUS = create(RegistryKeys.CHUNK_STATUS, "empty", (registry) -> {
         return ChunkStatus.EMPTY;
      });
      RULE_TEST = create(RegistryKeys.RULE_TEST, (registry) -> {
         return RuleTestType.ALWAYS_TRUE;
      });
      RULE_BLOCK_ENTITY_MODIFIER = create(RegistryKeys.RULE_BLOCK_ENTITY_MODIFIER, (registry) -> {
         return RuleBlockEntityModifierType.PASSTHROUGH;
      });
      POS_RULE_TEST = create(RegistryKeys.POS_RULE_TEST, (registry) -> {
         return PosRuleTestType.ALWAYS_TRUE;
      });
      SCREEN_HANDLER = create(RegistryKeys.SCREEN_HANDLER, (registry) -> {
         return ScreenHandlerType.ANVIL;
      });
      RECIPE_TYPE = create(RegistryKeys.RECIPE_TYPE, (registry) -> {
         return RecipeType.CRAFTING;
      });
      RECIPE_SERIALIZER = create(RegistryKeys.RECIPE_SERIALIZER, (registry) -> {
         return RecipeSerializer.SHAPELESS;
      });
      ATTRIBUTE = create(RegistryKeys.ATTRIBUTE, EntityAttributes::registerAndGetDefault);
      POSITION_SOURCE_TYPE = create(RegistryKeys.POSITION_SOURCE_TYPE, (registry) -> {
         return PositionSourceType.BLOCK;
      });
      COMMAND_ARGUMENT_TYPE = create(RegistryKeys.COMMAND_ARGUMENT_TYPE, ArgumentTypes::register);
      STAT_TYPE = create(RegistryKeys.STAT_TYPE, (registry) -> {
         return Stats.USED;
      });
      VILLAGER_TYPE = create(RegistryKeys.VILLAGER_TYPE, "plains", VillagerType::registerAndGetDefault);
      VILLAGER_PROFESSION = create(RegistryKeys.VILLAGER_PROFESSION, "none", VillagerProfession::registerAndGetDefault);
      POINT_OF_INTEREST_TYPE = create(RegistryKeys.POINT_OF_INTEREST_TYPE, PointOfInterestTypes::registerAndGetDefault);
      MEMORY_MODULE_TYPE = create(RegistryKeys.MEMORY_MODULE_TYPE, "dummy", (registry) -> {
         return MemoryModuleType.DUMMY;
      });
      SENSOR_TYPE = create(RegistryKeys.SENSOR_TYPE, "dummy", (registry) -> {
         return SensorType.DUMMY;
      });
      SCHEDULE = create(RegistryKeys.SCHEDULE, (registry) -> {
         return Schedule.EMPTY;
      });
      ACTIVITY = create(RegistryKeys.ACTIVITY, (registry) -> {
         return Activity.IDLE;
      });
      LOOT_POOL_ENTRY_TYPE = create(RegistryKeys.LOOT_POOL_ENTRY_TYPE, (registry) -> {
         return LootPoolEntryTypes.EMPTY;
      });
      LOOT_FUNCTION_TYPE = create(RegistryKeys.LOOT_FUNCTION_TYPE, (registry) -> {
         return LootFunctionTypes.SET_COUNT;
      });
      LOOT_CONDITION_TYPE = create(RegistryKeys.LOOT_CONDITION_TYPE, (registry) -> {
         return LootConditionTypes.INVERTED;
      });
      LOOT_NUMBER_PROVIDER_TYPE = create(RegistryKeys.LOOT_NUMBER_PROVIDER_TYPE, (registry) -> {
         return LootNumberProviderTypes.CONSTANT;
      });
      LOOT_NBT_PROVIDER_TYPE = create(RegistryKeys.LOOT_NBT_PROVIDER_TYPE, (registry) -> {
         return LootNbtProviderTypes.CONTEXT;
      });
      LOOT_SCORE_PROVIDER_TYPE = create(RegistryKeys.LOOT_SCORE_PROVIDER_TYPE, (registry) -> {
         return LootScoreProviderTypes.CONTEXT;
      });
      FLOAT_PROVIDER_TYPE = create(RegistryKeys.FLOAT_PROVIDER_TYPE, (registry) -> {
         return FloatProviderType.CONSTANT;
      });
      INT_PROVIDER_TYPE = create(RegistryKeys.INT_PROVIDER_TYPE, (registry) -> {
         return IntProviderType.CONSTANT;
      });
      HEIGHT_PROVIDER_TYPE = create(RegistryKeys.HEIGHT_PROVIDER_TYPE, (registry) -> {
         return HeightProviderType.CONSTANT;
      });
      BLOCK_PREDICATE_TYPE = create(RegistryKeys.BLOCK_PREDICATE_TYPE, (registry) -> {
         return BlockPredicateType.NOT;
      });
      CARVER = create(RegistryKeys.CARVER, (registry) -> {
         return Carver.CAVE;
      });
      FEATURE = create(RegistryKeys.FEATURE, (registry) -> {
         return Feature.ORE;
      });
      STRUCTURE_PLACEMENT = create(RegistryKeys.STRUCTURE_PLACEMENT, (registry) -> {
         return StructurePlacementType.RANDOM_SPREAD;
      });
      STRUCTURE_PIECE = create(RegistryKeys.STRUCTURE_PIECE, (registry) -> {
         return StructurePieceType.MINESHAFT_ROOM;
      });
      STRUCTURE_TYPE = create(RegistryKeys.STRUCTURE_TYPE, (registry) -> {
         return StructureType.JIGSAW;
      });
      PLACEMENT_MODIFIER_TYPE = create(RegistryKeys.PLACEMENT_MODIFIER_TYPE, (registry) -> {
         return PlacementModifierType.COUNT;
      });
      BLOCK_STATE_PROVIDER_TYPE = create(RegistryKeys.BLOCK_STATE_PROVIDER_TYPE, (registry) -> {
         return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
      });
      FOLIAGE_PLACER_TYPE = create(RegistryKeys.FOLIAGE_PLACER_TYPE, (registry) -> {
         return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
      });
      TRUNK_PLACER_TYPE = create(RegistryKeys.TRUNK_PLACER_TYPE, (registry) -> {
         return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
      });
      ROOT_PLACER_TYPE = create(RegistryKeys.ROOT_PLACER_TYPE, (registry) -> {
         return RootPlacerType.MANGROVE_ROOT_PLACER;
      });
      TREE_DECORATOR_TYPE = create(RegistryKeys.TREE_DECORATOR_TYPE, (registry) -> {
         return TreeDecoratorType.LEAVE_VINE;
      });
      FEATURE_SIZE_TYPE = create(RegistryKeys.FEATURE_SIZE_TYPE, (registry) -> {
         return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
      });
      BIOME_SOURCE = create(RegistryKeys.BIOME_SOURCE, BiomeSources::registerAndGetDefault);
      CHUNK_GENERATOR = create(RegistryKeys.CHUNK_GENERATOR, ChunkGenerators::registerAndGetDefault);
      MATERIAL_CONDITION = create(RegistryKeys.MATERIAL_CONDITION, MaterialRules.MaterialCondition::registerAndGetDefault);
      MATERIAL_RULE = create(RegistryKeys.MATERIAL_RULE, MaterialRules.MaterialRule::registerAndGetDefault);
      DENSITY_FUNCTION_TYPE = create(RegistryKeys.DENSITY_FUNCTION_TYPE, DensityFunctionTypes::registerAndGetDefault);
      BLOCK_TYPE = create(RegistryKeys.BLOCK_TYPE, BlockTypes::registerAndGetDefault);
      STRUCTURE_PROCESSOR = create(RegistryKeys.STRUCTURE_PROCESSOR, (registry) -> {
         return StructureProcessorType.BLOCK_IGNORE;
      });
      STRUCTURE_POOL_ELEMENT = create(RegistryKeys.STRUCTURE_POOL_ELEMENT, (registry) -> {
         return StructurePoolElementType.EMPTY_POOL_ELEMENT;
      });
      POOL_ALIAS_BINDING = create(RegistryKeys.POOL_ALIAS_BINDING, StructurePoolAliasBindings::registerAndGetDefault);
      DECORATED_POT_PATTERN = create(RegistryKeys.DECORATED_POT_PATTERN, DecoratedPotPatterns::registerAndGetDefault);
      ITEM_GROUP = create(RegistryKeys.ITEM_GROUP, ItemGroups::registerAndGetDefault);
      CRITERION = create(RegistryKeys.CRITERION, Criteria::getDefault);
      NUMBER_FORMAT_TYPE = create(RegistryKeys.NUMBER_FORMAT_TYPE, NumberFormatTypes::registerAndGetDefault);
      DATA_COMPONENT_TYPE = create(RegistryKeys.DATA_COMPONENT_TYPE, DataComponentTypes::getDefault);
      ENTITY_SUB_PREDICATE_TYPE = create(RegistryKeys.ENTITY_SUB_PREDICATE_TYPE, EntitySubPredicateTypes::getDefault);
      DATA_COMPONENT_PREDICATE_TYPE = create(RegistryKeys.DATA_COMPONENT_PREDICATE_TYPE, ComponentPredicateTypes::getDefault);
      MAP_DECORATION_TYPE = create(RegistryKeys.MAP_DECORATION_TYPE, MapDecorationTypes::getDefault);
      ENCHANTMENT_EFFECT_COMPONENT_TYPE = create(RegistryKeys.ENCHANTMENT_EFFECT_COMPONENT_TYPE, EnchantmentEffectComponentTypes::getDefault);
      ENCHANTMENT_LEVEL_BASED_VALUE_TYPE = create(RegistryKeys.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE, EnchantmentLevelBasedValue::registerAndGetDefault);
      ENCHANTMENT_ENTITY_EFFECT_TYPE = create(RegistryKeys.ENCHANTMENT_ENTITY_EFFECT_TYPE, EnchantmentEntityEffect::registerAndGetDefault);
      ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE = create(RegistryKeys.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE, EnchantmentLocationBasedEffect::registerAndGetDefault);
      ENCHANTMENT_VALUE_EFFECT_TYPE = create(RegistryKeys.ENCHANTMENT_VALUE_EFFECT_TYPE, EnchantmentValueEffect::registerAndGetDefault);
      ENCHANTMENT_PROVIDER_TYPE = create(RegistryKeys.ENCHANTMENT_PROVIDER_TYPE, EnchantmentProviderType::registerAndGetDefault);
      CONSUME_EFFECT_TYPE = create(RegistryKeys.CONSUME_EFFECT_TYPE, (registry) -> {
         return ConsumeEffect.Type.APPLY_EFFECTS;
      });
      RECIPE_DISPLAY = create(RegistryKeys.RECIPE_DISPLAY, RecipeDisplayBootstrap::registerAndGetDefault);
      SLOT_DISPLAY = create(RegistryKeys.SLOT_DISPLAY, SlotDisplays::registerAndGetDefault);
      RECIPE_BOOK_CATEGORY = create(RegistryKeys.RECIPE_BOOK_CATEGORY, RecipeBookCategories::registerAndGetDefault);
      TICKET_TYPE = create(RegistryKeys.TICKET_TYPE, (registry) -> {
         return ChunkTicketType.UNKNOWN;
      });
      TEST_ENVIRONMENT_DEFINITION_TYPE = create(RegistryKeys.TEST_ENVIRONMENT_DEFINITION_TYPE, TestEnvironmentDefinition::registerAndGetDefault);
      TEST_INSTANCE_TYPE = create(RegistryKeys.TEST_INSTANCE_TYPE, TestInstance::registerAndGetDefault);
      SPAWN_CONDITION_TYPE = create(RegistryKeys.SPAWN_CONDITION_TYPE, SpawnConditions::registerAndGetDefault);
      DIALOG_TYPE = create(RegistryKeys.DIALOG_TYPE, DialogTypes::registerAndGetDefault);
      DIALOG_ACTION_TYPE = create(RegistryKeys.DIALOG_ACTION_TYPE, DialogActionTypes::registerAndGetDefault);
      INPUT_CONTROL_TYPE = create(RegistryKeys.INPUT_CONTROL_TYPE, InputControlTypes::registerAndGetDefault);
      DIALOG_BODY_TYPE = create(RegistryKeys.DIALOG_BODY_TYPE, DialogBodyTypes::registerAndGetDefault);
      TEST_FUNCTION = create(RegistryKeys.TEST_FUNCTION, BuiltinTestFunctions::registerAndGetDefault);
      REGISTRIES = ROOT;
   }

   @FunctionalInterface
   private interface Initializer {
      Object run(Registry registry);
   }
}
