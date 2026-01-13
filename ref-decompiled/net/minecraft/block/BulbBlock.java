/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BulbBlock
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.World
 *  net.minecraft.world.block.WireOrientation
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jspecify.annotations.Nullable;

public class BulbBlock
extends Block {
    public static final MapCodec<BulbBlock> CODEC = BulbBlock.createCodec(BulbBlock::new);
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty LIT = Properties.LIT;

    protected MapCodec<? extends BulbBlock> getCodec() {
        return CODEC;
    }

    public BulbBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with((Property)LIT, (Comparable)Boolean.valueOf(false))).with((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.getBlock() != state.getBlock() && world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            this.update(state, serverWorld, pos);
        }
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            this.update(state, serverWorld, pos);
        }
    }

    public void update(BlockState state, ServerWorld world, BlockPos pos) {
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl == (Boolean)state.get((Property)POWERED)) {
            return;
        }
        BlockState blockState = state;
        if (!((Boolean)state.get((Property)POWERED)).booleanValue()) {
            world.playSound(null, pos, (Boolean)(blockState = (BlockState)blockState.cycle((Property)LIT)).get((Property)LIT) != false ? SoundEvents.BLOCK_COPPER_BULB_TURN_ON : SoundEvents.BLOCK_COPPER_BULB_TURN_OFF, SoundCategory.BLOCKS);
        }
        world.setBlockState(pos, (BlockState)blockState.with((Property)POWERED, (Comparable)Boolean.valueOf(bl)), 3);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LIT, POWERED});
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return (Boolean)world.getBlockState(pos).get((Property)LIT) != false ? 15 : 0;
    }
}

