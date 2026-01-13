/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.ConduitBlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LazyEntityReference
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.mob.Monster
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.entity.UniquelyIdentifiable
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.entity.UniquelyIdentifiable;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class ConduitBlockEntity
extends BlockEntity {
    private static final int field_31333 = 2;
    private static final int field_31334 = 13;
    private static final float field_31335 = -0.0375f;
    private static final int field_31336 = 16;
    private static final int MIN_BLOCKS_TO_ACTIVATE = 42;
    private static final int field_31338 = 8;
    private static final Block[] ACTIVATING_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
    public int ticks;
    private float ticksActive;
    private boolean active;
    private boolean eyeOpen;
    private final List<BlockPos> activatingBlocks = Lists.newArrayList();
    private @Nullable LazyEntityReference<LivingEntity> targetEntity;
    private long nextAmbientSoundTime;

    public ConduitBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.CONDUIT, pos, state);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.targetEntity = LazyEntityReference.fromData((ReadView)view, (String)"Target");
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        LazyEntityReference.writeData((LazyEntityReference)this.targetEntity, (WriteView)view, (String)"Target");
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createComponentlessNbt(registries);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity) {
        ++blockEntity.ticks;
        long l = world.getTime();
        List list = blockEntity.activatingBlocks;
        if (l % 40L == 0L) {
            blockEntity.active = ConduitBlockEntity.updateActivatingBlocks((World)world, (BlockPos)pos, (List)list);
            ConduitBlockEntity.openEye((ConduitBlockEntity)blockEntity, (List)list);
        }
        LivingEntity livingEntity = LazyEntityReference.getLivingEntity((LazyEntityReference)blockEntity.targetEntity, (World)world);
        ConduitBlockEntity.spawnNautilusParticles((World)world, (BlockPos)pos, (List)list, (Entity)livingEntity, (int)blockEntity.ticks);
        if (blockEntity.isActive()) {
            blockEntity.ticksActive += 1.0f;
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity) {
        ++blockEntity.ticks;
        long l = world.getTime();
        List list = blockEntity.activatingBlocks;
        if (l % 40L == 0L) {
            boolean bl = ConduitBlockEntity.updateActivatingBlocks((World)world, (BlockPos)pos, (List)list);
            if (bl != blockEntity.active) {
                SoundEvent soundEvent = bl ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE;
                world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            blockEntity.active = bl;
            ConduitBlockEntity.openEye((ConduitBlockEntity)blockEntity, (List)list);
            if (bl) {
                ConduitBlockEntity.givePlayersEffects((World)world, (BlockPos)pos, (List)list);
                ConduitBlockEntity.tryAttack((ServerWorld)((ServerWorld)world), (BlockPos)pos, (BlockState)state, (ConduitBlockEntity)blockEntity, (list.size() >= 42 ? 1 : 0) != 0);
            }
        }
        if (blockEntity.isActive()) {
            if (l % 80L == 0L) {
                world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_AMBIENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            if (l > blockEntity.nextAmbientSoundTime) {
                blockEntity.nextAmbientSoundTime = l + 60L + (long)world.getRandom().nextInt(40);
                world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    private static void openEye(ConduitBlockEntity blockEntity, List<BlockPos> activatingBlocks) {
        blockEntity.setEyeOpen(activatingBlocks.size() >= 42);
    }

    private static boolean updateActivatingBlocks(World world, BlockPos pos, List<BlockPos> activatingBlocks) {
        int k;
        int j;
        int i;
        activatingBlocks.clear();
        for (i = -1; i <= 1; ++i) {
            for (j = -1; j <= 1; ++j) {
                for (k = -1; k <= 1; ++k) {
                    BlockPos blockPos = pos.add(i, j, k);
                    if (world.isWater(blockPos)) continue;
                    return false;
                }
            }
        }
        for (i = -2; i <= 2; ++i) {
            for (j = -2; j <= 2; ++j) {
                for (k = -2; k <= 2; ++k) {
                    int l = Math.abs(i);
                    int m = Math.abs(j);
                    int n = Math.abs(k);
                    if (l <= 1 && m <= 1 && n <= 1 || (i != 0 || m != 2 && n != 2) && (j != 0 || l != 2 && n != 2) && (k != 0 || l != 2 && m != 2)) continue;
                    BlockPos blockPos2 = pos.add(i, j, k);
                    BlockState blockState = world.getBlockState(blockPos2);
                    for (Block block : ACTIVATING_BLOCKS) {
                        if (!blockState.isOf(block)) continue;
                        activatingBlocks.add(blockPos2);
                    }
                }
            }
        }
        return activatingBlocks.size() >= 16;
    }

    private static void givePlayersEffects(World world, BlockPos pos, List<BlockPos> activatingBlocks) {
        int m;
        int l;
        int i = activatingBlocks.size();
        int j = i / 7 * 16;
        int k = pos.getX();
        Box box = new Box((double)k, (double)(l = pos.getY()), (double)(m = pos.getZ()), (double)(k + 1), (double)(l + 1), (double)(m + 1)).expand((double)j).stretch(0.0, (double)world.getHeight(), 0.0);
        List list = world.getNonSpectatingEntities(PlayerEntity.class, box);
        if (list.isEmpty()) {
            return;
        }
        for (PlayerEntity playerEntity : list) {
            if (!pos.isWithinDistance((Vec3i)playerEntity.getBlockPos(), (double)j) || !playerEntity.isTouchingWaterOrRain()) continue;
            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 260, 0, true, true));
        }
    }

    private static void tryAttack(ServerWorld world, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity, boolean canAttack) {
        LazyEntityReference lazyEntityReference = ConduitBlockEntity.getValidTarget((LazyEntityReference)blockEntity.targetEntity, (ServerWorld)world, (BlockPos)pos, (boolean)canAttack);
        LivingEntity livingEntity = LazyEntityReference.getLivingEntity((LazyEntityReference)lazyEntityReference, (World)world);
        if (livingEntity != null) {
            world.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0f, 1.0f);
            livingEntity.damage(world, world.getDamageSources().magic(), 4.0f);
        }
        if (!Objects.equals(lazyEntityReference, blockEntity.targetEntity)) {
            blockEntity.targetEntity = lazyEntityReference;
            world.updateListeners(pos, state, state, 2);
        }
    }

    private static @Nullable LazyEntityReference<LivingEntity> getValidTarget(@Nullable LazyEntityReference<LivingEntity> currentTarget, ServerWorld world, BlockPos pos, boolean canAttack) {
        if (!canAttack) {
            return null;
        }
        if (currentTarget == null) {
            return ConduitBlockEntity.findAttackTarget((ServerWorld)world, (BlockPos)pos);
        }
        LivingEntity livingEntity = LazyEntityReference.getLivingEntity(currentTarget, (World)world);
        if (livingEntity == null || !livingEntity.isAlive() || !pos.isWithinDistance((Vec3i)livingEntity.getBlockPos(), 8.0)) {
            return null;
        }
        return currentTarget;
    }

    private static @Nullable LazyEntityReference<LivingEntity> findAttackTarget(ServerWorld world, BlockPos pos) {
        List list = world.getEntitiesByClass(LivingEntity.class, ConduitBlockEntity.getAttackZone((BlockPos)pos), entity -> entity instanceof Monster && entity.isTouchingWaterOrRain());
        if (list.isEmpty()) {
            return null;
        }
        return LazyEntityReference.of((UniquelyIdentifiable)((LivingEntity)Util.getRandom((List)list, (Random)world.random)));
    }

    private static Box getAttackZone(BlockPos pos) {
        return new Box(pos).expand(8.0);
    }

    private static void spawnNautilusParticles(World world, BlockPos pos, List<BlockPos> activatingBlocks, @Nullable Entity entity, int ticks) {
        float f;
        Random random = world.random;
        double d = MathHelper.sin((double)((float)(ticks + 35) * 0.1f)) / 2.0f + 0.5f;
        d = (d * d + d) * (double)0.3f;
        Vec3d vec3d = new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 1.5 + d, (double)pos.getZ() + 0.5);
        for (BlockPos blockPos : activatingBlocks) {
            if (random.nextInt(50) != 0) continue;
            BlockPos blockPos2 = blockPos.subtract((Vec3i)pos);
            f = -0.5f + random.nextFloat() + (float)blockPos2.getX();
            float g = -2.0f + random.nextFloat() + (float)blockPos2.getY();
            float h = -0.5f + random.nextFloat() + (float)blockPos2.getZ();
            world.addParticleClient((ParticleEffect)ParticleTypes.NAUTILUS, vec3d.x, vec3d.y, vec3d.z, (double)f, (double)g, (double)h);
        }
        if (entity != null) {
            Vec3d vec3d2 = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
            float i = (-0.5f + random.nextFloat()) * (3.0f + entity.getWidth());
            float j = -1.0f + random.nextFloat() * entity.getHeight();
            f = (-0.5f + random.nextFloat()) * (3.0f + entity.getWidth());
            Vec3d vec3d3 = new Vec3d((double)i, (double)j, (double)f);
            world.addParticleClient((ParticleEffect)ParticleTypes.NAUTILUS, vec3d2.x, vec3d2.y, vec3d2.z, vec3d3.x, vec3d3.y, vec3d3.z);
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isEyeOpen() {
        return this.eyeOpen;
    }

    private void setEyeOpen(boolean eyeOpen) {
        this.eyeOpen = eyeOpen;
    }

    public float getRotation(float tickProgress) {
        return (this.ticksActive + tickProgress) * -0.0375f;
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

