/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.FrostedIceBlock
 *  net.minecraft.block.IceBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.LightType
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.WireOrientation
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import org.jspecify.annotations.Nullable;

public class FrostedIceBlock
extends IceBlock {
    public static final MapCodec<FrostedIceBlock> CODEC = FrostedIceBlock.createCodec(FrostedIceBlock::new);
    public static final int MAX_AGE = 3;
    public static final IntProperty AGE = Properties.AGE_3;
    private static final int NEIGHBORS_CHECKED_ON_SCHEDULED_TICK = 4;
    private static final int NEIGHBORS_CHECKED_ON_NEIGHBOR_UPDATE = 2;

    public MapCodec<FrostedIceBlock> getCodec() {
        return CODEC;
    }

    public FrostedIceBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)AGE, (Comparable)Integer.valueOf(0)));
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, (Block)this, MathHelper.nextInt((Random)world.getRandom(), (int)60, (int)120));
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(3) == 0 || this.canMelt((BlockView)world, pos, 4)) {
            int i;
            int n = i = world.getRegistryKey() == World.END ? world.getLightLevel(LightType.BLOCK, pos) : world.getLightLevel(pos);
            if (i > 11 - (Integer)state.get((Property)AGE) - state.getOpacity() && this.increaseAge(state, (World)world, pos)) {
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                for (Direction direction : Direction.values()) {
                    mutable.set((Vec3i)pos, direction);
                    BlockState blockState = world.getBlockState((BlockPos)mutable);
                    if (!blockState.isOf((Block)this) || this.increaseAge(blockState, (World)world, (BlockPos)mutable)) continue;
                    world.scheduleBlockTick((BlockPos)mutable, (Block)this, MathHelper.nextInt((Random)random, (int)20, (int)40));
                }
                return;
            }
        }
        world.scheduleBlockTick(pos, (Block)this, MathHelper.nextInt((Random)random, (int)20, (int)40));
    }

    private boolean increaseAge(BlockState state, World world, BlockPos pos) {
        int i = (Integer)state.get((Property)AGE);
        if (i < 3) {
            world.setBlockState(pos, (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(i + 1)), 2);
            return false;
        }
        this.melt(state, world, pos);
        return true;
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (sourceBlock.getDefaultState().isOf((Block)this) && this.canMelt((BlockView)world, pos, 2)) {
            this.melt(state, world, pos);
        }
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
    }

    private boolean canMelt(BlockView world, BlockPos pos, int maxNeighbors) {
        int i = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.values()) {
            mutable.set((Vec3i)pos, direction);
            if (!world.getBlockState((BlockPos)mutable).isOf((Block)this) || ++i < maxNeighbors) continue;
            return false;
        }
        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE});
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return ItemStack.EMPTY;
    }
}

