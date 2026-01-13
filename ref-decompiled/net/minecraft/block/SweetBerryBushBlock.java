/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SweetBerryBushBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.loot.LootTables
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class SweetBerryBushBlock
extends PlantBlock
implements Fertilizable {
    public static final MapCodec<SweetBerryBushBlock> CODEC = SweetBerryBushBlock.createCodec(SweetBerryBushBlock::new);
    private static final float MIN_MOVEMENT_FOR_DAMAGE = 0.003f;
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;
    private static final VoxelShape SMALL_SHAPE = Block.createColumnShape((double)10.0, (double)0.0, (double)8.0);
    private static final VoxelShape LARGE_SHAPE = Block.createColumnShape((double)14.0, (double)0.0, (double)16.0);

    public MapCodec<SweetBerryBushBlock> getCodec() {
        return CODEC;
    }

    public SweetBerryBushBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)AGE, (Comparable)Integer.valueOf(0)));
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack((ItemConvertible)Items.SWEET_BERRIES);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch ((Integer)state.get((Property)AGE)) {
            case 0 -> SMALL_SHAPE;
            case 3 -> VoxelShapes.fullCube();
            default -> LARGE_SHAPE;
        };
    }

    protected boolean hasRandomTicks(BlockState state) {
        return (Integer)state.get((Property)AGE) < 3;
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = (Integer)state.get((Property)AGE);
        if (i < 3 && random.nextInt(5) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
            BlockState blockState = (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(i + 1));
            world.setBlockState(pos, blockState, 2);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((BlockState)blockState));
        }
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        Vec3d vec3d;
        ServerWorld serverWorld;
        block7: {
            block6: {
                if (!(entity instanceof LivingEntity) || entity.getType() == EntityType.FOX || entity.getType() == EntityType.BEE) {
                    return;
                }
                entity.slowMovement(state, new Vec3d((double)0.8f, 0.75, (double)0.8f));
                if (!(world instanceof ServerWorld)) break block6;
                serverWorld = (ServerWorld)world;
                if ((Integer)state.get((Property)AGE) != 0) break block7;
            }
            return;
        }
        Vec3d vec3d2 = vec3d = entity.isControlledByPlayer() ? entity.getMovement() : entity.getLastRenderPos().subtract(entity.getEntityPos());
        if (vec3d.horizontalLengthSquared() > 0.0) {
            double d = Math.abs(vec3d.getX());
            double e = Math.abs(vec3d.getZ());
            if (d >= (double)0.003f || e >= (double)0.003f) {
                entity.damage(serverWorld, world.getDamageSources().sweetBerryBush(), 1.0f);
            }
        }
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        boolean bl;
        int i = (Integer)state.get((Property)AGE);
        boolean bl2 = bl = i == 3;
        if (!bl && stack.isOf(Items.BONE_MEAL)) {
            return ActionResult.PASS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if ((Integer)state.get((Property)AGE) > 1) {
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                Block.generateBlockInteractLoot((ServerWorld)serverWorld, (RegistryKey)LootTables.SWEET_BERRY_BUSH_HARVEST, (BlockState)state, (BlockEntity)world.getBlockEntity(pos), null, (Entity)player, (worldx, stack) -> Block.dropStack((World)worldx, (BlockPos)pos, (ItemStack)stack));
                serverWorld.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0f, 0.8f + serverWorld.random.nextFloat() * 0.4f);
                BlockState blockState = (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(1));
                serverWorld.setBlockState(pos, blockState, 2);
                serverWorld.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((Entity)player, (BlockState)blockState));
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE});
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return (Integer)state.get((Property)AGE) < 3;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = Math.min(3, (Integer)state.get((Property)AGE) + 1);
        world.setBlockState(pos, (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(i)), 2);
    }
}

