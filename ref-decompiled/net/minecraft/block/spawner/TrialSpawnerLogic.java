/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.TrialSpawnerBlock
 *  net.minecraft.block.dispenser.ItemDispenserBehavior
 *  net.minecraft.block.enums.TrialSpawnerState
 *  net.minecraft.block.spawner.EntityDetector
 *  net.minecraft.block.spawner.EntityDetector$Selector
 *  net.minecraft.block.spawner.MobSpawnerEntry
 *  net.minecraft.block.spawner.MobSpawnerEntry$CustomSpawnRules
 *  net.minecraft.block.spawner.TrialSpawnerConfig
 *  net.minecraft.block.spawner.TrialSpawnerData
 *  net.minecraft.block.spawner.TrialSpawnerData$Packed
 *  net.minecraft.block.spawner.TrialSpawnerLogic
 *  net.minecraft.block.spawner.TrialSpawnerLogic$FullConfig
 *  net.minecraft.block.spawner.TrialSpawnerLogic$TrialSpawner
 *  net.minecraft.block.spawner.TrialSpawnerLogic$Type
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.SpawnRestriction
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.loot.LootTable
 *  net.minecraft.loot.context.LootContextTypes
 *  net.minecraft.loot.context.LootWorldContext
 *  net.minecraft.loot.context.LootWorldContext$Builder
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.particle.SimpleParticleType
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.NbtReadView
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.ErrorReporter
 *  net.minecraft.util.ErrorReporter$Logging
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  net.minecraft.world.ServerWorldAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.rule.GameRules
 *  org.slf4j.Logger
 */
package net.minecraft.block.spawner;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TrialSpawnerBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.EntityDetector;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.rule.GameRules;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
public final class TrialSpawnerLogic {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int field_47358 = 40;
    private static final int DEFAULT_COOLDOWN_LENGTH = 36000;
    private static final int DEFAULT_ENTITY_DETECTION_RANGE = 14;
    private static final int MAX_ENTITY_DISTANCE = 47;
    private static final int MAX_ENTITY_DISTANCE_SQUARED = MathHelper.square((int)47);
    private static final float SOUND_RATE_PER_TICK = 0.02f;
    private final TrialSpawnerData data = new TrialSpawnerData();
    private FullConfig fullConfig;
    private final TrialSpawner trialSpawner;
    private EntityDetector entityDetector;
    private final EntityDetector.Selector entitySelector;
    private boolean forceActivate;
    private boolean ominous;

    public TrialSpawnerLogic(FullConfig fullConfig, TrialSpawner trialSpawner, EntityDetector entityDetector, EntityDetector.Selector entitySelector) {
        this.fullConfig = fullConfig;
        this.trialSpawner = trialSpawner;
        this.entityDetector = entityDetector;
        this.entitySelector = entitySelector;
    }

    public TrialSpawnerConfig getConfig() {
        return this.ominous ? (TrialSpawnerConfig)this.fullConfig.ominous().value() : (TrialSpawnerConfig)this.fullConfig.normal.value();
    }

    public TrialSpawnerConfig getNormalConfig() {
        return (TrialSpawnerConfig)this.fullConfig.normal.value();
    }

    public TrialSpawnerConfig getOminousConfig() {
        return (TrialSpawnerConfig)this.fullConfig.ominous.value();
    }

    public void readData(ReadView view) {
        view.read(TrialSpawnerData.Packed.CODEC).ifPresent(arg_0 -> ((TrialSpawnerData)this.data).unpack(arg_0));
        this.fullConfig = view.read(FullConfig.CODEC).orElse(FullConfig.DEFAULT);
    }

    public void writeData(WriteView view) {
        view.put(TrialSpawnerData.Packed.CODEC, (Object)this.data.pack());
        view.put(FullConfig.CODEC, (Object)this.fullConfig);
    }

    public void setOminous(ServerWorld world, BlockPos pos) {
        world.setBlockState(pos, (BlockState)world.getBlockState(pos).with((Property)TrialSpawnerBlock.OMINOUS, (Comparable)Boolean.valueOf(true)), 3);
        world.syncWorldEvent(3020, pos, 1);
        this.ominous = true;
        this.data.resetAndClearMobs(this, world);
    }

    public void setNotOminous(ServerWorld world, BlockPos pos) {
        world.setBlockState(pos, (BlockState)world.getBlockState(pos).with((Property)TrialSpawnerBlock.OMINOUS, (Comparable)Boolean.valueOf(false)), 3);
        this.ominous = false;
    }

    public boolean isOminous() {
        return this.ominous;
    }

    public int getCooldownLength() {
        return this.fullConfig.targetCooldownLength;
    }

    public int getDetectionRadius() {
        return this.fullConfig.requiredPlayerRange;
    }

    public TrialSpawnerState getSpawnerState() {
        return this.trialSpawner.getSpawnerState();
    }

    public TrialSpawnerData getData() {
        return this.data;
    }

    public void setSpawnerState(World world, TrialSpawnerState spawnerState) {
        this.trialSpawner.setSpawnerState(world, spawnerState);
    }

    public void updateListeners() {
        this.trialSpawner.updateListeners();
    }

    public EntityDetector getEntityDetector() {
        return this.entityDetector;
    }

    public EntityDetector.Selector getEntitySelector() {
        return this.entitySelector;
    }

    public boolean canActivate(ServerWorld world) {
        if (!((Boolean)world.getGameRules().getValue(GameRules.SPAWNER_BLOCKS_WORK)).booleanValue()) {
            return false;
        }
        if (this.forceActivate) {
            return true;
        }
        if (world.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        return (Boolean)world.getGameRules().getValue(GameRules.DO_MOB_SPAWNING);
    }

    public Optional<UUID> trySpawnMob(ServerWorld world, BlockPos pos) {
        Random random = world.getRandom();
        MobSpawnerEntry mobSpawnerEntry = this.data.getSpawnData(this, world.getRandom());
        try (ErrorReporter.Logging logging = new ErrorReporter.Logging(() -> "spawner@" + String.valueOf(pos), LOGGER);){
            Object mobEntity;
            MobSpawnerEntry.CustomSpawnRules customSpawnRules;
            ReadView readView = NbtReadView.create((ErrorReporter)logging, (RegistryWrapper.WrapperLookup)world.getRegistryManager(), (NbtCompound)mobSpawnerEntry.entity());
            Optional optional = EntityType.fromData((ReadView)readView);
            if (optional.isEmpty()) {
                Optional<UUID> optional2 = Optional.empty();
                return optional2;
            }
            Vec3d vec3d = readView.read("Pos", Vec3d.CODEC).orElseGet(() -> {
                TrialSpawnerConfig trialSpawnerConfig = this.getConfig();
                return new Vec3d((double)pos.getX() + (random.nextDouble() - random.nextDouble()) * (double)trialSpawnerConfig.spawnRange() + 0.5, (double)(pos.getY() + random.nextInt(3) - 1), (double)pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double)trialSpawnerConfig.spawnRange() + 0.5);
            });
            if (!world.isSpaceEmpty(((EntityType)optional.get()).getSpawnBox(vec3d.x, vec3d.y, vec3d.z))) {
                Optional<UUID> optional3 = Optional.empty();
                return optional3;
            }
            if (!TrialSpawnerLogic.hasLineOfSight((World)world, (Vec3d)pos.toCenterPos(), (Vec3d)vec3d)) {
                Optional<UUID> optional4 = Optional.empty();
                return optional4;
            }
            BlockPos blockPos = BlockPos.ofFloored((Position)vec3d);
            if (!SpawnRestriction.canSpawn((EntityType)((EntityType)optional.get()), (ServerWorldAccess)world, (SpawnReason)SpawnReason.TRIAL_SPAWNER, (BlockPos)blockPos, (Random)world.getRandom())) {
                Optional<UUID> optional5 = Optional.empty();
                return optional5;
            }
            if (mobSpawnerEntry.getCustomSpawnRules().isPresent() && !(customSpawnRules = (MobSpawnerEntry.CustomSpawnRules)mobSpawnerEntry.getCustomSpawnRules().get()).canSpawn(blockPos, world)) {
                Optional<UUID> optional6 = Optional.empty();
                return optional6;
            }
            Entity entity2 = EntityType.loadEntityWithPassengers((ReadView)readView, (World)world, (SpawnReason)SpawnReason.TRIAL_SPAWNER, entity -> {
                entity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, random.nextFloat() * 360.0f, 0.0f);
                return entity;
            });
            if (entity2 == null) {
                Optional<UUID> optional7 = Optional.empty();
                return optional7;
            }
            if (entity2 instanceof MobEntity) {
                boolean bl;
                mobEntity = (MobEntity)entity2;
                if (!mobEntity.canSpawn((WorldView)world)) {
                    Optional<UUID> optional8 = Optional.empty();
                    return optional8;
                }
                boolean bl2 = bl = mobSpawnerEntry.getNbt().getSize() == 1 && mobSpawnerEntry.getNbt().getString("id").isPresent();
                if (bl) {
                    mobEntity.initialize((ServerWorldAccess)world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.TRIAL_SPAWNER, null);
                }
                mobEntity.setPersistent();
                mobSpawnerEntry.getEquipment().ifPresent(arg_0 -> ((MobEntity)mobEntity).setEquipmentFromTable(arg_0));
            }
            if (!world.spawnNewEntityAndPassengers(entity2)) {
                mobEntity = Optional.empty();
                return mobEntity;
            }
            Type type = this.ominous ? Type.OMINOUS : Type.NORMAL;
            world.syncWorldEvent(3011, pos, type.getIndex());
            world.syncWorldEvent(3012, blockPos, type.getIndex());
            world.emitGameEvent(entity2, (RegistryEntry)GameEvent.ENTITY_PLACE, blockPos);
            Optional<UUID> optional9 = Optional.of(entity2.getUuid());
            return optional9;
        }
    }

    public void ejectLootTable(ServerWorld world, BlockPos pos, RegistryKey<LootTable> lootTable) {
        LootWorldContext lootWorldContext;
        LootTable lootTable2 = world.getServer().getReloadableRegistries().getLootTable(lootTable);
        ObjectArrayList objectArrayList = lootTable2.generateLoot(lootWorldContext = new LootWorldContext.Builder(world).build(LootContextTypes.EMPTY));
        if (!objectArrayList.isEmpty()) {
            for (ItemStack itemStack : objectArrayList) {
                ItemDispenserBehavior.spawnItem((World)world, (ItemStack)itemStack, (int)2, (Direction)Direction.UP, (Position)Vec3d.ofBottomCenter((Vec3i)pos).offset(Direction.UP, 1.2));
            }
            world.syncWorldEvent(3014, pos, 0);
        }
    }

    public void tickClient(World world, BlockPos pos, boolean ominous) {
        Random random;
        TrialSpawnerState trialSpawnerState = this.getSpawnerState();
        trialSpawnerState.emitParticles(world, pos, ominous);
        if (trialSpawnerState.doesDisplayRotate()) {
            double d = Math.max(0L, this.data.nextMobSpawnsAt - world.getTime());
            this.data.lastDisplayEntityRotation = this.data.displayEntityRotation;
            this.data.displayEntityRotation = (this.data.displayEntityRotation + trialSpawnerState.getDisplayRotationSpeed() / (d + 200.0)) % 360.0;
        }
        if (trialSpawnerState.playsSound() && (random = world.getRandom()).nextFloat() <= 0.02f) {
            SoundEvent soundEvent = ominous ? SoundEvents.BLOCK_TRIAL_SPAWNER_AMBIENT_OMINOUS : SoundEvents.BLOCK_TRIAL_SPAWNER_AMBIENT;
            world.playSoundAtBlockCenterClient(pos, soundEvent, SoundCategory.BLOCKS, random.nextFloat() * 0.25f + 0.75f, random.nextFloat() + 0.5f, false);
        }
    }

    public void tickServer(ServerWorld world, BlockPos pos, boolean ominous) {
        TrialSpawnerState trialSpawnerState2;
        this.ominous = ominous;
        TrialSpawnerState trialSpawnerState = this.getSpawnerState();
        if (this.data.spawnedMobsAlive.removeIf(uuid -> TrialSpawnerLogic.shouldRemoveMobFromData((ServerWorld)world, (BlockPos)pos, (UUID)uuid))) {
            this.data.nextMobSpawnsAt = world.getTime() + (long)this.getConfig().ticksBetweenSpawn();
        }
        if ((trialSpawnerState2 = trialSpawnerState.tick(pos, this, world)) != trialSpawnerState) {
            this.setSpawnerState((World)world, trialSpawnerState2);
        }
    }

    private static boolean shouldRemoveMobFromData(ServerWorld world, BlockPos pos, UUID uuid) {
        Entity entity = world.getEntity(uuid);
        return entity == null || !entity.isAlive() || !entity.getEntityWorld().getRegistryKey().equals(world.getRegistryKey()) || entity.getBlockPos().getSquaredDistance((Vec3i)pos) > (double)MAX_ENTITY_DISTANCE_SQUARED;
    }

    private static boolean hasLineOfSight(World world, Vec3d spawnerPos, Vec3d spawnPos) {
        BlockHitResult blockHitResult = world.raycast(new RaycastContext(spawnPos, spawnerPos, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
        return blockHitResult.getBlockPos().equals((Object)BlockPos.ofFloored((Position)spawnerPos)) || blockHitResult.getType() == HitResult.Type.MISS;
    }

    public static void addMobSpawnParticles(World world, BlockPos pos, Random random, SimpleParticleType particle) {
        for (int i = 0; i < 20; ++i) {
            double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double e = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            world.addParticleClient((ParticleEffect)ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
            world.addParticleClient((ParticleEffect)particle, d, e, f, 0.0, 0.0, 0.0);
        }
    }

    public static void addTrialOmenParticles(World world, BlockPos pos, Random random) {
        for (int i = 0; i < 20; ++i) {
            double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double e = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
            double g = random.nextGaussian() * 0.02;
            double h = random.nextGaussian() * 0.02;
            double j = random.nextGaussian() * 0.02;
            world.addParticleClient((ParticleEffect)ParticleTypes.TRIAL_OMEN, d, e, f, g, h, j);
            world.addParticleClient((ParticleEffect)ParticleTypes.SOUL_FIRE_FLAME, d, e, f, g, h, j);
        }
    }

    public static void addDetectionParticles(World world, BlockPos pos, Random random, int playerCount, ParticleEffect particle) {
        for (int i = 0; i < 30 + Math.min(playerCount, 10) * 5; ++i) {
            double d = (double)(2.0f * random.nextFloat() - 1.0f) * 0.65;
            double e = (double)(2.0f * random.nextFloat() - 1.0f) * 0.65;
            double f = (double)pos.getX() + 0.5 + d;
            double g = (double)pos.getY() + 0.1 + (double)random.nextFloat() * 0.8;
            double h = (double)pos.getZ() + 0.5 + e;
            world.addParticleClient(particle, f, g, h, 0.0, 0.0, 0.0);
        }
    }

    public static void addEjectItemParticles(World world, BlockPos pos, Random random) {
        for (int i = 0; i < 20; ++i) {
            double d = (double)pos.getX() + 0.4 + random.nextDouble() * 0.2;
            double e = (double)pos.getY() + 0.4 + random.nextDouble() * 0.2;
            double f = (double)pos.getZ() + 0.4 + random.nextDouble() * 0.2;
            double g = random.nextGaussian() * 0.02;
            double h = random.nextGaussian() * 0.02;
            double j = random.nextGaussian() * 0.02;
            world.addParticleClient((ParticleEffect)ParticleTypes.SMALL_FLAME, d, e, f, g, h, j * 0.25);
            world.addParticleClient((ParticleEffect)ParticleTypes.SMOKE, d, e, f, g, h, j);
        }
    }

    public void setEntityType(EntityType<?> entityType, World world) {
        this.data.reset();
        this.fullConfig = this.fullConfig.withEntityType(entityType);
        this.setSpawnerState(world, TrialSpawnerState.INACTIVE);
    }

    @Deprecated(forRemoval=true)
    @VisibleForTesting
    public void setEntityDetector(EntityDetector detector) {
        this.entityDetector = detector;
    }

    @Deprecated(forRemoval=true)
    @VisibleForTesting
    public void forceActivate() {
        this.forceActivate = true;
    }
}

