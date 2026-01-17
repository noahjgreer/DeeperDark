package net.noahsarch.deeperdark.state;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import org.slf4j.Logger;

import java.util.*;

public class ActiveBeaconState extends PersistentState {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<BeaconInfo> BEACON_INFO_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(i -> i.pos),
            Codec.LONG.fieldOf("time").forGetter(i -> i.remainingTime),
            Uuids.INT_STREAM_CODEC.listOf().fieldOf("players").forGetter(i -> new ArrayList<>(i.trackedPlayers)),
            StatusEffect.ENTRY_CODEC.optionalFieldOf("primary").forGetter(i -> Optional.ofNullable(i.primary)),
            StatusEffect.ENTRY_CODEC.optionalFieldOf("secondary").forGetter(i -> Optional.ofNullable(i.secondary)),
            Codec.INT.fieldOf("level").forGetter(i -> i.level)
    ).apply(instance, BeaconInfo::new));

    public static final Codec<ActiveBeaconState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, BEACON_INFO_CODEC).xmap(
                map -> {
                    Map<Long, BeaconInfo> newMap = new HashMap<>();
                    // Rebuild map using the position inside BeaconInfo to ensure consistency
                    // regardless of the map key stored on disk
                    map.values().forEach(info -> {
                        if (info != null && info.pos != null) {
                            newMap.put(info.pos.asLong(), info);
                        }
                    });
                    return newMap;
                },
                map -> {
                    Map<String, BeaconInfo> newMap = new HashMap<>();
                    map.forEach((k, v) -> newMap.put(String.valueOf(k), v));
                    return newMap;
                }
            ).fieldOf("beacons").forGetter(state -> state.activeBeacons),
            Codec.unboundedMap(Codec.STRING, StatusEffect.ENTRY_CODEC.listOf()).optionalFieldOf("pending_removals", new HashMap<>()).xmap(
                map -> {
                    Map<UUID, List<RegistryEntry<StatusEffect>>> newMap = new HashMap<>();
                    map.forEach((k, v) -> {
                        try {
                            newMap.put(UUID.fromString(k), v);
                        } catch (IllegalArgumentException e) {
                            LOGGER.warn("Skipping invalid UUID in pending_removals: {}", k);
                        }
                    });
                    return newMap;
                },
                map -> {
                    Map<String, List<RegistryEntry<StatusEffect>>> newMap = new HashMap<>();
                    map.forEach((k, v) -> newMap.put(k.toString(), v));
                    return newMap;
                }
            ).forGetter(state -> state.pendingRemovals)
    ).apply(instance, ActiveBeaconState::new));

    public static final PersistentStateType<ActiveBeaconState> TYPE = new PersistentStateType<>(
            "deeperdark_active_beacons",
            ActiveBeaconState::new,
            CODEC,
            DataFixTypes.LEVEL
    );

    // Map of BlockPos (as Long) to BeaconInfo
    private final Map<Long, BeaconInfo> activeBeacons;
    // Map of UUID to list of effects that need to be removed (for players who were offline when beacon broke)
    private final Map<UUID, List<RegistryEntry<StatusEffect>>> pendingRemovals;

    public ActiveBeaconState() {
        this.activeBeacons = new HashMap<>();
        this.pendingRemovals = new HashMap<>();
        LOGGER.info("ActiveBeaconState constructed (empty)");
    }

    public ActiveBeaconState(Map<Long, BeaconInfo> activeBeacons, Map<UUID, List<RegistryEntry<StatusEffect>>> pendingRemovals) {
        this.activeBeacons = new HashMap<>(activeBeacons);
        this.pendingRemovals = new HashMap<>(pendingRemovals);
        LOGGER.info("ActiveBeaconState constructed (from disk): {} beacons, {} pending removals", activeBeacons.size(), pendingRemovals.size());
    }

    public static ActiveBeaconState get(ServerWorld world) {
        // Use the correct signature for your Minecraft version
        return world.getPersistentStateManager().getOrCreate(TYPE);
    }

    public void updateBeacon(BlockPos pos, long remainingTime, Set<UUID> trackedPlayers, RegistryEntry<StatusEffect> primary, RegistryEntry<StatusEffect> secondary, int level, ServerWorld world) {
        LOGGER.info("updateBeacon: pos={}, time={}, tracked={}, primary={}, secondary={}, level={}", pos, remainingTime, trackedPlayers.size(), primary, secondary, level);
        if (remainingTime <= 0) {
            removeBeacon(pos, world);
            return;
        }

        ActiveBeaconState.BeaconInfo info = activeBeacons.get(pos.asLong());
        if (info == null) {
            info = new BeaconInfo(pos, remainingTime, new ArrayList<>(trackedPlayers), Optional.ofNullable(primary), Optional.ofNullable(secondary), level);
            activeBeacons.put(pos.asLong(), info);
        } else {
            // Update existing beacon info, but make sure to update primary/secondary if they changed
            // This handles "putting another effect on the same beacon"
            // If the primary or secondary effect is different, we should probably reset/overwrite?
            // The user implies that changing effects resets everything.
            // This method is called by beacon block entity.
            // In resetBeacon(), we remove the beacon entirely.
            // So if `resetBeacon` was called, `info` would be null here initially, creating a new one.

            // However, `updateBeacon` is also called periodically in `tick`.
            // If the beacon block entity changes effects, `tick` will call this.

            // Let's just trust the passed values.
            info.level = level;
            info.primary = primary;
            info.secondary = secondary;
            info.remainingTime = remainingTime;
            info.trackedPlayers = new HashSet<>(trackedPlayers);
        }
        this.markDirty();
        LOGGER.info("ActiveBeaconState marked dirty after updateBeacon");
    }

    public BeaconInfo getBeacon(BlockPos pos) {
        return activeBeacons.get(pos.asLong());
    }

    public void removeBeacon(BlockPos pos, ServerWorld world) {
        LOGGER.info("removeBeacon: pos={}", pos);
        BeaconInfo removed = activeBeacons.remove(pos.asLong());
        if (removed != null) {
            this.markDirty();
            LOGGER.info("ActiveBeaconState marked dirty after removeBeacon");
            // Clear effects from associated players immediately
            MinecraftServer server = world.getServer();
            if (server != null) {
                for (UUID uuid : removed.trackedPlayers) {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                    if (player != null) {
                         if (removed.primary != null) player.removeStatusEffect(removed.primary);
                         if (removed.secondary != null) player.removeStatusEffect(removed.secondary);
                    } else {
                        // Player is offline, add to pending removals
                        List<RegistryEntry<StatusEffect>> effects = new ArrayList<>();
                        if (removed.primary != null) effects.add(removed.primary);
                        if (removed.secondary != null) effects.add(removed.secondary);
                        if (!effects.isEmpty()) {
                            pendingRemovals.computeIfAbsent(uuid, k -> new ArrayList<>()).addAll(effects);
                        }
                    }
                }
            }
        }
    }

    public void tick(ServerWorld world) {
        Iterator<Map.Entry<Long, BeaconInfo>> iterator = activeBeacons.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, BeaconInfo> entry = iterator.next();
            BeaconInfo info = entry.getValue();

            if (info.remainingTime > 0) {
                info.remainingTime--;

                // Only mark dirty occasionally to save disk writes
                if (info.remainingTime % 100 == 0) {
                    this.markDirty();
                }

                if (world.getTime() % 80 == 0) { // Apply effects every 4 seconds
                    applyEffects(world, info);
                }
            } else {
                iterator.remove();
                this.markDirty();
            }
        }
    }

    private void applyEffects(ServerWorld world, BeaconInfo info) {
        MinecraftServer server = world.getServer();
        if (server == null) return;

        if (info.level <= 0 || info.primary == null) {
             // If beacon became invalid (level 0) or has no primary, strip effects immediately
             // so they don't linger for the full duration (which could be hours).
             for (UUID uuid : info.trackedPlayers) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                if (player != null) {
                     if (info.primary != null) player.removeStatusEffect(info.primary);
                     if (info.secondary != null) player.removeStatusEffect(info.secondary);
                }
                // If offline, we can't easily strip "invalid" beacon effects without tracking which beacon gave what.
                // But typically invalidation happens on tick, which presumes server logic.
                // We'll leave pending removal for full removal only for now.
             }
             return;
        }

        for (UUID uuid : info.trackedPlayers) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null && !player.isSpectator()) {
                 int duration = (int) info.remainingTime;
                 // Ensure duration is safe for client display (some clients glitch with excessively large numbers, though 24h is fine)
                 // Also ensure non-zero
                 if (duration < 20) duration = 20;

                 int amplifier = 0;
                 if (info.level >= 4 && Objects.equals(info.primary, info.secondary)) {
                     amplifier = 1;
                 }

                 player.addStatusEffect(new StatusEffectInstance(info.primary, duration, amplifier, true, true));

                 if (info.level >= 4 && !Objects.equals(info.primary, info.secondary) && info.secondary != null) {
                     player.addStatusEffect(new StatusEffectInstance(info.secondary, duration, 0, true, true));
                 }
            }
        }
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();

        // Process pending removals
        if (pendingRemovals.containsKey(uuid)) {
            List<RegistryEntry<StatusEffect>> effects = pendingRemovals.remove(uuid);
            if (effects != null) {
                LOGGER.info("Processing pending removals for player {}", player.getName().getString());
                for (RegistryEntry<StatusEffect> effect : effects) {
                    player.removeStatusEffect(effect);
                }
                this.markDirty();
            }
        }

        // Only reapply effects if the player is already tracked by a beacon
        for (BeaconInfo info : activeBeacons.values()) {
            if (info.trackedPlayers.contains(uuid)) {
                LOGGER.info("Found tracking for player {} in beacon at {}", player.getName().getString(), info.pos);
                if (info.level > 0 && info.primary != null) {
                    int duration = (int) info.remainingTime;
                    if (duration < 20) duration = 20;
                    int amplifier = 0;
                    if (info.level >= 4 && Objects.equals(info.primary, info.secondary)) {
                        amplifier = 1;
                    }
                    player.addStatusEffect(new StatusEffectInstance(info.primary, duration, amplifier, true, true));
                    if (info.level >= 4 && !Objects.equals(info.primary, info.secondary) && info.secondary != null) {
                        player.addStatusEffect(new StatusEffectInstance(info.secondary, duration, 0, true, true));
                    }
                }
            }
        }
    }

    public Collection<BeaconInfo> getActiveBeacons() {
        return activeBeacons.values();
    }

    public static class BeaconInfo {
        public BlockPos pos;
        public long remainingTime;
        public Set<UUID> trackedPlayers = new HashSet<>();
        public RegistryEntry<StatusEffect> primary;
        public RegistryEntry<StatusEffect> secondary;
        public int level;

        public BeaconInfo(BlockPos pos, long remainingTime, List<UUID> players, Optional<RegistryEntry<StatusEffect>> primary, Optional<RegistryEntry<StatusEffect>> secondary, int level) {
             this.pos = pos;
             this.remainingTime = remainingTime;
             this.trackedPlayers.addAll(players);
             this.primary = primary.orElse(null);
             this.secondary = secondary.orElse(null);
             this.level = level;
        }

        public BeaconInfo(BlockPos pos) {
            this.pos = pos;
        }
    }
}

























