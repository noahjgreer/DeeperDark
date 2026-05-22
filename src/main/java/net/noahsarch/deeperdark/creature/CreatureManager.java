package net.noahsarch.deeperdark.creature;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.Deeperdark;
import net.noahsarch.deeperdark.entity.ModEntities;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.portal.TeleportTransition;

/**
 * Central manager for all creature instances.
 * Handles spawn validity tests, spawning, tick updates, interaction, chase logic,
 * side effects, despawning, and cleanup.
 *
 * Registered as a server tick handler during mod initialization.
 */
public class CreatureManager {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("CreatureManager");

    /** All active creature instances, keyed by creature UUID */
    private static final Map<UUID, CreatureInstance> creatures = new ConcurrentHashMap<>();

    /** Tick counter for spawn validity checks */
    private static int spawnValidityTick = 0;

    /** Active echo zones not tied to a creature anymore */
    private static final List<EchoZone> activeEchoZones = new ArrayList<>();

    /** Players frozen during chase prep */
    private static final Set<UUID> frozenPlayers = new HashSet<>();

    /** Players being chased (to track view jitter per tick) */
    private static final Map<UUID, UUID> playerChaseMap = new HashMap<>(); // playerUUID -> creatureUUID

    /** Players whose non-essential sound categories should be suppressed (used by SoundFilterMixin) */
    private static final Set<UUID> soundSuppressedPlayers = new HashSet<>();

    /** Custom damage type registry key for creature kills */
    private static final ResourceKey<DamageType> CREATURE_DAMAGE_TYPE = ResourceKey.create(
            Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("deeperdark", "creature")
    );

    private record EchoZone(Vec3 position, ServerLevel world, int ambienceSoundVariant, int ticksRemaining, boolean triggered) {}

    // ===== Registration =====

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(CreatureManager::onServerTick);
        Deeperdark.LOGGER.info("[Deeper Dark] Creature system registered");
    }

    /**
     * Checks if a player's non-essential sounds should be suppressed (during chase).
     * Called by SoundFilterMixin to decide whether to cancel PlaySoundS2CPackets.
     */
    public static boolean isPlayerSoundSuppressed(UUID playerUuid) {
        return soundSuppressedPlayers.contains(playerUuid);
    }

    // ===== Debug Messaging =====

    /**
     * Sends a debug message to all online operators when debug logging is enabled.
     * Messages are shown as gray italic chat text, only visible to ops (permission level 2+).
     */
    private static void debugOps(MinecraftServer server, String message) {
        if (!DeeperDarkConfig.get().creature.enableDebugLogging) return;
        Component text = Component.literal("[Creature] " + message).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        Permission gamemasterPerm = new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.permissions().hasPermission(gamemasterPerm)) {
                player.sendSystemMessage(text);
            }
        }
    }

    // ===== Public API =====

    /**
     * Manually spawns a creature at the specified position.
     */
    public static CreatureInstance spawnCreatureAt(ServerLevel world, Vec3 position) {
        CreatureConfig config = DeeperDarkConfig.get().creature;
        UUID id = UUID.randomUUID();
        int variant = world.getRandom().nextInt(4);
        CreatureInstance creature = new CreatureInstance(id, position, world, variant, config.despawnDelay);

        // Pre-roll behavior flags
        rollBehaviorFlags(creature, config, world.getRandom());

        // Spawn the display entity
        spawnDisplayEntity(creature);

        creatures.put(id, creature);

        if (config.enableDebugLogging) {
            LOGGER.info("[Creature] Spawned creature {} at {} variant={} chase={} torch={} echo={} projectile={}",
                    id.toString().substring(0, 8), position, variant,
                    creature.willChase(), creature.willRemoveTorches(), creature.willEcho(), creature.willRejectProjectiles());
            debugOps(world.getServer(), String.format("Spawned creature %s at [%.0f, %.0f, %.0f] (variant=%d, chase=%s)",
                    id.toString().substring(0, 8), position.x, position.y, position.z, variant, creature.willChase()));
        }

        return creature;
    }

    /**
     * Spawns a creature targeting a specific player using placement algorithms.
     */
    public static CreatureInstance spawnCreatureForPlayer(ServerPlayer player, int maxDistOverride) {
        ServerLevel world = (ServerLevel) ((net.minecraft.server.level.ServerLevel) player.level());
        CreatureConfig config = DeeperDarkConfig.get().creature;
        int minDist = config.pathfindingMinDist;
        int maxDist = maxDistOverride > 0 ? maxDistOverride : config.pathfindingMaxDist;
        int maxY = config.pathfindingMaxY;
        boolean debug = config.enableDebugLogging;

        List<Vec3> existingPositions = creatures.values().stream()
                .filter(c -> c.getWorld() == world)
                .map(CreatureInstance::getPosition)
                .toList();

        BlockPos playerPos = player.blockPosition();

        // Try pathtrace first
        CreaturePathfinder.PathResult result = CreaturePathfinder.pathtraceFromPlayer(
                world, playerPos, minDist, maxDist, maxY, existingPositions, config.entitySpacing, debug);

        if (!result.success()) {
            if (debug) LOGGER.info("[Creature] Pathtrace failed for {}: {}. Trying radial.", player.getName().getString(), result.failureReason());
            // Fallback to radial
            result = CreaturePathfinder.radialPlacement(
                    world, playerPos, minDist, maxDist, maxY, existingPositions, config.entitySpacing, debug);
        }

        if (!result.success()) {
            LOGGER.warn("[Creature] Both placement algorithms failed for player {}: {}", player.getName().getString(), result.failureReason());
            return null;
        }

        // Debug path: show particle trail along the pathfinding result
        if (config.enableDebugPath && !result.path().isEmpty()) {
            sendDebugPathParticles(world, result.path(), player);
        }

        CreatureInstance creature = spawnCreatureAt(world, result.position());
        return creature;
    }

    /**
     * Removes a specific creature by UUID.
     */
    public static boolean removeCreature(UUID id) {
        CreatureInstance creature = creatures.remove(id);
        if (creature != null) {
            despawnDisplayEntity(creature);
            return true;
        }
        return false;
    }

    /**
     * Removes all creatures from all worlds.
     */
    public static int clearAllCreatures() {
        int count = creatures.size();
        for (CreatureInstance creature : creatures.values()) {
            despawnDisplayEntity(creature);
        }
        creatures.clear();
        activeEchoZones.clear();
        frozenPlayers.clear();
        playerChaseMap.clear();
        soundSuppressedPlayers.clear();
        return count;
    }

    /**
     * Gets all active creature instances (read-only).
     */
    public static Collection<CreatureInstance> getAllCreatures() {
        return Collections.unmodifiableCollection(creatures.values());
    }

    /**
     * Gets a creature by UUID.
     */
    public static CreatureInstance getCreature(UUID id) {
        return creatures.get(id);
    }

    // ===== Main Tick Loop =====

    private static void onServerTick(MinecraftServer server) {
        CreatureConfig config = DeeperDarkConfig.get().creature;
        boolean debug = config.enableDebugLogging;

        // Spawn validity check
        spawnValidityTick++;
        if (spawnValidityTick >= config.validityFrequency) {
            spawnValidityTick = 0;
            runSpawnValidityTest(server, config, debug);
        }

        // Tick all creatures
        List<UUID> toRemove = new ArrayList<>();
        for (CreatureInstance creature : creatures.values()) {
            try {
                tickCreature(creature, server, config, debug);
            } catch (Exception e) {
                LOGGER.error("[Creature] Error ticking creature {}: {}", creature.getCreatureId(), e.getMessage(), e);
            }

            // Check despawn
            if (creature.getDespawnTimer() <= 0 && creature.getCurrentSequence() != CreatureInstance.Sequence.CHASING
                    && creature.getCurrentSequence() != CreatureInstance.Sequence.CHASE_PREP) {
                toRemove.add(creature.getCreatureId());
            }
        }

        for (UUID id : toRemove) {
            CreatureInstance creature = creatures.remove(id);
            if (creature != null) {
                despawnDisplayEntity(creature);
                if (debug) LOGGER.info("[Creature] Despawned creature {} (timer expired)", id.toString().substring(0, 8));
            }
        }

        // Tick echo zones
        tickEchoZones(server, config, debug);

        // Tick frozen players
        tickFrozenPlayers(server);

        // Tick chase view jitter
        tickChasePitchInfluence(server, config);

        // Tick projectile rejection for all creatures
        tickProjectileRejection(server, config, debug);

        // Sync debug glow state on all existing display entities
        syncDebugGlow(config);
    }

    /**
     * Syncs the debug glow state for all active creature display entities.
     * Called every tick so toggling the config affects existing creatures immediately.
     */
    private static void syncDebugGlow(CreatureConfig config) {
        boolean glowEnabled = config.enableDebugGlow;
        for (CreatureInstance creature : creatures.values()) {
            if (creature.getEntityUuid() == null) continue;
            ServerLevel world = creature.getWorld();
            Entity entity = world.getEntity(creature.getEntityUuid());
            if (entity != null && entity.hasGlowingTag() != glowEnabled) {
                entity.setGlowingTag(glowEnabled);
            }
        }
    }

    // ===== Spawn Validity Test =====

    private static void runSpawnValidityTest(MinecraftServer server, CreatureConfig config, boolean debug) {
        if (debug) LOGGER.info("[Creature] Running spawn validity test (roll chance: {})", config.validityRoll);

        RandomSource rand = RandomSource.create();
        if (rand.nextDouble() > config.validityRoll) {
            if (debug) LOGGER.info("[Creature] Spawn roll failed");
            return;
        }

        if (debug) LOGGER.info("[Creature] Spawn roll passed! Searching for eligible player...");

        // Shuffle player list for randomness
        List<ServerPlayer> players = new ArrayList<>(server.getPlayerList().getPlayers());
        Collections.shuffle(players, new java.util.Random(rand.nextLong()));

        for (ServerPlayer player : players) {
            if (player.isSpectator()) continue;
            if (player.getY() <= config.pathfindingMaxY) {
                if (debug) LOGGER.info("[Creature] Player {} at Y={} passes Y check, attempting spawn",
                        player.getName().getString(), player.getY());

                CreatureInstance spawned = spawnCreatureForPlayer(player, -1);
                if (spawned != null) {
                    if (debug) LOGGER.info("[Creature] Successfully spawned creature for {}", player.getName().getString());
                    return;
                }
            } else {
                if (debug) LOGGER.info("[Creature] Player {} at Y={} above max Y {}, skipping",
                        player.getName().getString(), player.getY(), config.pathfindingMaxY);
            }
        }

        if (debug) LOGGER.info("[Creature] No eligible players found for spawn");
    }

    // ===== Per-Creature Tick =====

    private static void tickCreature(CreatureInstance creature, MinecraftServer server, CreatureConfig config, boolean debug) {
        creature.incrementTicksAlive();
        creature.incrementSequenceTicks();
        creature.decrementDespawnTimer();

        ServerLevel world = creature.getWorld();
        Vec3 creaturePos = creature.getPosition();

        // Update display entity facing - face nearest player
        updateCreatureFacing(creature, server);

        switch (creature.getCurrentSequence()) {
            case IDLE -> tickIdle(creature, server, config, debug);
            case AMBIENT_SOUND, COPPER_TRAIL -> tickIdleBehaviors(creature, server, config, debug);
            case CHASE_PREP -> tickChasePrep(creature, server, config, debug);
            case CHASING -> tickChasing(creature, server, config, debug);
            case DISAPPEARING -> tickDisappearing(creature, config, debug);
            case ECHO_ZONE -> {
                // Echo zone handled separately in activeEchoZones
            }
            case DESPAWNING -> {
                // Will be cleaned up by despawn check
            }
        }
    }

    // ===== Idle Sequence =====

    private static void tickIdle(CreatureInstance creature, MinecraftServer server, CreatureConfig config, boolean debug) {
        ServerLevel world = creature.getWorld();
        Vec3 creaturePos = creature.getPosition();
        boolean creatureOnAnyScreen = false;
        ServerPlayer closestScreenPlayer = null;
        double closestScreenDist = Double.MAX_VALUE;

        // Check all players for proximity triggers
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (((net.minecraft.server.level.ServerLevel) player.level()) != world) continue;
            if (player.isSpectator()) continue;

            double distance = player.position().distanceTo(creaturePos);

            // Direct interaction check: player within tolerance distance
            if (distance <= config.playerDistanceTolerance) {
                triggerInteraction(creature, player, config, debug);
                return;
            }

            // Direct look = immediate interaction for all creature types
            if (isPlayerLookingAtCreature(player, creaturePos, world)) {
                triggerInteraction(creature, player, config, debug);
                return;
            }

            // On-screen check for non-chase creatures (view tolerance)
            if (!creature.willChase() && isCreatureOnPlayerScreen(player, creaturePos, world)) {
                creatureOnAnyScreen = true;
                if (distance < closestScreenDist) {
                    closestScreenDist = distance;
                    closestScreenPlayer = player;
                }
            }

            // Ambience sound trigger
            if (!creature.isAmbienceSoundPlayed() && distance <= config.pathfindingMinDist) {
                if (debug) LOGGER.info("[Creature] Player {} within ambience range ({} blocks)", player.getName().getString(), distance);
                int variant = world.getRandom().nextInt(8);
                creature.setAmbienceSoundVariant(variant);
                float volume = CreatureSoundHelper.calculateAmbienceVolume(distance, config.pathfindingMinDist);
                CreatureSoundHelper.playAmbienceSound(player, variant, creaturePos, volume);
                creature.setAmbienceSoundPlayed(true);
                creature.setCurrentSequence(CreatureInstance.Sequence.AMBIENT_SOUND);
                if (debug) LOGGER.info("[Creature] Playing ambience{} to {} at volume {}", variant, player.getName().getString(), volume);
            }
        }

        // On-screen tolerance: non-chase creatures despawn after being visible for too long
        if (creatureOnAnyScreen) {
            creature.incrementOnScreenTicks();
            if (creature.getOnScreenTicks() >= config.onScreenTolerance && closestScreenPlayer != null) {
                if (debug) LOGGER.info("[Creature] Non-chase creature {} despawning after {} ticks on screen",
                        creature.getCreatureId().toString().substring(0, 8), creature.getOnScreenTicks());
                triggerDefaultDisappear(creature, closestScreenPlayer, config, debug);
                return;
            }
        } else {
            creature.setOnScreenTicks(0);
        }

        // Handle shaking/jitter based on player visibility
        updateJitter(creature, server, config);
    }

    // ===== Idle Behaviors (Ambient Sound + Copper Trail) =====

    private static void tickIdleBehaviors(CreatureInstance creature, MinecraftServer server, CreatureConfig config, boolean debug) {
        ServerLevel world = creature.getWorld();
        Vec3 creaturePos = creature.getPosition();
        boolean creatureOnAnyScreen = false;
        ServerPlayer closestScreenPlayer = null;
        double closestScreenDist = Double.MAX_VALUE;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (((net.minecraft.server.level.ServerLevel) player.level()) != world) continue;
            if (player.isSpectator()) continue;

            double distance = player.position().distanceTo(creaturePos);

            // Direct interaction check
            if (distance <= config.playerDistanceTolerance) {
                triggerInteraction(creature, player, config, debug);
                return;
            }

            // Direct look = immediate interaction for all creature types
            if (isPlayerLookingAtCreature(player, creaturePos, world)) {
                triggerInteraction(creature, player, config, debug);
                return;
            }

            // On-screen check for non-chase creatures (view tolerance)
            if (!creature.willChase() && isCreatureOnPlayerScreen(player, creaturePos, world)) {
                creatureOnAnyScreen = true;
                if (distance < closestScreenDist) {
                    closestScreenDist = distance;
                    closestScreenPlayer = player;
                }
            }

            // Copper nugget trail trigger: player within 2/3 of pathfinding radius
            double trailTriggerDist = config.pathfindingMinDist * (2.0 / 3.0);
            if (!creature.isCopperTrailSpawned() && distance <= trailTriggerDist) {
                if (creature.getCopperTrailDelayTicks() < 0) {
                    // First tick in range — start the delay timer
                    creature.setCopperTrailDelayTicks(0);
                    creature.setCopperTrailTriggeredByPlayer(player.getUUID());
                    if (debug) LOGGER.info("[Creature] Copper trail trigger started for creature {}, waiting for safe moment",
                            creature.getCreatureId().toString().substring(0, 8));
                } else {
                    creature.incrementCopperTrailDelayTicks();
                }

                // After at least 20 ticks (1 second) delay, try to spawn when no one is looking
                if (creature.getCopperTrailDelayTicks() >= 20) {
                    if (!isAnyPlayerLookingAtPath(server, world, player.position(), creaturePos)) {
                        spawnCopperTrail(creature, player, config, debug);
                    } else if (debug && creature.getCopperTrailDelayTicks() % 20 == 0) {
                        LOGGER.info("[Creature] Copper trail delayed — player(s) looking at path area");
                    }
                }
            }
        }

        // On-screen tolerance: non-chase creatures despawn after being visible for too long
        if (creatureOnAnyScreen) {
            creature.incrementOnScreenTicks();
            if (creature.getOnScreenTicks() >= config.onScreenTolerance && closestScreenPlayer != null) {
                if (debug) LOGGER.info("[Creature] Non-chase creature {} despawning after {} ticks on screen",
                        creature.getCreatureId().toString().substring(0, 8), creature.getOnScreenTicks());
                triggerDefaultDisappear(creature, closestScreenPlayer, config, debug);
                return;
            }
        } else {
            creature.setOnScreenTicks(0);
        }

        // Handle shaking/jitter
        updateJitter(creature, server, config);
    }

    // ===== Interaction Trigger =====

    private static void triggerInteraction(CreatureInstance creature, ServerPlayer player, CreatureConfig config, boolean debug) {
        if (debug) {
            LOGGER.info("[Creature] Interaction triggered by {} with creature {}",
                    player.getName().getString(), creature.getCreatureId().toString().substring(0, 8));
            Vec3 cPos = creature.getPosition();
            debugOps(creature.getWorld().getServer(), String.format("Interaction: %s triggered creature %s at [%.0f, %.0f, %.0f] (will chase: %s)",
                    player.getName().getString(), creature.getCreatureId().toString().substring(0, 8),
                    cPos.x, cPos.y, cPos.z, creature.willChase()));
        }

        creature.setTargetPlayer(player);

        if (creature.willChase()) {
            // Chase behavior
            beginChasePrep(creature, player, config, debug);
        } else {
            // Default behavior: disappear with effects
            triggerDefaultDisappear(creature, player, config, debug);
        }
    }

    // ===== Default Disappear Behavior =====

    private static void triggerDefaultDisappear(CreatureInstance creature, ServerPlayer player, CreatureConfig config, boolean debug) {
        if (debug) LOGGER.info("[Creature] Default disappear behavior for creature {}", creature.getCreatureId().toString().substring(0, 8));

        Vec3 lastPos = creature.getPosition();

        // Apply status effects
        // 5 seconds of Darkness (100 ticks)
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false, true));
        // 2 seconds of Nausea (40 ticks)
        player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 40, 0, false, false, true));
        // 30 seconds of Weakness (600 ticks)
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 0, false, false, true));

        // Remove the display entity
        despawnDisplayEntity(creature);
        creature.setCurrentSequence(CreatureInstance.Sequence.DISAPPEARING);

        // Handle side effects
        handleSideEffects(creature, lastPos, config, debug);
    }

    // ===== Chase Preparation =====

    private static void beginChasePrep(CreatureInstance creature, ServerPlayer player, CreatureConfig config, boolean debug) {
        if (debug) {
            LOGGER.info("[Creature] Chase prep beginning for creature {}, target: {}",
                    creature.getCreatureId().toString().substring(0, 8), player.getName().getString());
            Vec3 cPos = creature.getPosition();
            debugOps(creature.getWorld().getServer(), String.format("Chase prep: creature %s freezing %s (creature at [%.0f, %.0f, %.0f])",
                    creature.getCreatureId().toString().substring(0, 8), player.getName().getString(),
                    cPos.x, cPos.y, cPos.z));
        }

        creature.setCurrentSequence(CreatureInstance.Sequence.CHASE_PREP);
        creature.setTargetPlayer(player);

        // Freeze the player's position and lock gaze at creature
        frozenPlayers.add(player.getUUID());

        // Lock player gaze at creature origin
        Vec3 creaturePos = creature.getPosition().add(0, 1.5, 0); // Look at center of creature
        Vec3 playerEye = player.getEyePosition();
        Vec3 lookVec = creaturePos.subtract(playerEye).normalize();
        float yaw = (float) (Math.atan2(-lookVec.x, lookVec.z) * (180.0 / Math.PI));
        float pitch = (float) (-Math.asin(lookVec.y) * (180.0 / Math.PI));

        player.connection.teleport(
                new PositionMoveRotation(player.position(), Vec3.ZERO, yaw, pitch),
                Set.of()
        );

        // Play hush sound
        CreatureSoundHelper.playHushSound(player, creature.getPosition(), 2.0f);
    }

    private static void tickChasePrep(CreatureInstance creature, MinecraftServer server, CreatureConfig config, boolean debug) {
        int prepTicks = creature.getSequenceTicks();
        ServerPlayer player = getTargetPlayer(creature, server);

        if (player == null) {
            // Target disconnected, abort chase
            if (debug) LOGGER.info("[Creature] Chase target disconnected, aborting");
            creature.setCurrentSequence(CreatureInstance.Sequence.DESPAWNING);
            frozenPlayers.remove(creature.getTargetPlayerUuid());
            return;
        }

        // Jitter intensity logarithmic falloff over 2 seconds (40 ticks) — synced to client renderer
        if (prepTicks <= 40) {
            double jitterFactor = 1.0 - Math.log(1 + prepTicks) / Math.log(41);
            float newIntensity = (float) (creature.getCurrentJitterIntensity() * jitterFactor);
            creature.setCurrentJitterIntensity(newIntensity);
            syncJitterIntensity(creature);
        }

        // Keep player frozen and looking at creature
        Vec3 creaturePos = creature.getPosition().add(0, 1.5, 0);
        Vec3 playerEye = player.getEyePosition();
        Vec3 lookVec = creaturePos.subtract(playerEye).normalize();
        float yaw = (float) (Math.atan2(-lookVec.x, lookVec.z) * (180.0 / Math.PI));
        float pitch = (float) (-Math.asin(lookVec.y) * (180.0 / Math.PI));

        player.connection.teleport(
                new PositionMoveRotation(player.position(), Vec3.ZERO, yaw, pitch),
                Set.of()
        );

        // At 20 ticks: apply blindness
        if (prepTicks == 20) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false, true));
            if (debug) LOGGER.info("[Creature] Blindness applied to {}", player.getName().getString());
        }

        // At 60 ticks (3 seconds of blindness): begin chase
        if (prepTicks >= 60) {
            beginChaseSequence(creature, player, config, debug);
        }
    }

    // ===== Chase Sequence =====

    private static void beginChaseSequence(CreatureInstance creature, ServerPlayer player, CreatureConfig config, boolean debug) {
        if (debug) {
            LOGGER.info("[Creature] Chase sequence beginning for creature {}", creature.getCreatureId().toString().substring(0, 8));
            debugOps(creature.getWorld().getServer(), String.format("Chase started: creature %s chasing %s from [%.0f, %.0f, %.0f]",
                    creature.getCreatureId().toString().substring(0, 8), player.getName().getString(),
                    creature.getPosition().x, creature.getPosition().y, creature.getPosition().z));
        }

        // Unfreeze player
        frozenPlayers.remove(player.getUUID());

        // Apply darkness for 30 seconds
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 600, 0, false, false, true));

        // Stop all ambient/hostile/music/weather sounds so the player only hears footsteps
        CreatureSoundHelper.stopAllAmbientSoundsForChase(player);

        // Register this player for server-side sound suppression (mixin will intercept packets)
        soundSuppressedPlayers.add(player.getUUID());

        // Move creature to a new position within chase range
        ServerLevel world = creature.getWorld();
        BlockPos chasePos = CreaturePathfinder.findChasePosition(
                world, player.blockPosition(),
                config.chasePathMinDist, config.chasePathMaxDist,
                config.pathfindingMaxY, debug
        );

        if (chasePos == null) {
            // Fallback: place at max distance in a random direction
            if (debug) LOGGER.info("[Creature] Could not find chase position, using fallback");
            double angle = world.getRandom().nextDouble() * 2 * Math.PI;
            int dist = config.chasePathMinDist;
            chasePos = player.blockPosition().offset((int) (dist * Math.cos(angle)), 0, (int) (dist * Math.sin(angle)));
        }

        Vec3 newPos = Vec3.atBottomCenterOf(chasePos);
        creature.setPosition(newPos);
        updateDisplayEntityPosition(creature);

        creature.setCurrentSequence(CreatureInstance.Sequence.CHASING);
        creature.setChaseTicks(0);

        // Register chase for pitch influence tracking
        playerChaseMap.put(player.getUUID(), creature.getCreatureId());

        if (debug) {
            LOGGER.info("[Creature] Creature relocated to {} for chase", newPos);
            debugOps(creature.getWorld().getServer(), String.format("Chase: creature relocated to [%.0f, %.0f, %.0f]", newPos.x, newPos.y, newPos.z));
        }
    }

    private static void tickChasing(CreatureInstance creature, MinecraftServer server, CreatureConfig config, boolean debug) {
        creature.incrementChaseTicks();
        ServerPlayer player = getTargetPlayer(creature, server);

        if (player == null) {
            // Target disconnected
            endChase(creature, null, server, config, debug, false);
            return;
        }

        // Check evasion timer (30 seconds = 600 ticks by default)
        if (creature.getChaseTicks() >= config.evasionTimer) {
            // Player escaped!
            if (debug) {
                LOGGER.info("[Creature] Player {} escaped the creature!", player.getName().getString());
                debugOps(server, String.format("Player %s escaped creature %s after %d ticks!",
                        player.getName().getString(), creature.getCreatureId().toString().substring(0, 8),
                        creature.getChaseTicks()));
            }
            endChase(creature, player, server, config, debug, true);
            return;
        }

        // Move creature towards player at configured speed
        Vec3 creaturePos = creature.getPosition();
        Vec3 playerPos = player.position();
        Vec3 direction = playerPos.subtract(creaturePos).normalize();

        // Movement speed is blocks per second, so blocks per tick = speed / 20
        double movePerTick = config.movementSpeed / 20.0;
        Vec3 newPos = creaturePos.add(direction.scale(movePerTick));

        // Keep creature grounded - find solid ground
        BlockPos newBlockPos = BlockPos.containing(newPos);
        newPos = snapToGround(creature.getWorld(), newBlockPos, newPos);

        creature.setPosition(newPos);
        updateDisplayEntityPosition(creature);

        // Play rapid footstep sounds every 2 ticks
        if (creature.getChaseTicks() % 2 == 0) {
            CreatureSoundHelper.playFootstepSound(player, newPos, creature.getWorld());
        }

        // Check if creature caught the player (within 1.5 blocks)
        double distToPlayer = newPos.distanceTo(playerPos);
        if (distToPlayer <= 1.5) {
            if (debug) {
                LOGGER.info("[Creature] Creature caught player {}", player.getName().getString());
                debugOps(server, String.format("Creature %s caught %s at [%.0f, %.0f, %.0f]",
                        creature.getCreatureId().toString().substring(0, 8), player.getName().getString(),
                        newPos.x, newPos.y, newPos.z));
            }
            handleCatchPlayer(creature, player, server, config, debug);
            return;
        }

        // Update creature facing to look at player
        Vec3 lookDir = playerPos.subtract(newPos);
        float yaw = (float) (Math.atan2(-lookDir.x, lookDir.z) * (180.0 / Math.PI));
        creature.setYaw(yaw);
    }

    private static void handleCatchPlayer(CreatureInstance creature, ServerPlayer player,
                                           MinecraftServer server, CreatureConfig config, boolean debug) {
        RandomSource rand = RandomSource.create();
        boolean willDie = rand.nextDouble() < config.deathFrequency;

        if (willDie) {
            // Kill the player with custom damage type — uses "death.attack.creature" translation key
            if (debug) {
                LOGGER.info("[Creature] Player {} will be killed", player.getName().getString());
                debugOps(server, String.format("Catch outcome: %s will be KILLED", player.getName().getString()));
            }
            ServerLevel playerWorld = (ServerLevel) player.level();
            net.minecraft.core.Holder<net.minecraft.world.damagesource.DamageType> damageTypeHolder =
                playerWorld.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(CREATURE_DAMAGE_TYPE);
            DamageSource creatureDamage = new DamageSource(damageTypeHolder);
            // Stop ambience sounds BEFORE dealing damage so the packet reaches the client
            CreatureSoundHelper.stopAllCreatureSounds(player);
            player.hurtServer(playerWorld, creatureDamage, Float.MAX_VALUE);
        } else {
            // Teleport to spawn — manually extract coordinates to avoid corrupt TeleportTransition
            if (debug) {
                LOGGER.info("[Creature] Player {} will be teleported to spawn", player.getName().getString());
                debugOps(server, String.format("Catch outcome: %s will be teleported to spawn", player.getName().getString()));
            }
            teleportPlayerToSpawn(player, server, debug);
        }

        endChase(creature, player, server, config, debug, false);
    }

    /**
     * Safely teleports a player to their respawn point, or world spawn as fallback.
     * Manually extracts coordinates instead of using getRespawnTarget to avoid corrupt positions.
     */
    private static void teleportPlayerToSpawn(ServerPlayer player, MinecraftServer server, boolean debug) {
        // Try player's personal respawn point first (bed/respawn anchor)
        ServerPlayer.RespawnConfig respawn = player.getRespawnConfig();
        if (respawn != null) {
            net.minecraft.resources.ResourceKey<Level> dimension = respawn.respawnData().dimension();
            BlockPos spawnPos = respawn.respawnData().pos();
            ServerLevel spawnWorld = server.getLevel(dimension);

            if (spawnWorld != null) {
                Vec3 pos = new Vec3(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                if (debug) LOGGER.info("[Creature] Teleporting {} to personal spawn at {} in {}",
                        player.getName().getString(), pos, dimension.identifier());
                player.teleport(new net.minecraft.world.level.portal.TeleportTransition(
                        spawnWorld, pos, Vec3.ZERO, respawn.respawnData().yaw(), respawn.respawnData().pitch(),
                        net.minecraft.world.level.portal.TeleportTransition.DO_NOTHING
                ));
                return;
            }
        }

        // Fallback: world spawn
        teleportToWorldSpawn(player, server);
    }

    private static void endChase(CreatureInstance creature, ServerPlayer player,
                                  MinecraftServer server, CreatureConfig config, boolean debug, boolean escaped) {
        if (debug) LOGGER.info("[Creature] Chase ended for creature {} (escaped: {})",
                creature.getCreatureId().toString().substring(0, 8), escaped);

        // Drop diamond if player escaped
        if (escaped && player != null) {
            ServerLevel world = creature.getWorld();
            Vec3 dropPos = creature.getPosition();
            ItemEntity diamondEntity = new ItemEntity(world, dropPos.x, dropPos.y, dropPos.z, new ItemStack(Items.DIAMOND));
            world.addFreshEntity(diamondEntity);
            if (debug) LOGGER.info("[Creature] Diamond dropped at {}", dropPos);
        }

        // Stop sounds for the target player
        if (player != null) {
            CreatureSoundHelper.stopAllCreatureSounds(player);
            // Restore ambient sounds that were stopped during chase
            // (just stop the silence — MC will naturally resume ambient sounds)
            // Clear all status effects
            player.removeAllEffects();
        }

        // Remove chase tracking and sound suppression
        if (creature.getTargetPlayerUuid() != null) {
            frozenPlayers.remove(creature.getTargetPlayerUuid());
            playerChaseMap.remove(creature.getTargetPlayerUuid());
            soundSuppressedPlayers.remove(creature.getTargetPlayerUuid());
        }

        // Remove creature from world
        Vec3 lastPos = creature.getPosition();
        despawnDisplayEntity(creature);
        creatures.remove(creature.getCreatureId());

        // Handle side effects (echo, torch removal)
        handleSideEffects(creature, lastPos, config, debug);
    }

    // ===== Disappearing Tick =====

    private static void tickDisappearing(CreatureInstance creature, CreatureConfig config, boolean debug) {
        // Creature has already disappeared, just wait for cleanup
        if (creature.getSequenceTicks() >= 5) {
            creatures.remove(creature.getCreatureId());
            if (debug) LOGGER.info("[Creature] Cleaned up disappeared creature {}", creature.getCreatureId().toString().substring(0, 8));
        }
    }

    // ===== Side Effects =====

    private static void handleSideEffects(CreatureInstance creature, Vec3 lastPos, CreatureConfig config, boolean debug) {
        RandomSource rand = RandomSource.create();

        // Echo effect
        if (creature.willEcho() && rand.nextDouble() < config.echoChance) {
            activeEchoZones.add(new EchoZone(
                    lastPos,
                    creature.getWorld(),
                    creature.getAmbienceSoundVariant() >= 0 ? creature.getAmbienceSoundVariant() : rand.nextInt(8),
                    600,  // 30 seconds
                    false
            ));
            if (debug) LOGGER.info("[Creature] Echo zone created at {}", lastPos);
        }

        // Torch removal
        if (creature.willRemoveTorches() && rand.nextDouble() < config.torchRemovalChance) {
            removeTorchesNearby(creature.getWorld(), lastPos, 10, debug);
        }
    }

    private static void removeTorchesNearby(ServerLevel world, Vec3 center, int radius, boolean debug) {
        BlockPos centerPos = BlockPos.containing(center);
        int removed = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = centerPos.offset(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    if (state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)
                            || state.is(Blocks.SOUL_TORCH) || state.is(Blocks.SOUL_WALL_TORCH)) {
                        // Break the torch (drops the item)
                        world.destroyBlock(pos, true, null, 512);
                        removed++;
                    }
                }
            }
        }

        if (debug) LOGGER.info("[Creature] Removed {} torches in {}^3 radius around {}", removed, radius * 2, center);
    }

    // ===== Echo Zone Tick =====

    private static void tickEchoZones(MinecraftServer server, CreatureConfig config, boolean debug) {
        // Collect updated zones in a separate list to avoid ConcurrentModificationException
        List<EchoZone> updatedZones = new ArrayList<>();

        for (EchoZone zone : activeEchoZones) {
            // Decrement timer
            int remaining = zone.ticksRemaining() - 1;

            if (remaining <= 0 || zone.triggered()) {
                if (debug) LOGGER.info("[Creature] Echo zone expired/triggered at {}", zone.position());
                continue; // Drop this zone (don't add to updatedZones)
            }

            // Check if any player is within trigger radius
            boolean triggered = false;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (((net.minecraft.server.level.ServerLevel) player.level()) != zone.world()) continue;
                if (player.isSpectator()) continue;
                if (player.position().distanceTo(zone.position()) <= config.echoTriggerRadius) {
                    // Trigger echo: play ambience sound
                    CreatureSoundHelper.playAmbienceSound(player, zone.ambienceSoundVariant(), zone.position(), 1.0f);
                    triggered = true;
                    if (debug) LOGGER.info("[Creature] Echo triggered by {} at {}", player.getName().getString(), zone.position());
                    break;
                }
            }

            if (!triggered) {
                // Keep zone with decremented timer
                updatedZones.add(new EchoZone(zone.position(), zone.world(), zone.ambienceSoundVariant(), remaining, false));
            }
            // If triggered, drop the zone (don't add to updatedZones)
        }

        activeEchoZones.clear();
        activeEchoZones.addAll(updatedZones);
    }

    // ===== Frozen Players Tick =====

    private static void tickFrozenPlayers(MinecraftServer server) {
        Iterator<UUID> it = frozenPlayers.iterator();
        while (it.hasNext()) {
            UUID playerId = it.next();
            ServerPlayer player = server.getPlayerList().getPlayer(playerId);
            if (player == null) {
                it.remove();
                continue;
            }

            // Keep player velocity at zero
            player.setDeltaMovement(Vec3.ZERO);
        }
    }

    // ===== Chase View Jitter =====

    private static final RandomSource jitterRng = RandomSource.create();

    private static void tickChasePitchInfluence(MinecraftServer server, CreatureConfig config) {
        for (Map.Entry<UUID, UUID> entry : playerChaseMap.entrySet()) {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) continue;

            CreatureInstance creature = creatures.get(entry.getValue());
            if (creature == null || creature.getCurrentSequence() != CreatureInstance.Sequence.CHASING) continue;

            // Apply random jitter to player's view (yaw and pitch) each tick.
            // The player can still freely move and look; the jitter adds small random offsets.
            float jitterYaw = (jitterRng.nextFloat() * 2.0f - 1.0f) * 1.5f;  // ±1.5 degrees
            float jitterPitch = (jitterRng.nextFloat() * 2.0f - 1.0f) * 1.0f; // ±1.0 degrees

            // All values are RELATIVE — small random offsets added on top of current look direction.
            // X/Y/Z position and velocity are also relative at +0 so the player moves freely.
            player.connection.teleport(
                    new PositionMoveRotation(Vec3.ZERO, Vec3.ZERO, jitterYaw, jitterPitch),
                    Set.of(Relative.X, Relative.Y, Relative.Z,
                            Relative.Y_ROT, Relative.X_ROT,
                            Relative.DELTA_X, Relative.DELTA_Y, Relative.DELTA_Z)
            );

            creature.addPitchInfluence(1.0f);
        }
    }

    // ===== Projectile Rejection =====

    private static void tickProjectileRejection(MinecraftServer server, CreatureConfig config, boolean debug) {
        for (CreatureInstance creature : creatures.values()) {
            if (!creature.willRejectProjectiles()) continue;
            if (creature.getCurrentSequence() == CreatureInstance.Sequence.CHASING) continue;

            ServerLevel world = creature.getWorld();
            Vec3 creaturePos = creature.getPosition();

            // Check for projectiles near the creature (within 2 blocks)
            AABB detectionBox = new AABB(creaturePos.subtract(2, 2, 2), creaturePos.add(2, 5, 2));
            List<Projectile> projectiles = world.getEntitiesOfClass(Projectile.class, detectionBox, p -> !p.isRemoved());

            for (Projectile projectile : projectiles) {
                Entity owner = projectile.getOwner();
                if (owner instanceof ServerPlayer playerOwner) {
                    if (debug) LOGGER.info("[Creature] Projectile rejection: {} shot at creature {}", playerOwner.getName().getString(),
                            creature.getCreatureId().toString().substring(0, 8));

                    // Store projectile type info before destroying
                    EntityType<?> projectileType = projectile.getType();
                    Vec3 projectileVelocity = projectile.getDeltaMovement();

                    // Destroy the projectile
                    projectile.discard();

                    // Schedule return fire after delay (configurable ticks)
                    int delay = config.projectileRejectionDelay;
                    // Use a simple tick-based scheduling by storing in a deferred action list
                    scheduleReturnFire(world, creature, playerOwner, projectileType, delay, debug);
                }
            }
        }
    }

    private static final List<DeferredAction> deferredActions = new ArrayList<>();

    private record DeferredAction(int ticksRemaining, Runnable action) {}

    /**
     * Schedules a projectile return fire after a delay.
     */
    private static void scheduleReturnFire(ServerLevel world, CreatureInstance creature,
                                            ServerPlayer target, EntityType<?> projectileType,
                                            int delayTicks, boolean debug) {
        deferredActions.add(new DeferredAction(delayTicks, () -> {
            if (creature.getPosition() == null || target.isRemoved()) return;

            try {
                Vec3 creaturePos = creature.getPosition().add(0, 1.5, 0);
                Vec3 targetPos = target.getEyePosition();
                Vec3 direction = targetPos.subtract(creaturePos).normalize();

                // Create a new projectile of the same type
                Entity newProjectile = projectileType.create(world, EntitySpawnReason.MOB_SUMMONED);
                if (newProjectile instanceof Projectile proj) {
                    proj.setPos(creaturePos);
                    proj.setDeltaMovement(direction.x * 2.0, direction.y * 2.0, direction.z * 2.0);
                    world.addFreshEntity(proj);
                    if (debug) LOGGER.info("[Creature] Return fire projectile spawned at {} towards {}", creaturePos, target.getName().getString());
                }
            } catch (Exception e) {
                LOGGER.warn("[Creature] Failed to spawn return fire projectile: {}", e.getMessage());
            }
        }));
    }

    // ===== Jitter / Shaking =====

    private static void updateJitter(CreatureInstance creature, MinecraftServer server, CreatureConfig config) {
        ServerLevel world = creature.getWorld();
        Vec3 creaturePos = creature.getPosition();
        double maxJitter = config.jitterMax;
        double bestJitter = 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (((net.minecraft.server.level.ServerLevel) player.level()) != world) continue;
            if (player.isSpectator()) continue;

            double distance = player.position().distanceTo(creaturePos);
            if (distance > 64) continue;

            Vec3 playerLook = player.getViewVector(1.0f);
            Vec3 toCreature = creaturePos.add(0, 1.5, 0).subtract(player.getEyePosition()).normalize();
            double dotProduct = playerLook.dot(toCreature);

            if (dotProduct <= 0) continue;

            boolean directLook = isPlayerLookingAtCreature(player, creaturePos, world);

            double jitter;
            if (directLook) {
                jitter = maxJitter;
            } else if (dotProduct > 0.3) {
                jitter = maxJitter * 0.3 * ((dotProduct - 0.3) / 0.7);
            } else {
                continue;
            }

            bestJitter = Math.max(bestJitter, jitter);
        }

        float newIntensity = (float) bestJitter;
        if (newIntensity != creature.getCurrentJitterIntensity()) {
            creature.setCurrentJitterIntensity(newIntensity);
            syncJitterIntensity(creature);
        }
    }

    /** Pushes the current jitter intensity to the client-side CreatureEntity via SynchedEntityData. */
    private static void syncJitterIntensity(CreatureInstance creature) {
        if (creature.getEntityUuid() == null) return;
        Entity entity = creature.getWorld().getEntity(creature.getEntityUuid());
        if (entity instanceof CreatureEntity ce) {
            ce.setJitterIntensity(creature.getCurrentJitterIntensity());
        }
    }

    // ===== Copper Nugget Trail =====

    private static void spawnCopperTrail(CreatureInstance creature, ServerPlayer player, CreatureConfig config, boolean debug) {
        if (debug) {
            LOGGER.info("[Creature] Spawning copper nugget trail for creature {}", creature.getCreatureId().toString().substring(0, 8));
            debugOps(creature.getWorld().getServer(), String.format("Copper trail: spawning for creature %s near %s",
                    creature.getCreatureId().toString().substring(0, 8), player.getName().getString()));
        }

        ServerLevel world = creature.getWorld();
        Vec3 playerPos = player.position();
        Vec3 creaturePos = creature.getPosition();

        // Build path from player to creature
        List<BlockPos> path = CreaturePathfinder.findChasePath(
                world,
                BlockPos.containing(playerPos),
                BlockPos.containing(creaturePos),
                config.pathfindingMaxY
        );

        if (path.isEmpty()) {
            if (debug) LOGGER.info("[Creature] Could not build path for copper trail");
            return;
        }

        // Calculate total path length
        double totalLength = 0;
        for (int i = 1; i < path.size(); i++) {
            totalLength += path.get(i).getCenter().distanceTo(path.get(i - 1).getCenter());
        }

        // Place nuggets along the path up to trail_reach percentage
        double reachDistance = totalLength * config.trailReach;
        double traveled = 0;
        double lastNuggetDist = 0;
        int nuggetsPlaced = 0;

        for (int i = 1; i < path.size(); i++) {
            double segmentLength = path.get(i).getCenter().distanceTo(path.get(i - 1).getCenter());
            traveled += segmentLength;

            if (traveled > reachDistance) break;

            if (traveled - lastNuggetDist >= config.trailSeparation) {
                Vec3 nuggetPos = path.get(i).getCenter();
                ItemEntity nugget = new ItemEntity(world, nuggetPos.x, nuggetPos.y, nuggetPos.z, new ItemStack(Items.COPPER_NUGGET));
                nugget.setNoPickUpDelay();
                nugget.setDeltaMovement(Vec3.ZERO);
                world.addFreshEntity(nugget);
                nuggetsPlaced++;
                lastNuggetDist = traveled;
            }
        }

        creature.setCopperTrailSpawned(true);
        creature.setCurrentSequence(CreatureInstance.Sequence.COPPER_TRAIL);
        if (debug) {
            LOGGER.info("[Creature] Placed {} copper nuggets along trail ({}% of {} blocks)", nuggetsPlaced, (int)(config.trailReach * 100), (int)totalLength);
            debugOps(creature.getWorld().getServer(), String.format("Copper trail: placed %d nuggets (%.0f%% of %.0f blocks)",
                    nuggetsPlaced, config.trailReach * 100, totalLength));
        }
    }

    // ===== CreatureEntity Management =====

    private static void spawnDisplayEntity(CreatureInstance creature) {
        ServerLevel world = creature.getWorld();

        CreatureEntity entity = ModEntities.CREATURE.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (entity == null) {
            LOGGER.error("[Creature] Failed to create CreatureEntity for creature {}", creature.getCreatureId());
            return;
        }

        Vec3 pos = creature.getPosition();
        entity.setPos(pos.x, pos.y, pos.z);
        entity.setTextureVariant(creature.getTextureVariant());
        entity.setGlowingTag(DeeperDarkConfig.get().creature.enableDebugGlow);

        world.addFreshEntity(entity);

        creature.setEntityId(entity.getId());
        creature.setEntityUuid(entity.getUUID());
    }

    private static void despawnDisplayEntity(CreatureInstance creature) {
        if (creature.getEntityUuid() == null) return;

        Entity entity = creature.getWorld().getEntity(creature.getEntityUuid());
        if (entity != null) entity.discard();

        creature.setEntityId(-1);
        creature.setEntityUuid(null);
    }

    /**
     * Pre-rolls chance-based behavior flags for a creature instance.
     */
    private static void rollBehaviorFlags(CreatureInstance creature, CreatureConfig config, net.minecraft.util.RandomSource random) {
        creature.setWillChase(random.nextDouble() < config.chaseFrequency);
        creature.setWillRemoveTorches(random.nextDouble() < config.torchRemovalChance);
        creature.setWillEcho(random.nextDouble() < config.echoChance);
        creature.setWillRejectProjectiles(random.nextDouble() < config.projectileRejectionChance);
    }

    /** Moves the CreatureEntity to the creature's current logical position. */
    private static void updateDisplayEntityPosition(CreatureInstance creature) {
        if (creature.getEntityUuid() == null) return;

        Entity entity = creature.getWorld().getEntity(creature.getEntityUuid());
        if (entity instanceof CreatureEntity ce) {
            Vec3 pos = creature.getPosition();
            ce.setPos(pos.x, pos.y, pos.z);
        }
    }

    /**
     * Billboarding is handled entirely client-side by the renderer using the camera yaw,
     * so only the internal yaw on CreatureInstance is updated here (used for chase direction
     * calculations, not for rendering).
     */
    private static void updateCreatureFacing(CreatureInstance creature, MinecraftServer server) {
        ServerLevel world = creature.getWorld();
        Vec3 creaturePos = creature.getPosition();

        ServerPlayer nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (((net.minecraft.server.level.ServerLevel) player.level()) != world) continue;
            if (player.isSpectator()) continue;
            double dist = player.position().distanceTo(creaturePos);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = player;
            }
        }

        if (nearest != null) {
            Vec3 direction = nearest.position().subtract(creaturePos);
            creature.setYaw((float) (Math.atan2(-direction.x, direction.z) * (180.0 / Math.PI)));
        }
    }

    // ===== Visibility Helpers =====

    /**
     * Checks if a player is directly looking at the creature (crosshair on the entity).
     * Uses raycasting to check for line-of-sight and crosshair proximity.
     */
    private static boolean isPlayerLookingAtCreature(ServerPlayer player, Vec3 creaturePos, ServerLevel world) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0f);

        // Raycast from player eye towards where they're looking (max 128 blocks)
        Vec3 rayEnd = eyePos.add(lookVec.scale(128));

        // First check if the ray roughly points at the creature
        Vec3 toCreature = creaturePos.add(0, 1.5, 0).subtract(eyePos);
        double distance = toCreature.length();
        if (distance > 128) return false;

        Vec3 toCreatureNorm = toCreature.normalize();
        double dot = lookVec.dot(toCreatureNorm);

        // Crosshair must be within ~3 degrees of the creature center
        if (dot < 0.998) return false;

        // Check for block obstruction
        net.minecraft.world.phys.BlockHitResult blockHit = world.clip(new ClipContext(
                eyePos,
                creaturePos.add(0, 1.5, 0),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        if (blockHit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            double blockDist = eyePos.distanceTo(blockHit.getLocation());
            if (blockDist < distance - 0.5) {
                return false; // View obstructed by a block
            }
        }

        return true;
    }

    /**
     * Checks if a creature is visible on a player's screen (within ~55 degree cone and not obscured by blocks),
     * but NOT necessarily directly looked at. Used for on-screen tolerance timing on non-chase creatures.
     */
    private static boolean isCreatureOnPlayerScreen(ServerPlayer player, Vec3 creaturePos, ServerLevel world) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 toCreature = creaturePos.add(0, 1.5, 0).subtract(eyePos);
        double distance = toCreature.length();
        if (distance > 128 || distance < 1) return false;

        Vec3 lookVec = player.getViewVector(1.0f);
        Vec3 toCreatureNorm = toCreature.normalize();
        double dot = lookVec.dot(toCreatureNorm);

        // On screen ≈ within ~55° of look direction (dot > 0.55)
        if (dot <= 0.55) return false;

        // Check for block obstruction
        net.minecraft.world.phys.BlockHitResult blockHit = world.clip(new ClipContext(
                eyePos,
                creaturePos.add(0, 1.5, 0),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        if (blockHit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            double blockDist = eyePos.distanceTo(blockHit.getLocation());
            if (blockDist < distance - 0.5) {
                return false; // View obstructed
            }
        }

        return true;
    }

    /**
     * Checks if any player is looking towards the horizontal direction of the path.
     * Uses horizontal-only projection since the copper trail spawns at ground level,
     * not along the 3D line to the underground creature.
     */
    private static boolean isAnyPlayerLookingAtPath(MinecraftServer server, ServerLevel world, Vec3 from, Vec3 to) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (((net.minecraft.server.level.ServerLevel) player.level()) != world) continue;
            if (player.isSpectator()) continue;

            Vec3 eyePos = player.getEyePosition();
            Vec3 lookVec = player.getViewVector(1.0f);

            // Project look direction onto horizontal plane
            Vec3 lookHorizontal = new Vec3(lookVec.x, 0, lookVec.z);
            if (lookHorizontal.lengthSqr() < 0.001) continue; // Player looking straight up/down
            lookHorizontal = lookHorizontal.normalize();

            // Sample 5 points along the path, projected horizontally
            // Skip t=0 since the player is AT the from-point
            for (double t = 0.1; t <= 1.0; t += 0.225) {
                Vec3 samplePoint = from.lerp(to, t);
                // Project to horizontal: direction from player eye to sample, ignoring Y
                Vec3 toSampleHorizontal = new Vec3(samplePoint.x - eyePos.x, 0, samplePoint.z - eyePos.z);
                double horizDist = toSampleHorizontal.length();
                if (horizDist < 3) continue;  // Too close horizontally, skip
                if (horizDist > 80) continue;  // Too far to notice nuggets appearing

                toSampleHorizontal = toSampleHorizontal.normalize();
                double dot = lookHorizontal.dot(toSampleHorizontal);

                // dot > 0.55 ≈ within ~56 degree horizontal cone of view
                if (dot > 0.55) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ServerPlayer getTargetPlayer(CreatureInstance creature, MinecraftServer server) {
        if (creature.getTargetPlayerUuid() == null) return null;
        return server.getPlayerList().getPlayer(creature.getTargetPlayerUuid());
    }

    /**
     * Teleports a player to the world spawn as a fallback.
     */
    private static void teleportToWorldSpawn(ServerPlayer player, MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        net.minecraft.world.level.storage.LevelData.RespawnData respawnData = overworld.getRespawnData();
        BlockPos worldSpawn = (respawnData != null) ? respawnData.pos() : new BlockPos(0, 64, 0);
        Vec3 spawnPos = new Vec3(worldSpawn.getX() + 0.5, worldSpawn.getY(), worldSpawn.getZ() + 0.5);
        player.teleport(new net.minecraft.world.level.portal.TeleportTransition(
                overworld, spawnPos, Vec3.ZERO, player.getYRot(), player.getXRot(),
                net.minecraft.world.level.portal.TeleportTransition.DO_NOTHING
        ));
    }

    /**
     * Snaps a position to the ground (finds solid block below and stands on it).
     */
    private static Vec3 snapToGround(ServerLevel world, BlockPos blockPos, Vec3 originalPos) {
        // Search downward for solid ground
        for (int dy = 0; dy >= -5; dy--) {
            BlockPos checkPos = blockPos.offset(0, dy, 0);
            BlockPos belowPos = checkPos.below();
            if (world.getBlockState(checkPos).isAir() && world.getBlockState(belowPos).isSolid()) {
                return new Vec3(originalPos.x, checkPos.getY(), originalPos.z);
            }
        }
        // Search upward
        for (int dy = 1; dy <= 5; dy++) {
            BlockPos checkPos = blockPos.offset(0, dy, 0);
            BlockPos belowPos = checkPos.below();
            if (world.getBlockState(checkPos).isAir() && world.getBlockState(belowPos).isSolid()) {
                return new Vec3(originalPos.x, checkPos.getY(), originalPos.z);
            }
        }
        return originalPos;
    }

    // ===== Deferred Action Processing =====

    /**
     * Sends END_ROD particles along a pathfinding result to visualize the algorithm's path.
     * Particles are re-sent every second for the configured duration so they remain visible.
     */
    private static void sendDebugPathParticles(ServerLevel world, List<BlockPos> path, ServerPlayer triggeringPlayer) {
        if (path.isEmpty()) return;

        CreatureConfig config = DeeperDarkConfig.get().creature;
        int durationSeconds = Math.max(1, config.debugPathDuration);

        // Send particles immediately
        sendDebugPathParticlesBurst(world, path, triggeringPlayer);

        // Schedule re-sends every 20 ticks (1 second) for the remaining duration
        for (int sec = 1; sec < durationSeconds; sec++) {
            int delayTicks = sec * 20;
            // Capture the path and world reference for the deferred action
            final List<BlockPos> pathCopy = List.copyOf(path);
            deferredActions.add(new DeferredAction(delayTicks, () -> {
                // Re-check that the player is still online
                ServerPlayer player = world.getServer().getPlayerList().getPlayer(triggeringPlayer.getUUID());
                if (player != null) {
                    sendDebugPathParticlesBurst(world, pathCopy, player);
                }
            }));
        }

        LOGGER.info("[Creature] Debug path: {} particles, repeating for {} seconds to {} player(s)",
                path.size(), durationSeconds, 1);
    }

    /**
     * Sends a single burst of debug path particles.
     */
    private static void sendDebugPathParticlesBurst(ServerLevel world, List<BlockPos> path, ServerPlayer triggeringPlayer) {
        // Collect target players (triggering player + nearby players)
        List<ServerPlayer> targets = new ArrayList<>();
        targets.add(triggeringPlayer);
        for (ServerPlayer other : world.players()) {
            if (other == triggeringPlayer) continue;
            if (other.distanceToSqr(triggeringPlayer) < 128 * 128) {
                targets.add(other);
            }
        }

        // Send particles along the path
        for (int i = 0; i < path.size(); i++) {
            Vec3 pos = Vec3.atBottomCenterOf(path.get(i)).add(0, 0.5, 0);

            ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
                    ParticleTypes.END_ROD, true, false,
                    pos.x, pos.y, pos.z,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1
            );

            for (ServerPlayer target : targets) {
                target.connection.send(packet);
            }
        }

        // Mark the final position (creature spawn) with a cluster of FLAME particles
        if (!path.isEmpty()) {
            Vec3 finalPos = Vec3.atBottomCenterOf(path.getLast()).add(0, 1.0, 0);
            ClientboundLevelParticlesPacket markerPacket = new ClientboundLevelParticlesPacket(
                    ParticleTypes.FLAME, true, false,
                    finalPos.x, finalPos.y, finalPos.z,
                    0.3f, 0.3f, 0.3f,
                    0.02f, 20
            );
            for (ServerPlayer target : targets) {
                target.connection.send(markerPacket);
            }
        }
    }

    static {
        // Process deferred actions each tick
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<DeferredAction> it = deferredActions.iterator();
            List<DeferredAction> updated = new ArrayList<>();
            List<Runnable> toExecute = new ArrayList<>();

            while (it.hasNext()) {
                DeferredAction action = it.next();
                it.remove();
                if (action.ticksRemaining() <= 1) {
                    toExecute.add(action.action());
                } else {
                    updated.add(new DeferredAction(action.ticksRemaining() - 1, action.action()));
                }
            }

            deferredActions.addAll(updated);
            toExecute.forEach(Runnable::run);
        });
    }
}
