/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.shorts.Short2BooleanMap
 *  it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 */
package net.minecraft.fluid;

import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

protected class FlowableFluid.SpreadCache {
    private final BlockView world;
    private final BlockPos startPos;
    private final Short2ObjectMap<BlockState> stateCache = new Short2ObjectOpenHashMap();
    private final Short2BooleanMap flowDownCache = new Short2BooleanOpenHashMap();

    FlowableFluid.SpreadCache(BlockView world, BlockPos startPos) {
        this.world = world;
        this.startPos = startPos;
    }

    public BlockState getBlockState(BlockPos pos) {
        return this.getBlockState(pos, this.pack(pos));
    }

    private BlockState getBlockState(BlockPos pos, short packed) {
        return (BlockState)this.stateCache.computeIfAbsent(packed, packedPos -> this.world.getBlockState(pos));
    }

    public boolean canFlowDownTo(BlockPos pos) {
        return this.flowDownCache.computeIfAbsent(this.pack(pos), packed -> {
            BlockState blockState = this.getBlockState(pos, packed);
            BlockPos blockPos2 = pos.down();
            BlockState blockState2 = this.world.getBlockState(blockPos2);
            return FlowableFluid.this.canFlowDownTo(this.world, pos, blockState, blockPos2, blockState2);
        });
    }

    private short pack(BlockPos pos) {
        int i = pos.getX() - this.startPos.getX();
        int j = pos.getZ() - this.startPos.getZ();
        return (short)((i + 128 & 0xFF) << 8 | j + 128 & 0xFF);
    }
}
