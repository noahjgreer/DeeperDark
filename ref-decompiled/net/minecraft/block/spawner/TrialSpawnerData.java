/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.block.enums.TrialSpawnerState
 *  net.minecraft.block.spawner.MobSpawnerEntry
 *  net.minecraft.block.spawner.TrialSpawnerConfig
 *  net.minecraft.block.spawner.TrialSpawnerData
 *  net.minecraft.block.spawner.TrialSpawnerData$Packed
 *  net.minecraft.block.spawner.TrialSpawnerLogic
 *  net.minecraft.block.spawner.TrialSpawnerLogic$Type
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LoadedEntityProcessor
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.loot.LootTable
 *  net.minecraft.loot.context.LootContextTypes
 *  net.minecraft.loot.context.LootWorldContext
 *  net.minecraft.loot.context.LootWorldContext$Builder
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.Util
 *  net.minecraft.util.collection.Pool
 *  net.minecraft.util.collection.Pool$Builder
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.spawner;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LoadedEntityProcessor;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class TrialSpawnerData {
    private static final String SPAWN_DATA_KEY = "spawn_data";
    private static final String NEXT_MOB_SPAWNS_AT_KEY = "next_mob_spawns_at";
    private static final int field_50190 = 20;
    private static final int field_50191 = 18000;
    final Set<UUID> players = new HashSet();
    final Set<UUID> spawnedMobsAlive = new HashSet();
    long cooldownEnd;
    long nextMobSpawnsAt;
    int totalSpawnedMobs;
    Optional<MobSpawnerEntry> spawnData = Optional.empty();
    Optional<RegistryKey<LootTable>> rewardLootTable = Optional.empty();
    private @Nullable Entity displayEntity;
    private @Nullable Pool<ItemStack> itemsToDropWhenOminous;
    double displayEntityRotation;
    double lastDisplayEntityRotation;

    public Packed pack() {
        return new Packed(Set.copyOf(this.players), Set.copyOf(this.spawnedMobsAlive), this.cooldownEnd, this.nextMobSpawnsAt, this.totalSpawnedMobs, this.spawnData, this.rewardLootTable);
    }

    public void unpack(Packed packed) {
        this.players.clear();
        this.players.addAll(packed.detectedPlayers);
        this.spawnedMobsAlive.clear();
        this.spawnedMobsAlive.addAll(packed.currentMobs);
        this.cooldownEnd = packed.cooldownEndsAt;
        this.nextMobSpawnsAt = packed.nextMobSpawnsAt;
        this.totalSpawnedMobs = packed.totalMobsSpawned;
        this.spawnData = packed.nextSpawnData;
        this.rewardLootTable = packed.ejectingLootTable;
    }

    public void reset() {
        this.spawnedMobsAlive.clear();
        this.spawnData = Optional.empty();
        this.deactivate();
    }

    public void deactivate() {
        this.players.clear();
        this.totalSpawnedMobs = 0;
        this.nextMobSpawnsAt = 0L;
        this.cooldownEnd = 0L;
    }

    public boolean hasSpawnData(TrialSpawnerLogic logic, Random random) {
        boolean bl = this.getSpawnData(logic, random).getNbt().getString("id").isPresent();
        return bl || !logic.getConfig().spawnPotentials().isEmpty();
    }

    public boolean hasSpawnedAllMobs(TrialSpawnerConfig config, int additionalPlayers) {
        return this.totalSpawnedMobs >= config.getTotalMobs(additionalPlayers);
    }

    public boolean areMobsDead() {
        return this.spawnedMobsAlive.isEmpty();
    }

    public boolean canSpawnMore(ServerWorld world, TrialSpawnerConfig config, int additionalPlayers) {
        return world.getTime() >= this.nextMobSpawnsAt && this.spawnedMobsAlive.size() < config.getSimultaneousMobs(additionalPlayers);
    }

    public int getAdditionalPlayers(BlockPos pos) {
        if (this.players.isEmpty()) {
            Util.logErrorOrPause((String)("Trial Spawner at " + String.valueOf(pos) + " has no detected players"));
        }
        return Math.max(0, this.players.size() - 1);
    }

    public void updatePlayers(ServerWorld world, BlockPos pos, TrialSpawnerLogic logic) {
        List list2;
        boolean bl2;
        boolean bl;
        boolean bl3 = bl = (pos.asLong() + world.getTime()) % 20L != 0L;
        if (bl) {
            return;
        }
        if (logic.getSpawnerState().equals((Object)TrialSpawnerState.COOLDOWN) && logic.isOminous()) {
            return;
        }
        List list = logic.getEntityDetector().detect(world, logic.getEntitySelector(), pos, (double)logic.getDetectionRadius(), true);
        if (logic.isOminous() || list.isEmpty()) {
            bl2 = false;
        } else {
            Optional optional = TrialSpawnerData.findPlayerWithOmen((ServerWorld)world, (List)list);
            optional.ifPresent(pair -> {
                PlayerEntity playerEntity = (PlayerEntity)pair.getFirst();
                if (pair.getSecond() == StatusEffects.BAD_OMEN) {
                    TrialSpawnerData.applyTrialOmen((PlayerEntity)playerEntity);
                }
                world.syncWorldEvent(3020, BlockPos.ofFloored((Position)playerEntity.getEyePos()), 0);
                logic.setOminous(world, pos);
            });
            bl2 = optional.isPresent();
        }
        if (logic.getSpawnerState().equals((Object)TrialSpawnerState.COOLDOWN) && !bl2) {
            return;
        }
        boolean bl32 = logic.getData().players.isEmpty();
        List list3 = list2 = bl32 ? list : logic.getEntityDetector().detect(world, logic.getEntitySelector(), pos, (double)logic.getDetectionRadius(), false);
        if (this.players.addAll(list2)) {
            this.nextMobSpawnsAt = Math.max(world.getTime() + 40L, this.nextMobSpawnsAt);
            if (!bl2) {
                int i = logic.isOminous() ? 3019 : 3013;
                world.syncWorldEvent(i, pos, this.players.size());
            }
        }
    }

    private static Optional<Pair<PlayerEntity, RegistryEntry<StatusEffect>>> findPlayerWithOmen(ServerWorld world, List<UUID> players) {
        PlayerEntity playerEntity = null;
        for (UUID uUID : players) {
            PlayerEntity playerEntity2 = world.getPlayerByUuid(uUID);
            if (playerEntity2 == null) continue;
            RegistryEntry registryEntry = StatusEffects.TRIAL_OMEN;
            if (playerEntity2.hasStatusEffect(registryEntry)) {
                return Optional.of(Pair.of((Object)playerEntity2, (Object)registryEntry));
            }
            if (!playerEntity2.hasStatusEffect(StatusEffects.BAD_OMEN)) continue;
            playerEntity = playerEntity2;
        }
        return Optional.ofNullable(playerEntity).map(player -> Pair.of((Object)player, (Object)StatusEffects.BAD_OMEN));
    }

    public void resetAndClearMobs(TrialSpawnerLogic logic, ServerWorld world) {
        this.spawnedMobsAlive.stream().map(arg_0 -> ((ServerWorld)world).getEntity(arg_0)).forEach(entity -> {
            if (entity == null) {
                return;
            }
            world.syncWorldEvent(3012, entity.getBlockPos(), TrialSpawnerLogic.Type.NORMAL.getIndex());
            if (entity instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity)entity;
                mobEntity.dropAllForeignEquipment(world);
            }
            entity.remove(Entity.RemovalReason.DISCARDED);
        });
        if (!logic.getOminousConfig().spawnPotentials().isEmpty()) {
            this.spawnData = Optional.empty();
        }
        this.totalSpawnedMobs = 0;
        this.spawnedMobsAlive.clear();
        this.nextMobSpawnsAt = world.getTime() + (long)logic.getOminousConfig().ticksBetweenSpawn();
        logic.updateListeners();
        this.cooldownEnd = world.getTime() + logic.getOminousConfig().getCooldownLength();
    }

    private static void applyTrialOmen(PlayerEntity player) {
        StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.BAD_OMEN);
        if (statusEffectInstance == null) {
            return;
        }
        int i = statusEffectInstance.getAmplifier() + 1;
        int j = 18000 * i;
        player.removeStatusEffect(StatusEffects.BAD_OMEN);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.TRIAL_OMEN, j, 0));
    }

    public boolean isCooldownPast(ServerWorld world, float f, int i) {
        long l = this.cooldownEnd - (long)i;
        return (float)world.getTime() >= (float)l + f;
    }

    public boolean isCooldownAtRepeating(ServerWorld world, float f, int i) {
        long l = this.cooldownEnd - (long)i;
        return (float)(world.getTime() - l) % f == 0.0f;
    }

    public boolean isCooldownOver(ServerWorld world) {
        return world.getTime() >= this.cooldownEnd;
    }

    protected MobSpawnerEntry getSpawnData(TrialSpawnerLogic logic, Random random) {
        if (this.spawnData.isPresent()) {
            return (MobSpawnerEntry)this.spawnData.get();
        }
        Pool pool = logic.getConfig().spawnPotentials();
        Optional optional = pool.isEmpty() ? this.spawnData : pool.getOrEmpty(random);
        this.spawnData = Optional.of(optional.orElseGet(MobSpawnerEntry::new));
        logic.updateListeners();
        return (MobSpawnerEntry)this.spawnData.get();
    }

    public @Nullable Entity setDisplayEntity(TrialSpawnerLogic logic, World world, TrialSpawnerState state) {
        NbtCompound nbtCompound;
        if (!state.doesDisplayRotate()) {
            return null;
        }
        if (this.displayEntity == null && (nbtCompound = this.getSpawnData(logic, world.getRandom()).getNbt()).getString("id").isPresent()) {
            this.displayEntity = EntityType.loadEntityWithPassengers((NbtCompound)nbtCompound, (World)world, (SpawnReason)SpawnReason.TRIAL_SPAWNER, (LoadedEntityProcessor)LoadedEntityProcessor.NOOP);
        }
        return this.displayEntity;
    }

    public NbtCompound getSpawnDataNbt(TrialSpawnerState state) {
        NbtCompound nbtCompound = new NbtCompound();
        if (state == TrialSpawnerState.ACTIVE) {
            nbtCompound.putLong("next_mob_spawns_at", this.nextMobSpawnsAt);
        }
        this.spawnData.ifPresent(spawnData -> nbtCompound.put("spawn_data", MobSpawnerEntry.CODEC, spawnData));
        return nbtCompound;
    }

    public double getDisplayEntityRotation() {
        return this.displayEntityRotation;
    }

    public double getLastDisplayEntityRotation() {
        return this.lastDisplayEntityRotation;
    }

    Pool<ItemStack> getItemsToDropWhenOminous(ServerWorld world, TrialSpawnerConfig config, BlockPos pos) {
        long l;
        LootWorldContext lootWorldContext;
        if (this.itemsToDropWhenOminous != null) {
            return this.itemsToDropWhenOminous;
        }
        LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(config.itemsToDropWhenOminous());
        ObjectArrayList objectArrayList = lootTable.generateLoot(lootWorldContext = new LootWorldContext.Builder(world).build(LootContextTypes.EMPTY), l = TrialSpawnerData.getLootSeed((ServerWorld)world, (BlockPos)pos));
        if (objectArrayList.isEmpty()) {
            return Pool.empty();
        }
        Pool.Builder builder = Pool.builder();
        for (ItemStack itemStack : objectArrayList) {
            builder.add((Object)itemStack.copyWithCount(1), itemStack.getCount());
        }
        this.itemsToDropWhenOminous = builder.build();
        return this.itemsToDropWhenOminous;
    }

    private static long getLootSeed(ServerWorld world, BlockPos pos) {
        BlockPos blockPos = new BlockPos(MathHelper.floor((float)((float)pos.getX() / 30.0f)), MathHelper.floor((float)((float)pos.getY() / 20.0f)), MathHelper.floor((float)((float)pos.getZ() / 30.0f)));
        return world.getSeed() + blockPos.asLong();
    }
}

