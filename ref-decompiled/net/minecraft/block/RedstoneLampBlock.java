/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.RedstoneLampBlock
 *  net.minecraft.block.RedstoneTorchBlock
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.block.WireOrientation
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jspecify.annotations.Nullable;

public class RedstoneLampBlock
extends Block {
    public static final MapCodec<RedstoneLampBlock> CODEC = RedstoneLampBlock.createCodec(RedstoneLampBlock::new);
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public MapCodec<RedstoneLampBlock> getCodec() {
        return CODEC;
    }

    public RedstoneLampBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with((Property)LIT, (Comparable)Boolean.valueOf(false)));
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)LIT, (Comparable)Boolean.valueOf(ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())));
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isClient()) {
            return;
        }
        boolean bl = (Boolean)state.get((Property)LIT);
        if (bl != world.isReceivingRedstonePower(pos)) {
            if (bl) {
                world.scheduleBlockTick(pos, (Block)this, 4);
            } else {
                world.setBlockState(pos, (BlockState)state.cycle((Property)LIT), 2);
            }
        }
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (((Boolean)state.get((Property)LIT)).booleanValue() && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, (BlockState)state.cycle((Property)LIT), 2);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LIT});
    }
}

