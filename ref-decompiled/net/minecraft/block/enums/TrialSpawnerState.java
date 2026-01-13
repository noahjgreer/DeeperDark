/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.enums.TrialSpawnerState
 *  net.minecraft.block.enums.TrialSpawnerState$ParticleEmitter
 *  net.minecraft.block.spawner.TrialSpawnerConfig
 *  net.minecraft.block.spawner.TrialSpawnerData
 *  net.minecraft.block.spawner.TrialSpawnerLogic
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.OminousItemSpawnerEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.StringIdentifiable
 *  net.minecraft.util.Util
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.enums;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class TrialSpawnerState
extends Enum<TrialSpawnerState>
implements StringIdentifiable {
    public static final /* enum */ TrialSpawnerState INACTIVE = new TrialSpawnerState("INACTIVE", 0, "inactive", 0, ParticleEmitter.NONE, -1.0, false);
    public static final /* enum */ TrialSpawnerState WAITING_FOR_PLAYERS = new TrialSpawnerState("WAITING_FOR_PLAYERS", 1, "waiting_for_players", 4, ParticleEmitter.WAITING, 200.0, true);
    public static final /* enum */ TrialSpawnerState ACTIVE = new TrialSpawnerState("ACTIVE", 2, "active", 8, ParticleEmitter.ACTIVE, 1000.0, true);
    public static final /* enum */ TrialSpawnerState WAITING_FOR_REWARD_EJECTION = new TrialSpawnerState("WAITING_FOR_REWARD_EJECTION", 3, "waiting_for_reward_ejection", 8, ParticleEmitter.WAITING, -1.0, false);
    public static final /* enum */ TrialSpawnerState EJECTING_REWARD = new TrialSpawnerState("EJECTING_REWARD", 4, "ejecting_reward", 8, ParticleEmitter.WAITING, -1.0, false);
    public static final /* enum */ TrialSpawnerState COOLDOWN = new TrialSpawnerState("COOLDOWN", 5, "cooldown", 0, ParticleEmitter.COOLDOWN, -1.0, false);
    private static final float START_EJECTING_REWARDS_COOLDOWN = 40.0f;
    private static final int BETWEEN_EJECTING_REWARDS_COOLDOWN;
    private final String id;
    private final int luminance;
    private final double displayRotationSpeed;
    private final ParticleEmitter particleEmitter;
    private final boolean playsSound;
    private static final /* synthetic */ TrialSpawnerState[] field_47396;

    public static TrialSpawnerState[] values() {
        return (TrialSpawnerState[])field_47396.clone();
    }

    public static TrialSpawnerState valueOf(String string) {
        return Enum.valueOf(TrialSpawnerState.class, string);
    }

    private TrialSpawnerState(String id, int luminance, ParticleEmitter particleEmitter, double displayRotationSpeed, boolean playsSound) {
        this.id = id;
        this.luminance = luminance;
        this.particleEmitter = particleEmitter;
        this.displayRotationSpeed = displayRotationSpeed;
        this.playsSound = playsSound;
    }

    TrialSpawnerState tick(BlockPos pos, TrialSpawnerLogic logic, ServerWorld world) {
        TrialSpawnerData trialSpawnerData = logic.getData();
        TrialSpawnerConfig trialSpawnerConfig = logic.getConfig();
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> {
                if (trialSpawnerData.setDisplayEntity(logic, (World)world, WAITING_FOR_PLAYERS) == null) {
                    yield this;
                }
                yield WAITING_FOR_PLAYERS;
            }
            case 1 -> {
                if (!logic.canActivate(world)) {
                    trialSpawnerData.deactivate();
                    yield this;
                }
                if (!trialSpawnerData.hasSpawnData(logic, world.random)) {
                    yield INACTIVE;
                }
                trialSpawnerData.updatePlayers(world, pos, logic);
                if (trialSpawnerData.players.isEmpty()) {
                    yield this;
                }
                yield ACTIVE;
            }
            case 2 -> {
                if (!logic.canActivate(world)) {
                    trialSpawnerData.deactivate();
                    yield WAITING_FOR_PLAYERS;
                }
                if (!trialSpawnerData.hasSpawnData(logic, world.random)) {
                    yield INACTIVE;
                }
                int i = trialSpawnerData.getAdditionalPlayers(pos);
                trialSpawnerData.updatePlayers(world, pos, logic);
                if (logic.isOminous()) {
                    this.spawnOminousItemSpawner(world, pos, logic);
                }
                if (trialSpawnerData.hasSpawnedAllMobs(trialSpawnerConfig, i)) {
                    if (trialSpawnerData.areMobsDead()) {
                        trialSpawnerData.cooldownEnd = world.getTime() + (long)logic.getCooldownLength();
                        trialSpawnerData.totalSpawnedMobs = 0;
                        trialSpawnerData.nextMobSpawnsAt = 0L;
                        yield WAITING_FOR_REWARD_EJECTION;
                    }
                } else if (trialSpawnerData.canSpawnMore(world, trialSpawnerConfig, i)) {
                    logic.trySpawnMob(world, pos).ifPresent(uuid -> {
                        trialSpawnerData.spawnedMobsAlive.add(uuid);
                        ++trialSpawnerData.totalSpawnedMobs;
                        trialSpawnerData.nextMobSpawnsAt = world.getTime() + (long)trialSpawnerConfig.ticksBetweenSpawn();
                        trialSpawnerConfig.spawnPotentials().getOrEmpty(world.getRandom()).ifPresent(spawnData -> {
                            trialSpawnerData.spawnData = Optional.of(spawnData);
                            logic.updateListeners();
                        });
                    });
                }
                yield this;
            }
            case 3 -> {
                if (trialSpawnerData.isCooldownPast(world, 40.0f, logic.getCooldownLength())) {
                    world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_OPEN_SHUTTER, SoundCategory.BLOCKS);
                    yield EJECTING_REWARD;
                }
                yield this;
            }
            case 4 -> {
                if (!trialSpawnerData.isCooldownAtRepeating(world, (float)BETWEEN_EJECTING_REWARDS_COOLDOWN, logic.getCooldownLength())) {
                    yield this;
                }
                if (trialSpawnerData.players.isEmpty()) {
                    world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_CLOSE_SHUTTER, SoundCategory.BLOCKS);
                    trialSpawnerData.rewardLootTable = Optional.empty();
                    yield COOLDOWN;
                }
                if (trialSpawnerData.rewardLootTable.isEmpty()) {
                    trialSpawnerData.rewardLootTable = trialSpawnerConfig.lootTablesToEject().getOrEmpty(world.getRandom());
                }
                trialSpawnerData.rewardLootTable.ifPresent(lootTable -> logic.ejectLootTable(world, pos, lootTable));
                trialSpawnerData.players.remove(trialSpawnerData.players.iterator().next());
                yield this;
            }
            case 5 -> {
                trialSpawnerData.updatePlayers(world, pos, logic);
                if (!trialSpawnerData.players.isEmpty()) {
                    trialSpawnerData.totalSpawnedMobs = 0;
                    trialSpawnerData.nextMobSpawnsAt = 0L;
                    yield ACTIVE;
                }
                if (trialSpawnerData.isCooldownOver(world)) {
                    logic.setNotOminous(world, pos);
                    trialSpawnerData.reset();
                    yield WAITING_FOR_PLAYERS;
                }
                yield this;
            }
        };
    }

    private void spawnOminousItemSpawner(ServerWorld world, BlockPos pos2, TrialSpawnerLogic logic) {
        TrialSpawnerConfig trialSpawnerConfig;
        TrialSpawnerData trialSpawnerData = logic.getData();
        ItemStack itemStack = trialSpawnerData.getItemsToDropWhenOminous(world, trialSpawnerConfig = logic.getConfig(), pos2).getOrEmpty(world.random).orElse(ItemStack.EMPTY);
        if (itemStack.isEmpty()) {
            return;
        }
        if (this.shouldCooldownEnd(world, trialSpawnerData)) {
            TrialSpawnerState.getPosToSpawnItemSpawner((ServerWorld)world, (BlockPos)pos2, (TrialSpawnerLogic)logic, (TrialSpawnerData)trialSpawnerData).ifPresent(pos -> {
                OminousItemSpawnerEntity ominousItemSpawnerEntity = OminousItemSpawnerEntity.create((World)world, (ItemStack)itemStack);
                ominousItemSpawnerEntity.refreshPositionAfterTeleport(pos);
                world.spawnEntity((Entity)ominousItemSpawnerEntity);
                float f = (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2f + 1.0f;
                world.playSound(null, BlockPos.ofFloored((Position)pos), SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_ITEM_BEGIN, SoundCategory.BLOCKS, 1.0f, f);
                trialSpawnerData.cooldownEnd = world.getTime() + logic.getOminousConfig().getCooldownLength();
            });
        }
    }

    private static Optional<Vec3d> getPosToSpawnItemSpawner(ServerWorld world, BlockPos pos, TrialSpawnerLogic logic, TrialSpawnerData data) {
        List<PlayerEntity> list = data.players.stream().map(arg_0 -> ((ServerWorld)world).getPlayerByUuid(arg_0)).filter(Objects::nonNull).filter(player -> !player.isCreative() && !player.isSpectator() && player.isAlive() && player.squaredDistanceTo(pos.toCenterPos()) <= (double)MathHelper.square((int)logic.getDetectionRadius())).toList();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        Entity entity = TrialSpawnerState.getRandomEntity(list, (Set)data.spawnedMobsAlive, (TrialSpawnerLogic)logic, (BlockPos)pos, (ServerWorld)world);
        if (entity == null) {
            return Optional.empty();
        }
        return TrialSpawnerState.getPosAbove((Entity)entity, (ServerWorld)world);
    }

    private static Optional<Vec3d> getPosAbove(Entity entity, ServerWorld world) {
        Vec3d vec3d2;
        Vec3d vec3d = entity.getEntityPos();
        BlockHitResult blockHitResult = world.raycast(new RaycastContext(vec3d, vec3d2 = vec3d.offset(Direction.UP, (double)(entity.getHeight() + 2.0f + (float)world.random.nextInt(4))), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
        Vec3d vec3d3 = blockHitResult.getBlockPos().toCenterPos().offset(Direction.DOWN, 1.0);
        BlockPos blockPos = BlockPos.ofFloored((Position)vec3d3);
        if (!world.getBlockState(blockPos).getCollisionShape((BlockView)world, blockPos).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(vec3d3);
    }

    private static @Nullable Entity getRandomEntity(List<PlayerEntity> players, Set<UUID> entityUuids, TrialSpawnerLogic logic, BlockPos pos, ServerWorld world) {
        List<PlayerEntity> list;
        Stream<Entity> stream = entityUuids.stream().map(arg_0 -> ((ServerWorld)world).getEntity(arg_0)).filter(Objects::nonNull).filter(entity -> entity.isAlive() && entity.squaredDistanceTo(pos.toCenterPos()) <= (double)MathHelper.square((int)logic.getDetectionRadius()));
        List<Object> list2 = list = world.random.nextBoolean() ? stream.toList() : players;
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return (Entity)list.getFirst();
        }
        return (Entity)Util.getRandom(list, (Random)world.random);
    }

    private boolean shouldCooldownEnd(ServerWorld world, TrialSpawnerData data) {
        return world.getTime() >= data.cooldownEnd;
    }

    public int getLuminance() {
        return this.luminance;
    }

    public double getDisplayRotationSpeed() {
        return this.displayRotationSpeed;
    }

    public boolean doesDisplayRotate() {
        return this.displayRotationSpeed >= 0.0;
    }

    public boolean playsSound() {
        return this.playsSound;
    }

    public void emitParticles(World world, BlockPos pos, boolean ominous) {
        this.particleEmitter.emit(world, world.getRandom(), pos, ominous);
    }

    public String asString() {
        return this.id;
    }

    private static /* synthetic */ TrialSpawnerState[] method_55218() {
        return new TrialSpawnerState[]{INACTIVE, WAITING_FOR_PLAYERS, ACTIVE, WAITING_FOR_REWARD_EJECTION, EJECTING_REWARD, COOLDOWN};
    }

    static {
        field_47396 = TrialSpawnerState.method_55218();
        BETWEEN_EJECTING_REWARDS_COOLDOWN = MathHelper.floor((float)30.0f);
    }
}

