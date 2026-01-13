/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractCauldronBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CauldronBlock
 *  net.minecraft.block.LeveledCauldronBlock
 *  net.minecraft.block.cauldron.CauldronBehavior
 *  net.minecraft.block.cauldron.CauldronBehavior$CauldronBehaviorMap
 *  net.minecraft.entity.CollisionEvent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome$Precipitation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.CollisionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

/*
 * Exception performing whole class analysis ignored.
 */
public class LeveledCauldronBlock
extends AbstractCauldronBlock {
    public static final MapCodec<LeveledCauldronBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter(block -> block.precipitation), (App)CauldronBehavior.CODEC.fieldOf("interactions").forGetter(block -> block.behaviorMap), (App)LeveledCauldronBlock.createSettingsCodec()).apply((Applicative)instance, LeveledCauldronBlock::new));
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 3;
    public static final IntProperty LEVEL = Properties.LEVEL_3;
    private static final int BASE_FLUID_HEIGHT = 6;
    private static final double FLUID_HEIGHT_PER_LEVEL = 3.0;
    private static final VoxelShape[] INSIDE_COLLISION_SHAPE_BY_LEVEL = (VoxelShape[])Util.make(() -> Block.createShapeArray((int)2, level -> VoxelShapes.union((VoxelShape)AbstractCauldronBlock.OUTLINE_SHAPE, (VoxelShape)Block.createColumnShape((double)12.0, (double)4.0, (double)LeveledCauldronBlock.getFluidHeight((int)(level + 1))))));
    private final Biome.Precipitation precipitation;

    public MapCodec<LeveledCauldronBlock> getCodec() {
        return CODEC;
    }

    public LeveledCauldronBlock(Biome.Precipitation precipitation, CauldronBehavior.CauldronBehaviorMap behaviorMap, AbstractBlock.Settings settings) {
        super(settings, behaviorMap);
        this.precipitation = precipitation;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)LEVEL, (Comparable)Integer.valueOf(1)));
    }

    public boolean isFull(BlockState state) {
        return (Integer)state.get((Property)LEVEL) == 3;
    }

    protected boolean canBeFilledByDripstone(Fluid fluid) {
        return fluid == Fluids.WATER && this.precipitation == Biome.Precipitation.RAIN;
    }

    protected double getFluidHeight(BlockState state) {
        return LeveledCauldronBlock.getFluidHeight((int)((Integer)state.get((Property)LEVEL))) / 16.0;
    }

    private static double getFluidHeight(int level) {
        return 6.0 + (double)level * 3.0;
    }

    protected VoxelShape getInsideCollisionShape(BlockState state, BlockView world, BlockPos pos, Entity entity) {
        return INSIDE_COLLISION_SHAPE_BY_LEVEL[(Integer)state.get((Property)LEVEL) - 1];
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            BlockPos blockPos = pos.toImmutable();
            handler.addPreCallback(CollisionEvent.EXTINGUISH, collidedEntity -> {
                if (collidedEntity.isOnFire() && collidedEntity.canModifyAt(serverWorld, blockPos)) {
                    this.onFireCollision(state, world, blockPos);
                }
            });
        }
        handler.addEvent(CollisionEvent.EXTINGUISH);
    }

    private void onFireCollision(BlockState state, World world, BlockPos pos) {
        if (this.precipitation == Biome.Precipitation.SNOW) {
            LeveledCauldronBlock.decrementFluidLevel((BlockState)((BlockState)Blocks.WATER_CAULDRON.getDefaultState().with((Property)LEVEL, (Comparable)((Integer)state.get((Property)LEVEL)))), (World)world, (BlockPos)pos);
        } else {
            LeveledCauldronBlock.decrementFluidLevel((BlockState)state, (World)world, (BlockPos)pos);
        }
    }

    public static void decrementFluidLevel(BlockState state, World world, BlockPos pos) {
        int i = (Integer)state.get((Property)LEVEL) - 1;
        BlockState blockState = i == 0 ? Blocks.CAULDRON.getDefaultState() : (BlockState)state.with((Property)LEVEL, (Comparable)Integer.valueOf(i));
        world.setBlockState(pos, blockState);
        world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((BlockState)blockState));
    }

    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        if (!CauldronBlock.canFillWithPrecipitation((World)world, (Biome.Precipitation)precipitation) || (Integer)state.get((Property)LEVEL) == 3 || precipitation != this.precipitation) {
            return;
        }
        BlockState blockState = (BlockState)state.cycle((Property)LEVEL);
        world.setBlockState(pos, blockState);
        world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((BlockState)blockState));
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return (Integer)state.get((Property)LEVEL);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LEVEL});
    }

    protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
        if (this.isFull(state)) {
            return;
        }
        BlockState blockState = (BlockState)state.with((Property)LEVEL, (Comparable)Integer.valueOf((Integer)state.get((Property)LEVEL) + 1));
        world.setBlockState(pos, blockState);
        world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((BlockState)blockState));
        world.syncWorldEvent(1047, pos, 0);
    }
}

