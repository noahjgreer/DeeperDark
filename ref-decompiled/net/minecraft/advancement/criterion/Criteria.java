/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AnyBlockUseCriterion
 *  net.minecraft.advancement.criterion.BeeNestDestroyedCriterion
 *  net.minecraft.advancement.criterion.BredAnimalsCriterion
 *  net.minecraft.advancement.criterion.BrewedPotionCriterion
 *  net.minecraft.advancement.criterion.ChangedDimensionCriterion
 *  net.minecraft.advancement.criterion.ChanneledLightningCriterion
 *  net.minecraft.advancement.criterion.ConstructBeaconCriterion
 *  net.minecraft.advancement.criterion.ConsumeItemCriterion
 *  net.minecraft.advancement.criterion.Criteria
 *  net.minecraft.advancement.criterion.Criterion
 *  net.minecraft.advancement.criterion.CuredZombieVillagerCriterion
 *  net.minecraft.advancement.criterion.DefaultBlockUseCriterion
 *  net.minecraft.advancement.criterion.EffectsChangedCriterion
 *  net.minecraft.advancement.criterion.EnchantedItemCriterion
 *  net.minecraft.advancement.criterion.EnterBlockCriterion
 *  net.minecraft.advancement.criterion.EntityHurtPlayerCriterion
 *  net.minecraft.advancement.criterion.FallAfterExplosionCriterion
 *  net.minecraft.advancement.criterion.FilledBucketCriterion
 *  net.minecraft.advancement.criterion.FishingRodHookedCriterion
 *  net.minecraft.advancement.criterion.ImpossibleCriterion
 *  net.minecraft.advancement.criterion.InventoryChangedCriterion
 *  net.minecraft.advancement.criterion.ItemCriterion
 *  net.minecraft.advancement.criterion.ItemDurabilityChangedCriterion
 *  net.minecraft.advancement.criterion.KilledByArrowCriterion
 *  net.minecraft.advancement.criterion.LevitationCriterion
 *  net.minecraft.advancement.criterion.LightningStrikeCriterion
 *  net.minecraft.advancement.criterion.OnKilledCriterion
 *  net.minecraft.advancement.criterion.PlayerGeneratesContainerLootCriterion
 *  net.minecraft.advancement.criterion.PlayerHurtEntityCriterion
 *  net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion
 *  net.minecraft.advancement.criterion.RecipeCraftedCriterion
 *  net.minecraft.advancement.criterion.RecipeUnlockedCriterion
 *  net.minecraft.advancement.criterion.ShotCrossbowCriterion
 *  net.minecraft.advancement.criterion.SlideDownBlockCriterion
 *  net.minecraft.advancement.criterion.SpearMobsCriterion
 *  net.minecraft.advancement.criterion.StartedRidingCriterion
 *  net.minecraft.advancement.criterion.SummonedEntityCriterion
 *  net.minecraft.advancement.criterion.TameAnimalCriterion
 *  net.minecraft.advancement.criterion.TargetHitCriterion
 *  net.minecraft.advancement.criterion.ThrownItemPickedUpByEntityCriterion
 *  net.minecraft.advancement.criterion.TickCriterion
 *  net.minecraft.advancement.criterion.TravelCriterion
 *  net.minecraft.advancement.criterion.UsedEnderEyeCriterion
 *  net.minecraft.advancement.criterion.UsedTotemCriterion
 *  net.minecraft.advancement.criterion.UsingItemCriterion
 *  net.minecraft.advancement.criterion.VillagerTradeCriterion
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.Registry
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AnyBlockUseCriterion;
import net.minecraft.advancement.criterion.BeeNestDestroyedCriterion;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.BrewedPotionCriterion;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.ChanneledLightningCriterion;
import net.minecraft.advancement.criterion.ConstructBeaconCriterion;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CuredZombieVillagerCriterion;
import net.minecraft.advancement.criterion.DefaultBlockUseCriterion;
import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.advancement.criterion.EnchantedItemCriterion;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion;
import net.minecraft.advancement.criterion.FallAfterExplosionCriterion;
import net.minecraft.advancement.criterion.FilledBucketCriterion;
import net.minecraft.advancement.criterion.FishingRodHookedCriterion;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.ItemDurabilityChangedCriterion;
import net.minecraft.advancement.criterion.KilledByArrowCriterion;
import net.minecraft.advancement.criterion.LevitationCriterion;
import net.minecraft.advancement.criterion.LightningStrikeCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerGeneratesContainerLootCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion;
import net.minecraft.advancement.criterion.RecipeCraftedCriterion;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.advancement.criterion.SlideDownBlockCriterion;
import net.minecraft.advancement.criterion.SpearMobsCriterion;
import net.minecraft.advancement.criterion.StartedRidingCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.advancement.criterion.TargetHitCriterion;
import net.minecraft.advancement.criterion.ThrownItemPickedUpByEntityCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.advancement.criterion.TravelCriterion;
import net.minecraft.advancement.criterion.UsedEnderEyeCriterion;
import net.minecraft.advancement.criterion.UsedTotemCriterion;
import net.minecraft.advancement.criterion.UsingItemCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Criteria {
    public static final Codec<Criterion<?>> CODEC = Registries.CRITERION.getCodec();
    public static final ImpossibleCriterion IMPOSSIBLE = (ImpossibleCriterion)Criteria.register((String)"impossible", (Criterion)new ImpossibleCriterion());
    public static final OnKilledCriterion PLAYER_KILLED_ENTITY = (OnKilledCriterion)Criteria.register((String)"player_killed_entity", (Criterion)new OnKilledCriterion());
    public static final OnKilledCriterion ENTITY_KILLED_PLAYER = (OnKilledCriterion)Criteria.register((String)"entity_killed_player", (Criterion)new OnKilledCriterion());
    public static final EnterBlockCriterion ENTER_BLOCK = (EnterBlockCriterion)Criteria.register((String)"enter_block", (Criterion)new EnterBlockCriterion());
    public static final InventoryChangedCriterion INVENTORY_CHANGED = (InventoryChangedCriterion)Criteria.register((String)"inventory_changed", (Criterion)new InventoryChangedCriterion());
    public static final RecipeUnlockedCriterion RECIPE_UNLOCKED = (RecipeUnlockedCriterion)Criteria.register((String)"recipe_unlocked", (Criterion)new RecipeUnlockedCriterion());
    public static final PlayerHurtEntityCriterion PLAYER_HURT_ENTITY = (PlayerHurtEntityCriterion)Criteria.register((String)"player_hurt_entity", (Criterion)new PlayerHurtEntityCriterion());
    public static final EntityHurtPlayerCriterion ENTITY_HURT_PLAYER = (EntityHurtPlayerCriterion)Criteria.register((String)"entity_hurt_player", (Criterion)new EntityHurtPlayerCriterion());
    public static final EnchantedItemCriterion ENCHANTED_ITEM = (EnchantedItemCriterion)Criteria.register((String)"enchanted_item", (Criterion)new EnchantedItemCriterion());
    public static final FilledBucketCriterion FILLED_BUCKET = (FilledBucketCriterion)Criteria.register((String)"filled_bucket", (Criterion)new FilledBucketCriterion());
    public static final BrewedPotionCriterion BREWED_POTION = (BrewedPotionCriterion)Criteria.register((String)"brewed_potion", (Criterion)new BrewedPotionCriterion());
    public static final ConstructBeaconCriterion CONSTRUCT_BEACON = (ConstructBeaconCriterion)Criteria.register((String)"construct_beacon", (Criterion)new ConstructBeaconCriterion());
    public static final UsedEnderEyeCriterion USED_ENDER_EYE = (UsedEnderEyeCriterion)Criteria.register((String)"used_ender_eye", (Criterion)new UsedEnderEyeCriterion());
    public static final SummonedEntityCriterion SUMMONED_ENTITY = (SummonedEntityCriterion)Criteria.register((String)"summoned_entity", (Criterion)new SummonedEntityCriterion());
    public static final BredAnimalsCriterion BRED_ANIMALS = (BredAnimalsCriterion)Criteria.register((String)"bred_animals", (Criterion)new BredAnimalsCriterion());
    public static final TickCriterion LOCATION = (TickCriterion)Criteria.register((String)"location", (Criterion)new TickCriterion());
    public static final TickCriterion SLEPT_IN_BED = (TickCriterion)Criteria.register((String)"slept_in_bed", (Criterion)new TickCriterion());
    public static final CuredZombieVillagerCriterion CURED_ZOMBIE_VILLAGER = (CuredZombieVillagerCriterion)Criteria.register((String)"cured_zombie_villager", (Criterion)new CuredZombieVillagerCriterion());
    public static final VillagerTradeCriterion VILLAGER_TRADE = (VillagerTradeCriterion)Criteria.register((String)"villager_trade", (Criterion)new VillagerTradeCriterion());
    public static final ItemDurabilityChangedCriterion ITEM_DURABILITY_CHANGED = (ItemDurabilityChangedCriterion)Criteria.register((String)"item_durability_changed", (Criterion)new ItemDurabilityChangedCriterion());
    public static final LevitationCriterion LEVITATION = (LevitationCriterion)Criteria.register((String)"levitation", (Criterion)new LevitationCriterion());
    public static final ChangedDimensionCriterion CHANGED_DIMENSION = (ChangedDimensionCriterion)Criteria.register((String)"changed_dimension", (Criterion)new ChangedDimensionCriterion());
    public static final TickCriterion TICK = (TickCriterion)Criteria.register((String)"tick", (Criterion)new TickCriterion());
    public static final TameAnimalCriterion TAME_ANIMAL = (TameAnimalCriterion)Criteria.register((String)"tame_animal", (Criterion)new TameAnimalCriterion());
    public static final ItemCriterion PLACED_BLOCK = (ItemCriterion)Criteria.register((String)"placed_block", (Criterion)new ItemCriterion());
    public static final ConsumeItemCriterion CONSUME_ITEM = (ConsumeItemCriterion)Criteria.register((String)"consume_item", (Criterion)new ConsumeItemCriterion());
    public static final EffectsChangedCriterion EFFECTS_CHANGED = (EffectsChangedCriterion)Criteria.register((String)"effects_changed", (Criterion)new EffectsChangedCriterion());
    public static final UsedTotemCriterion USED_TOTEM = (UsedTotemCriterion)Criteria.register((String)"used_totem", (Criterion)new UsedTotemCriterion());
    public static final TravelCriterion NETHER_TRAVEL = (TravelCriterion)Criteria.register((String)"nether_travel", (Criterion)new TravelCriterion());
    public static final FishingRodHookedCriterion FISHING_ROD_HOOKED = (FishingRodHookedCriterion)Criteria.register((String)"fishing_rod_hooked", (Criterion)new FishingRodHookedCriterion());
    public static final ChanneledLightningCriterion CHANNELED_LIGHTNING = (ChanneledLightningCriterion)Criteria.register((String)"channeled_lightning", (Criterion)new ChanneledLightningCriterion());
    public static final ShotCrossbowCriterion SHOT_CROSSBOW = (ShotCrossbowCriterion)Criteria.register((String)"shot_crossbow", (Criterion)new ShotCrossbowCriterion());
    public static final SpearMobsCriterion SPEAR_MOBS = (SpearMobsCriterion)Criteria.register((String)"spear_mobs", (Criterion)new SpearMobsCriterion());
    public static final KilledByArrowCriterion KILLED_BY_ARROW = (KilledByArrowCriterion)Criteria.register((String)"killed_by_arrow", (Criterion)new KilledByArrowCriterion());
    public static final TickCriterion HERO_OF_THE_VILLAGE = (TickCriterion)Criteria.register((String)"hero_of_the_village", (Criterion)new TickCriterion());
    public static final TickCriterion VOLUNTARY_EXILE = (TickCriterion)Criteria.register((String)"voluntary_exile", (Criterion)new TickCriterion());
    public static final SlideDownBlockCriterion SLIDE_DOWN_BLOCK = (SlideDownBlockCriterion)Criteria.register((String)"slide_down_block", (Criterion)new SlideDownBlockCriterion());
    public static final BeeNestDestroyedCriterion BEE_NEST_DESTROYED = (BeeNestDestroyedCriterion)Criteria.register((String)"bee_nest_destroyed", (Criterion)new BeeNestDestroyedCriterion());
    public static final TargetHitCriterion TARGET_HIT = (TargetHitCriterion)Criteria.register((String)"target_hit", (Criterion)new TargetHitCriterion());
    public static final ItemCriterion ITEM_USED_ON_BLOCK = (ItemCriterion)Criteria.register((String)"item_used_on_block", (Criterion)new ItemCriterion());
    public static final DefaultBlockUseCriterion DEFAULT_BLOCK_USE = (DefaultBlockUseCriterion)Criteria.register((String)"default_block_use", (Criterion)new DefaultBlockUseCriterion());
    public static final AnyBlockUseCriterion ANY_BLOCK_USE = (AnyBlockUseCriterion)Criteria.register((String)"any_block_use", (Criterion)new AnyBlockUseCriterion());
    public static final PlayerGeneratesContainerLootCriterion PLAYER_GENERATES_CONTAINER_LOOT = (PlayerGeneratesContainerLootCriterion)Criteria.register((String)"player_generates_container_loot", (Criterion)new PlayerGeneratesContainerLootCriterion());
    public static final ThrownItemPickedUpByEntityCriterion THROWN_ITEM_PICKED_UP_BY_ENTITY = (ThrownItemPickedUpByEntityCriterion)Criteria.register((String)"thrown_item_picked_up_by_entity", (Criterion)new ThrownItemPickedUpByEntityCriterion());
    public static final ThrownItemPickedUpByEntityCriterion THROWN_ITEM_PICKED_UP_BY_PLAYER = (ThrownItemPickedUpByEntityCriterion)Criteria.register((String)"thrown_item_picked_up_by_player", (Criterion)new ThrownItemPickedUpByEntityCriterion());
    public static final PlayerInteractedWithEntityCriterion PLAYER_INTERACTED_WITH_ENTITY = (PlayerInteractedWithEntityCriterion)Criteria.register((String)"player_interacted_with_entity", (Criterion)new PlayerInteractedWithEntityCriterion());
    public static final PlayerInteractedWithEntityCriterion PLAYER_SHEARED_EQUIPMENT = (PlayerInteractedWithEntityCriterion)Criteria.register((String)"player_sheared_equipment", (Criterion)new PlayerInteractedWithEntityCriterion());
    public static final StartedRidingCriterion STARTED_RIDING = (StartedRidingCriterion)Criteria.register((String)"started_riding", (Criterion)new StartedRidingCriterion());
    public static final LightningStrikeCriterion LIGHTNING_STRIKE = (LightningStrikeCriterion)Criteria.register((String)"lightning_strike", (Criterion)new LightningStrikeCriterion());
    public static final UsingItemCriterion USING_ITEM = (UsingItemCriterion)Criteria.register((String)"using_item", (Criterion)new UsingItemCriterion());
    public static final TravelCriterion FALL_FROM_HEIGHT = (TravelCriterion)Criteria.register((String)"fall_from_height", (Criterion)new TravelCriterion());
    public static final TravelCriterion RIDE_ENTITY_IN_LAVA = (TravelCriterion)Criteria.register((String)"ride_entity_in_lava", (Criterion)new TravelCriterion());
    public static final OnKilledCriterion KILL_MOB_NEAR_SCULK_CATALYST = (OnKilledCriterion)Criteria.register((String)"kill_mob_near_sculk_catalyst", (Criterion)new OnKilledCriterion());
    public static final ItemCriterion ALLAY_DROP_ITEM_ON_BLOCK = (ItemCriterion)Criteria.register((String)"allay_drop_item_on_block", (Criterion)new ItemCriterion());
    public static final TickCriterion AVOID_VIBRATION = (TickCriterion)Criteria.register((String)"avoid_vibration", (Criterion)new TickCriterion());
    public static final RecipeCraftedCriterion RECIPE_CRAFTED = (RecipeCraftedCriterion)Criteria.register((String)"recipe_crafted", (Criterion)new RecipeCraftedCriterion());
    public static final RecipeCraftedCriterion CRAFTER_RECIPE_CRAFTED = (RecipeCraftedCriterion)Criteria.register((String)"crafter_recipe_crafted", (Criterion)new RecipeCraftedCriterion());
    public static final FallAfterExplosionCriterion FALL_AFTER_EXPLOSION = (FallAfterExplosionCriterion)Criteria.register((String)"fall_after_explosion", (Criterion)new FallAfterExplosionCriterion());

    public static <T extends Criterion<?>> T register(String id, T criterion) {
        return (T)((Criterion)Registry.register((Registry)Registries.CRITERION, (String)id, criterion));
    }

    public static Criterion<?> getDefault(Registry<Criterion<?>> registry) {
        return IMPOSSIBLE;
    }
}

