/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public static interface LargeEntitySpawnHelper.Requirements {
    @Deprecated
    public static final LargeEntitySpawnHelper.Requirements IRON_GOLEM = (world, pos, state, abovePos, aboveState) -> {
        if (state.isOf(Blocks.COBWEB) || state.isOf(Blocks.CACTUS) || state.isOf(Blocks.GLASS_PANE) || state.getBlock() instanceof StainedGlassPaneBlock || state.getBlock() instanceof StainedGlassBlock || state.getBlock() instanceof LeavesBlock || state.isOf(Blocks.CONDUIT) || state.isOf(Blocks.ICE) || state.isOf(Blocks.TNT) || state.isOf(Blocks.GLOWSTONE) || state.isOf(Blocks.BEACON) || state.isOf(Blocks.SEA_LANTERN) || state.isOf(Blocks.FROSTED_ICE) || state.isOf(Blocks.TINTED_GLASS) || state.isOf(Blocks.GLASS)) {
            return false;
        }
        return !(!aboveState.isAir() && !aboveState.isLiquid() || !state.isSolid() && !state.isOf(Blocks.POWDER_SNOW));
    };
    public static final LargeEntitySpawnHelper.Requirements WARDEN = (world, pos, state, abovePos, aboveState) -> aboveState.getCollisionShape(world, abovePos).isEmpty() && Block.isFaceFullSquare(state.getCollisionShape(world, pos), Direction.UP);
    public static final LargeEntitySpawnHelper.Requirements CREAKING = (world, pos, state, abovePos, aboveState) -> aboveState.getCollisionShape(world, abovePos).isEmpty() && !state.isIn(BlockTags.LEAVES) && Block.isFaceFullSquare(state.getCollisionShape(world, pos), Direction.UP);

    public boolean canSpawnOn(ServerWorld var1, BlockPos var2, BlockState var3, BlockPos var4, BlockState var5);
}
