package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Criteria {
   public static final Codec CODEC;
   public static final ImpossibleCriterion IMPOSSIBLE;
   public static final OnKilledCriterion PLAYER_KILLED_ENTITY;
   public static final OnKilledCriterion ENTITY_KILLED_PLAYER;
   public static final EnterBlockCriterion ENTER_BLOCK;
   public static final InventoryChangedCriterion INVENTORY_CHANGED;
   public static final RecipeUnlockedCriterion RECIPE_UNLOCKED;
   public static final PlayerHurtEntityCriterion PLAYER_HURT_ENTITY;
   public static final EntityHurtPlayerCriterion ENTITY_HURT_PLAYER;
   public static final EnchantedItemCriterion ENCHANTED_ITEM;
   public static final FilledBucketCriterion FILLED_BUCKET;
   public static final BrewedPotionCriterion BREWED_POTION;
   public static final ConstructBeaconCriterion CONSTRUCT_BEACON;
   public static final UsedEnderEyeCriterion USED_ENDER_EYE;
   public static final SummonedEntityCriterion SUMMONED_ENTITY;
   public static final BredAnimalsCriterion BRED_ANIMALS;
   public static final TickCriterion LOCATION;
   public static final TickCriterion SLEPT_IN_BED;
   public static final CuredZombieVillagerCriterion CURED_ZOMBIE_VILLAGER;
   public static final VillagerTradeCriterion VILLAGER_TRADE;
   public static final ItemDurabilityChangedCriterion ITEM_DURABILITY_CHANGED;
   public static final LevitationCriterion LEVITATION;
   public static final ChangedDimensionCriterion CHANGED_DIMENSION;
   public static final TickCriterion TICK;
   public static final TameAnimalCriterion TAME_ANIMAL;
   public static final ItemCriterion PLACED_BLOCK;
   public static final ConsumeItemCriterion CONSUME_ITEM;
   public static final EffectsChangedCriterion EFFECTS_CHANGED;
   public static final UsedTotemCriterion USED_TOTEM;
   public static final TravelCriterion NETHER_TRAVEL;
   public static final FishingRodHookedCriterion FISHING_ROD_HOOKED;
   public static final ChanneledLightningCriterion CHANNELED_LIGHTNING;
   public static final ShotCrossbowCriterion SHOT_CROSSBOW;
   public static final KilledByArrowCriterion KILLED_BY_ARROW;
   public static final TickCriterion HERO_OF_THE_VILLAGE;
   public static final TickCriterion VOLUNTARY_EXILE;
   public static final SlideDownBlockCriterion SLIDE_DOWN_BLOCK;
   public static final BeeNestDestroyedCriterion BEE_NEST_DESTROYED;
   public static final TargetHitCriterion TARGET_HIT;
   public static final ItemCriterion ITEM_USED_ON_BLOCK;
   public static final DefaultBlockUseCriterion DEFAULT_BLOCK_USE;
   public static final AnyBlockUseCriterion ANY_BLOCK_USE;
   public static final PlayerGeneratesContainerLootCriterion PLAYER_GENERATES_CONTAINER_LOOT;
   public static final ThrownItemPickedUpByEntityCriterion THROWN_ITEM_PICKED_UP_BY_ENTITY;
   public static final ThrownItemPickedUpByEntityCriterion THROWN_ITEM_PICKED_UP_BY_PLAYER;
   public static final PlayerInteractedWithEntityCriterion PLAYER_INTERACTED_WITH_ENTITY;
   public static final PlayerInteractedWithEntityCriterion PLAYER_SHEARED_EQUIPMENT;
   public static final StartedRidingCriterion STARTED_RIDING;
   public static final LightningStrikeCriterion LIGHTNING_STRIKE;
   public static final UsingItemCriterion USING_ITEM;
   public static final TravelCriterion FALL_FROM_HEIGHT;
   public static final TravelCriterion RIDE_ENTITY_IN_LAVA;
   public static final OnKilledCriterion KILL_MOB_NEAR_SCULK_CATALYST;
   public static final ItemCriterion ALLAY_DROP_ITEM_ON_BLOCK;
   public static final TickCriterion AVOID_VIBRATION;
   public static final RecipeCraftedCriterion RECIPE_CRAFTED;
   public static final RecipeCraftedCriterion CRAFTER_RECIPE_CRAFTED;
   public static final FallAfterExplosionCriterion FALL_AFTER_EXPLOSION;

   public static Criterion register(String id, Criterion criterion) {
      return (Criterion)Registry.register(Registries.CRITERION, (String)id, criterion);
   }

   public static Criterion getDefault(Registry registry) {
      return IMPOSSIBLE;
   }

   static {
      CODEC = Registries.CRITERION.getCodec();
      IMPOSSIBLE = (ImpossibleCriterion)register("impossible", new ImpossibleCriterion());
      PLAYER_KILLED_ENTITY = (OnKilledCriterion)register("player_killed_entity", new OnKilledCriterion());
      ENTITY_KILLED_PLAYER = (OnKilledCriterion)register("entity_killed_player", new OnKilledCriterion());
      ENTER_BLOCK = (EnterBlockCriterion)register("enter_block", new EnterBlockCriterion());
      INVENTORY_CHANGED = (InventoryChangedCriterion)register("inventory_changed", new InventoryChangedCriterion());
      RECIPE_UNLOCKED = (RecipeUnlockedCriterion)register("recipe_unlocked", new RecipeUnlockedCriterion());
      PLAYER_HURT_ENTITY = (PlayerHurtEntityCriterion)register("player_hurt_entity", new PlayerHurtEntityCriterion());
      ENTITY_HURT_PLAYER = (EntityHurtPlayerCriterion)register("entity_hurt_player", new EntityHurtPlayerCriterion());
      ENCHANTED_ITEM = (EnchantedItemCriterion)register("enchanted_item", new EnchantedItemCriterion());
      FILLED_BUCKET = (FilledBucketCriterion)register("filled_bucket", new FilledBucketCriterion());
      BREWED_POTION = (BrewedPotionCriterion)register("brewed_potion", new BrewedPotionCriterion());
      CONSTRUCT_BEACON = (ConstructBeaconCriterion)register("construct_beacon", new ConstructBeaconCriterion());
      USED_ENDER_EYE = (UsedEnderEyeCriterion)register("used_ender_eye", new UsedEnderEyeCriterion());
      SUMMONED_ENTITY = (SummonedEntityCriterion)register("summoned_entity", new SummonedEntityCriterion());
      BRED_ANIMALS = (BredAnimalsCriterion)register("bred_animals", new BredAnimalsCriterion());
      LOCATION = (TickCriterion)register("location", new TickCriterion());
      SLEPT_IN_BED = (TickCriterion)register("slept_in_bed", new TickCriterion());
      CURED_ZOMBIE_VILLAGER = (CuredZombieVillagerCriterion)register("cured_zombie_villager", new CuredZombieVillagerCriterion());
      VILLAGER_TRADE = (VillagerTradeCriterion)register("villager_trade", new VillagerTradeCriterion());
      ITEM_DURABILITY_CHANGED = (ItemDurabilityChangedCriterion)register("item_durability_changed", new ItemDurabilityChangedCriterion());
      LEVITATION = (LevitationCriterion)register("levitation", new LevitationCriterion());
      CHANGED_DIMENSION = (ChangedDimensionCriterion)register("changed_dimension", new ChangedDimensionCriterion());
      TICK = (TickCriterion)register("tick", new TickCriterion());
      TAME_ANIMAL = (TameAnimalCriterion)register("tame_animal", new TameAnimalCriterion());
      PLACED_BLOCK = (ItemCriterion)register("placed_block", new ItemCriterion());
      CONSUME_ITEM = (ConsumeItemCriterion)register("consume_item", new ConsumeItemCriterion());
      EFFECTS_CHANGED = (EffectsChangedCriterion)register("effects_changed", new EffectsChangedCriterion());
      USED_TOTEM = (UsedTotemCriterion)register("used_totem", new UsedTotemCriterion());
      NETHER_TRAVEL = (TravelCriterion)register("nether_travel", new TravelCriterion());
      FISHING_ROD_HOOKED = (FishingRodHookedCriterion)register("fishing_rod_hooked", new FishingRodHookedCriterion());
      CHANNELED_LIGHTNING = (ChanneledLightningCriterion)register("channeled_lightning", new ChanneledLightningCriterion());
      SHOT_CROSSBOW = (ShotCrossbowCriterion)register("shot_crossbow", new ShotCrossbowCriterion());
      KILLED_BY_ARROW = (KilledByArrowCriterion)register("killed_by_arrow", new KilledByArrowCriterion());
      HERO_OF_THE_VILLAGE = (TickCriterion)register("hero_of_the_village", new TickCriterion());
      VOLUNTARY_EXILE = (TickCriterion)register("voluntary_exile", new TickCriterion());
      SLIDE_DOWN_BLOCK = (SlideDownBlockCriterion)register("slide_down_block", new SlideDownBlockCriterion());
      BEE_NEST_DESTROYED = (BeeNestDestroyedCriterion)register("bee_nest_destroyed", new BeeNestDestroyedCriterion());
      TARGET_HIT = (TargetHitCriterion)register("target_hit", new TargetHitCriterion());
      ITEM_USED_ON_BLOCK = (ItemCriterion)register("item_used_on_block", new ItemCriterion());
      DEFAULT_BLOCK_USE = (DefaultBlockUseCriterion)register("default_block_use", new DefaultBlockUseCriterion());
      ANY_BLOCK_USE = (AnyBlockUseCriterion)register("any_block_use", new AnyBlockUseCriterion());
      PLAYER_GENERATES_CONTAINER_LOOT = (PlayerGeneratesContainerLootCriterion)register("player_generates_container_loot", new PlayerGeneratesContainerLootCriterion());
      THROWN_ITEM_PICKED_UP_BY_ENTITY = (ThrownItemPickedUpByEntityCriterion)register("thrown_item_picked_up_by_entity", new ThrownItemPickedUpByEntityCriterion());
      THROWN_ITEM_PICKED_UP_BY_PLAYER = (ThrownItemPickedUpByEntityCriterion)register("thrown_item_picked_up_by_player", new ThrownItemPickedUpByEntityCriterion());
      PLAYER_INTERACTED_WITH_ENTITY = (PlayerInteractedWithEntityCriterion)register("player_interacted_with_entity", new PlayerInteractedWithEntityCriterion());
      PLAYER_SHEARED_EQUIPMENT = (PlayerInteractedWithEntityCriterion)register("player_sheared_equipment", new PlayerInteractedWithEntityCriterion());
      STARTED_RIDING = (StartedRidingCriterion)register("started_riding", new StartedRidingCriterion());
      LIGHTNING_STRIKE = (LightningStrikeCriterion)register("lightning_strike", new LightningStrikeCriterion());
      USING_ITEM = (UsingItemCriterion)register("using_item", new UsingItemCriterion());
      FALL_FROM_HEIGHT = (TravelCriterion)register("fall_from_height", new TravelCriterion());
      RIDE_ENTITY_IN_LAVA = (TravelCriterion)register("ride_entity_in_lava", new TravelCriterion());
      KILL_MOB_NEAR_SCULK_CATALYST = (OnKilledCriterion)register("kill_mob_near_sculk_catalyst", new OnKilledCriterion());
      ALLAY_DROP_ITEM_ON_BLOCK = (ItemCriterion)register("allay_drop_item_on_block", new ItemCriterion());
      AVOID_VIBRATION = (TickCriterion)register("avoid_vibration", new TickCriterion());
      RECIPE_CRAFTED = (RecipeCraftedCriterion)register("recipe_crafted", new RecipeCraftedCriterion());
      CRAFTER_RECIPE_CRAFTED = (RecipeCraftedCriterion)register("crafter_recipe_crafted", new RecipeCraftedCriterion());
      FALL_AFTER_EXPLOSION = (FallAfterExplosionCriterion)register("fall_after_explosion", new FallAfterExplosionCriterion());
   }
}
