/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.EntityShapeContext
 *  net.minecraft.block.FluidDrainable
 *  net.minecraft.block.PowderSnowBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.CollisionEvent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.FallingBlockEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.LivingEntity$FallSounds
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.tag.EntityTypeTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.rule.GameRules
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.CollisionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class PowderSnowBlock
extends Block
implements FluidDrainable {
    public static final MapCodec<PowderSnowBlock> CODEC = PowderSnowBlock.createCodec(PowderSnowBlock::new);
    private static final float field_31216 = 0.083333336f;
    private static final float HORIZONTAL_MOVEMENT_MULTIPLIER = 0.9f;
    private static final float VERTICAL_MOVEMENT_MULTIPLIER = 1.5f;
    private static final float field_31219 = 2.5f;
    private static final VoxelShape FALLING_SHAPE = VoxelShapes.cuboid((double)0.0, (double)0.0, (double)0.0, (double)1.0, (double)0.9f, (double)1.0);
    private static final double field_36189 = 4.0;
    private static final double SMALL_FALL_SOUND_MAX_DISTANCE = 7.0;

    public MapCodec<PowderSnowBlock> getCodec() {
        return CODEC;
    }

    public PowderSnowBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf((Block)this)) {
            return true;
        }
        return super.isSideInvisible(state, stateFrom, direction);
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity2, EntityCollisionHandler handler, boolean bl) {
        if (!(entity2 instanceof LivingEntity) || entity2.getBlockStateAtPos().isOf((Block)this)) {
            entity2.slowMovement(state, new Vec3d((double)0.9f, 1.5, (double)0.9f));
            if (world.isClient()) {
                boolean bl2;
                Random random = world.getRandom();
                boolean bl3 = bl2 = entity2.lastRenderX != entity2.getX() || entity2.lastRenderZ != entity2.getZ();
                if (bl2 && random.nextBoolean()) {
                    world.addParticleClient((ParticleEffect)ParticleTypes.SNOWFLAKE, entity2.getX(), (double)(pos.getY() + 1), entity2.getZ(), (double)(MathHelper.nextBetween((Random)random, (float)-1.0f, (float)1.0f) * 0.083333336f), (double)0.05f, (double)(MathHelper.nextBetween((Random)random, (float)-1.0f, (float)1.0f) * 0.083333336f));
                }
            }
        }
        BlockPos blockPos = pos.toImmutable();
        handler.addPreCallback(CollisionEvent.EXTINGUISH, entity -> {
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                if (entity.isOnFire() && (((Boolean)serverWorld.getGameRules().getValue(GameRules.DO_MOB_GRIEFING)).booleanValue() || entity instanceof PlayerEntity) && entity.canModifyAt(serverWorld, blockPos)) {
                    world.breakBlock(blockPos, false);
                }
            }
        });
        handler.addEvent(CollisionEvent.FREEZE);
        handler.addEvent(CollisionEvent.EXTINGUISH);
    }

    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (fallDistance < 4.0 || !(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity)entity;
        LivingEntity.FallSounds fallSounds = livingEntity.getFallSounds();
        SoundEvent soundEvent = fallDistance < 7.0 ? fallSounds.small() : fallSounds.big();
        entity.playSound(soundEvent, 1.0f, 1.0f);
    }

    protected VoxelShape getInsideCollisionShape(BlockState state, BlockView world, BlockPos pos, Entity entity) {
        VoxelShape voxelShape = this.getCollisionShape(state, world, pos, ShapeContext.of((Entity)entity));
        return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        EntityShapeContext entityShapeContext;
        Entity entity;
        if (!context.isPlacement() && context instanceof EntityShapeContext && (entity = (entityShapeContext = (EntityShapeContext)context).getEntity()) != null) {
            if (entity.fallDistance > 2.5) {
                return FALLING_SHAPE;
            }
            boolean bl = entity instanceof FallingBlockEntity;
            if (bl || PowderSnowBlock.canWalkOnPowderSnow((Entity)entity) && context.isAbove(VoxelShapes.fullCube(), pos, false) && !context.isDescending()) {
                return super.getCollisionShape(state, world, pos, context);
            }
        }
        return VoxelShapes.empty();
    }

    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    public static boolean canWalkOnPowderSnow(Entity entity) {
        if (entity.getType().isIn(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) {
            return true;
        }
        if (entity instanceof LivingEntity) {
            return ((LivingEntity)entity).getEquippedStack(EquipmentSlot.FEET).isOf(Items.LEATHER_BOOTS);
        }
        return false;
    }

    public ItemStack tryDrainFluid(@Nullable LivingEntity drainer, WorldAccess world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        if (!world.isClient()) {
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState((BlockState)state));
        }
        return new ItemStack((ItemConvertible)Items.POWDER_SNOW_BUCKET);
    }

    public Optional<SoundEvent> getBucketFillSound() {
        return Optional.of(SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return true;
    }
}

