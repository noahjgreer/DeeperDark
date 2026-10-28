package net.noahsarch.deeperdark.config;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.DeeperDarkConfig.ConfigInstance;
import net.noahsarch.deeperdark.DeeperDarkConfig.PlayerSoundProfile;
import net.noahsarch.deeperdark.item.ModItems;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Every setting the config menu can edit. Ranges match the "/dd" commands where one exists.
 * Labels and descriptions are the lang keys deeperdark.config.option.KEY and KEY.desc.
 */
public final class ConfigOptions {

    private ConfigOptions() {}

    public record Category(String id, Item icon, List<ConfigOption<ConfigInstance>> options) {}

    private static final int MAX = Integer.MAX_VALUE;
    private static final ConfigOption.Builder<ConfigInstance> G = new ConfigOption.Builder<>(new ConfigInstance());
    private static final ConfigOption.Builder<PlayerSoundProfile> P = new ConfigOption.Builder<>(new PlayerSoundProfile());
    // No player has an empty name, so reading "" gives the listener defaults
    private static final ConfigOption.Builder<String> L = new ConfigOption.Builder<>("");

    /** Server-wide settings, operators only. */
    public static final List<Category> CATEGORIES = List.of(
        new Category("border", Items.COMPASS, List.of(
            G.integer("origin_x", -30_000_000, 30_000_000, c -> c.originX, (c, v) -> c.originX = v),
            G.integer("origin_z", -30_000_000, 30_000_000, c -> c.originZ, (c, v) -> c.originZ = v),
            G.decimal("safe_radius", 1, 30_000_000, c -> c.safeRadius, (c, v) -> c.safeRadius = v),
            G.decimal("force_multiplier", 0, MAX, c -> c.forceMultiplier, (c, v) -> c.forceMultiplier = v),
            G.bool("allow_entity_spawning", c -> c.allowEntitySpawning, (c, v) -> c.allowEntitySpawning = v),
            G.bool("push_survival_only", c -> c.pushSurvivalModeOnly, (c, v) -> c.pushSurvivalModeOnly = v))),
        new Category("world", Items.GRASS_BLOCK, List.of(
            G.decimal("nether_coordinate_multiplier", 0, MAX, c -> c.netherCoordinateMultiplier, (c, v) -> c.netherCoordinateMultiplier = v),
            G.integer("piston_push_limit", 1, 1000, c -> c.pistonPushLimit, (c, v) -> c.pistonPushLimit = v),
            G.bool("void_fog_enabled", c -> c.voidFogEnabled, (c, v) -> c.voidFogEnabled = v),
            G.bool("all_ingredients_consumable", c -> c.allIngredientsConsumable, (c, v) -> c.allIngredientsConsumable = v),
            G.bool("unlock_all_recipes", c -> c.unlockAllRecipes, (c, v) -> c.unlockAllRecipes = v))),
        new Category("mobs", Items.ZOMBIE_HEAD, List.of(
            G.integer("creeper_effect_min_seconds", 1, MAX, c -> c.creeperEffectMinSeconds, (c, v) -> c.creeperEffectMinSeconds = v),
            G.integer("creeper_effect_max_seconds", 1, MAX, c -> c.creeperEffectMaxSeconds, (c, v) -> c.creeperEffectMaxSeconds = v),
            G.bool("baby_skeletons_enabled", c -> c.babySkeletonsEnabled, (c, v) -> c.babySkeletonsEnabled = v),
            G.bool("baby_creepers_enabled", c -> c.babyCreepersEnabled, (c, v) -> c.babyCreepersEnabled = v),
            G.bool("baby_spiders_enabled", c -> c.babySpidersEnabled, (c, v) -> c.babySpidersEnabled = v),
            G.bool("enderman_pick_up_all_blocks", c -> c.endermanPickUpAllBlocks, (c, v) -> c.endermanPickUpAllBlocks = v),
            G.decimal("zombie_follow_range", 1, 128, c -> c.zombieFollowRange, (c, v) -> c.zombieFollowRange = v),
            G.integer("fishing_charged_creeper_chance", 1, MAX, c -> c.fishingChargedCreeperChance, (c, v) -> c.fishingChargedCreeperChance = v))),
        new Category("fortune", Items.DIAMOND_PICKAXE, List.of(
            G.bool("custom_fortune_enabled", c -> c.customFortuneEnabled, (c, v) -> c.customFortuneEnabled = v),
            G.decimal("fortune_1_drop_chance", 0, 1, c -> c.fortune1DropChance, (c, v) -> c.fortune1DropChance = v),
            G.decimal("fortune_2_drop_chance", 0, 1, c -> c.fortune2DropChance, (c, v) -> c.fortune2DropChance = v),
            G.decimal("fortune_3_drop_chance", 0, 1, c -> c.fortune3DropChance, (c, v) -> c.fortune3DropChance = v),
            G.integer("fortune_max_drops", 1, MAX, c -> c.fortuneMaxDrops, (c, v) -> c.fortuneMaxDrops = v))),
        new Category("explosions", Items.TNT, List.of(
            G.bool("explosion_item_knockback_enabled", c -> c.explosionItemKnockbackEnabled, (c, v) -> c.explosionItemKnockbackEnabled = v),
            G.decimal("explosion_item_knockback_multiplier", 1, MAX, c -> c.explosionItemKnockbackMultiplier, (c, v) -> c.explosionItemKnockbackMultiplier = v))),
        new Category("moss", Items.MOSSY_COBBLESTONE, List.of(
            G.bool("moss_growth_enabled", c -> c.mossGrowthEnabled, (c, v) -> c.mossGrowthEnabled = v),
            G.integer("moss_tick_check_frequency", 1, MAX, c -> c.mossTickCheckFrequency, (c, v) -> c.mossTickCheckFrequency = v),
            G.decimal("moss_base_chance", 0, 1, c -> c.mossBaseChance, (c, v) -> c.mossBaseChance = v),
            G.decimal("moss_nearby_bonus", 0, 1, c -> c.mossNearbyBonus, (c, v) -> c.mossNearbyBonus = v),
            G.decimal("moss_underwater_multiplier", 0.1, MAX, c -> c.mossUnderwaterMultiplier, (c, v) -> c.mossUnderwaterMultiplier = v),
            G.decimal("stone_brick_moss_multiplier", 0, MAX, c -> c.stoneBrickMossMultiplier, (c, v) -> c.stoneBrickMossMultiplier = v))),
        new Category("combat", Items.IRON_SWORD, List.of(
            G.bool("sword_blocking_enabled", c -> c.swordBlockingEnabled, (c, v) -> c.swordBlockingEnabled = v),
            G.bool("sword_blocking_requires_no_shield", c -> c.swordBlockingRequiresNoShield, (c, v) -> c.swordBlockingRequiresNoShield = v))),
        new Category("leaves", Items.OAK_LEAVES, List.of(
            G.bool("leaves_be_gone_enabled", c -> c.leavesBeGoneEnabled, (c, v) -> c.leavesBeGoneEnabled = v),
            G.integer("leaves_decay_min_ticks", 1, MAX, c -> c.leavesDecayMinTicks, (c, v) -> c.leavesDecayMinTicks = v),
            G.integer("leaves_decay_max_ticks", 1, MAX, c -> c.leavesDecayMaxTicks, (c, v) -> c.leavesDecayMaxTicks = v),
            G.bool("leaves_be_gone_ignore_other_leaf_types", c -> c.leavesBeGoneIgnoreOtherLeafTypes, (c, v) -> c.leavesBeGoneIgnoreOtherLeafTypes = v))),
        new Category("magnets", ModItems.IRON_ITEM_MAGNET, List.of(
            G.decimal("copper_magnet_radius", 0, 256, c -> c.itemMagnet.copper.radius, (c, v) -> c.itemMagnet.copper.radius = v),
            G.decimal("copper_magnet_strength", 0, 10, c -> c.itemMagnet.copper.passiveStrength, (c, v) -> c.itemMagnet.copper.passiveStrength = v),
            G.decimal("iron_magnet_radius", 0, 256, c -> c.itemMagnet.iron.radius, (c, v) -> c.itemMagnet.iron.radius = v),
            G.decimal("iron_magnet_strength", 0, 10, c -> c.itemMagnet.iron.passiveStrength, (c, v) -> c.itemMagnet.iron.passiveStrength = v),
            G.decimal("gold_magnet_radius", 0, 256, c -> c.itemMagnet.gold.radius, (c, v) -> c.itemMagnet.gold.radius = v),
            G.decimal("gold_magnet_strength", 0, 10, c -> c.itemMagnet.gold.passiveStrength, (c, v) -> c.itemMagnet.gold.passiveStrength = v),
            G.decimal("diamond_magnet_radius", 0, 256, c -> c.itemMagnet.diamond.radius, (c, v) -> c.itemMagnet.diamond.radius = v),
            G.decimal("diamond_magnet_strength", 0, 10, c -> c.itemMagnet.diamond.passiveStrength, (c, v) -> c.itemMagnet.diamond.passiveStrength = v),
            G.decimal("netherite_magnet_radius", 0, 256, c -> c.itemMagnet.netherite.radius, (c, v) -> c.itemMagnet.netherite.radius = v),
            G.decimal("netherite_magnet_strength", 0, 10, c -> c.itemMagnet.netherite.passiveStrength, (c, v) -> c.itemMagnet.netherite.passiveStrength = v))),
        new Category("creature", Items.SCULK_SHRIEKER, List.of(
            G.integer("creature.pathfinding_min_dist", 1, 1000, c -> c.creature.pathfindingMinDist, (c, v) -> c.creature.pathfindingMinDist = v),
            G.integer("creature.pathfinding_max_dist", 1, 1000, c -> c.creature.pathfindingMaxDist, (c, v) -> c.creature.pathfindingMaxDist = v),
            G.integer("creature.pathfinding_max_y", -64, 320, c -> c.creature.pathfindingMaxY, (c, v) -> c.creature.pathfindingMaxY = v),
            G.integer("creature.validity_frequency", 1, 100000, c -> c.creature.validityFrequency, (c, v) -> c.creature.validityFrequency = v),
            G.decimal("creature.validity_roll", 0, 1, c -> c.creature.validityRoll, (c, v) -> c.creature.validityRoll = v),
            G.integer("creature.entity_spacing", 1, 500, c -> c.creature.entitySpacing, (c, v) -> c.creature.entitySpacing = v),
            G.integer("creature.trail_separation", 1, 50, c -> c.creature.trailSeparation, (c, v) -> c.creature.trailSeparation = v),
            G.decimal("creature.trail_reach", 0, 1, c -> c.creature.trailReach, (c, v) -> c.creature.trailReach = v),
            G.decimal("creature.jitter_max", 0, 10, c -> c.creature.jitterMax, (c, v) -> c.creature.jitterMax = v),
            G.integer("creature.player_distance_tolerance", 1, 50, c -> c.creature.playerDistanceTolerance, (c, v) -> c.creature.playerDistanceTolerance = v),
            G.decimal("creature.chase_frequency", 0, 1, c -> c.creature.chaseFrequency, (c, v) -> c.creature.chaseFrequency = v),
            G.integer("creature.chase_path_min_dist", 1, 500, c -> c.creature.chasePathMinDist, (c, v) -> c.creature.chasePathMinDist = v),
            G.integer("creature.chase_path_max_dist", 1, 500, c -> c.creature.chasePathMaxDist, (c, v) -> c.creature.chasePathMaxDist = v),
            G.decimal("creature.movement_speed", 0.1, 50, c -> c.creature.movementSpeed, (c, v) -> c.creature.movementSpeed = v),
            G.integer("creature.evasion_timer", 1, 100000, c -> c.creature.evasionTimer, (c, v) -> c.creature.evasionTimer = v),
            G.decimal("creature.death_frequency", 0, 1, c -> c.creature.deathFrequency, (c, v) -> c.creature.deathFrequency = v),
            G.decimal("creature.echo_chance", 0, 1, c -> c.creature.echoChance, (c, v) -> c.creature.echoChance = v),
            G.decimal("creature.echo_trigger_radius", 0.1, 20, c -> c.creature.echoTriggerRadius, (c, v) -> c.creature.echoTriggerRadius = v),
            G.decimal("creature.torch_removal_chance", 0, 1, c -> c.creature.torchRemovalChance, (c, v) -> c.creature.torchRemovalChance = v),
            G.decimal("creature.projectile_rejection_chance", 0, 1, c -> c.creature.projectileRejectionChance, (c, v) -> c.creature.projectileRejectionChance = v),
            G.integer("creature.projectile_rejection_delay", 0, 200, c -> c.creature.projectileRejectionDelay, (c, v) -> c.creature.projectileRejectionDelay = v),
            G.integer("creature.on_screen_tolerance", 1, 1000, c -> c.creature.onScreenTolerance, (c, v) -> c.creature.onScreenTolerance = v),
            G.integer("creature.despawn_delay", 1, 1000000, c -> c.creature.despawnDelay, (c, v) -> c.creature.despawnDelay = v),
            G.bool("creature.enable_debug_logging", c -> c.creature.enableDebugLogging, (c, v) -> c.creature.enableDebugLogging = v),
            G.bool("creature.enable_debug_glow", c -> c.creature.enableDebugGlow, (c, v) -> c.creature.enableDebugGlow = v),
            G.bool("creature.enable_debug_path", c -> c.creature.enableDebugPath, (c, v) -> c.creature.enableDebugPath = v),
            G.integer("creature.debug_path_duration", 1, 60, c -> c.creature.debugPathDuration, (c, v) -> c.creature.debugPathDuration = v))),
        new Category("creepiness", Items.ENDER_EYE, List.of(
            G.bool("creepiness.enabled", c -> c.creepiness.enabled, (c, v) -> c.creepiness.enabled = v),
            G.integer("creepiness.min_delay_ticks", 20, MAX, c -> c.creepiness.minDelayTicks, (c, v) -> c.creepiness.minDelayTicks = v),
            G.integer("creepiness.max_delay_ticks", 20, MAX, c -> c.creepiness.maxDelayTicks, (c, v) -> c.creepiness.maxDelayTicks = v),
            G.bool("creepiness.cave_sounds", c -> c.creepiness.caveSounds, (c, v) -> c.creepiness.caveSounds = v),
            G.bool("creepiness.mining", c -> c.creepiness.mining, (c, v) -> c.creepiness.mining = v),
            G.bool("creepiness.footsteps", c -> c.creepiness.footsteps, (c, v) -> c.creepiness.footsteps = v),
            G.bool("creepiness.doors", c -> c.creepiness.doors, (c, v) -> c.creepiness.doors = v),
            G.bool("creepiness.phantom_mobs", c -> c.creepiness.phantomMobs, (c, v) -> c.creepiness.phantomMobs = v),
            G.bool("creepiness.follower", c -> c.creepiness.follower, (c, v) -> c.creepiness.follower = v),
            G.bool("creepiness.ghost_blocks", c -> c.creepiness.ghostBlocks, (c, v) -> c.creepiness.ghostBlocks = v))));

    public static final Map<String, ConfigOption<ConfigInstance>> SERVER = CATEGORIES.stream()
        .flatMap(category -> category.options().stream())
        .collect(Collectors.toMap(ConfigOption::key, Function.identity()));

    /** A player's sound profile: what everyone hears when that player chats, dies, joins or is hurt. */
    public static final List<ConfigOption<PlayerSoundProfile>> PROFILE = List.of(
        P.sound("send_message_sound", p -> p.sendMessageSound, (p, v) -> p.sendMessageSound = v),
        P.sound("death_message_sound", p -> p.deathMessageSound, (p, v) -> p.deathMessageSound = v),
        P.sound("join_message_sound", p -> p.joinMessageSound, (p, v) -> p.joinMessageSound = v),
        P.sound("hurt_sound", p -> p.hurtSound, (p, v) -> p.hurtSound = v),
        P.decimal("pitch", 0.01, 4, p -> p.pitch, (p, v) -> p.pitch = v),
        P.decimal("pitch_deviance", 0, 2, p -> p.pitchDeviance, (p, v) -> p.pitchDeviance = v));

    public static <T> Optional<ConfigOption<T>> find(List<ConfigOption<T>> options, String key) {
        return options.stream().filter(option -> option.key().equals(key)).findFirst();
    }

    /** How a player hears everyone else's sounds, keyed by the listening player's name. */
    public static final List<ConfigOption<String>> LISTENER = List.of(
        L.bool("hear_player_sounds",
            name -> DeeperDarkConfig.get().playerSoundExclusions.stream().noneMatch(name::equalsIgnoreCase),
            (name, hear) -> {
                List<String> exclusions = DeeperDarkConfig.get().playerSoundExclusions;
                exclusions.removeIf(name::equalsIgnoreCase);
                if (!hear) exclusions.add(name.toLowerCase(Locale.ROOT));
            }),
        L.decimal("player_sound_volume", 0, 1,
            name -> DeeperDarkConfig.get().playerSoundVolumes.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .map(Map.Entry::getValue).findFirst().orElse(0.8),
            (name, volume) -> {
                Map<String, Double> volumes = DeeperDarkConfig.get().playerSoundVolumes;
                volumes.keySet().removeIf(name::equalsIgnoreCase);
                volumes.put(name, volume);
            }));
}
