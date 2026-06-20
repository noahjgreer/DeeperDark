package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(LadderBlock.class)
public class LadderBlockMixin {

    @Unique
    private static boolean deeperdark$isSameFacingLadder(BlockState state, Direction facing) {
        return state.is(Blocks.LADDER) && state.getValue(LadderBlock.FACING) == facing;
    }

    /**
     * Collects all same-facing ladders vertically connected to pos (up and down), capped at 256.
     */
    @Unique
    private static List<BlockPos> deeperdark$collectChain(LevelReader level, BlockPos start, Direction facing) {
        List<BlockPos> chain = new ArrayList<>();
        chain.add(start);

        BlockPos cur = start.above();
        while (chain.size() < 256 && deeperdark$isSameFacingLadder(level.getBlockState(cur), facing)) {
            chain.add(cur);
            cur = cur.above();
        }

        cur = start.below();
        while (chain.size() < 256 && deeperdark$isSameFacingLadder(level.getBlockState(cur), facing)) {
            chain.add(cur);
            cur = cur.below();
        }

        return chain;
    }

    /**
     * Allows placing a ladder on the top or bottom face of an existing ladder,
     * automatically inheriting the same facing direction.
     */
    @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
    private void deeperdark$getStateForPlacement(BlockPlaceContext context,
            CallbackInfoReturnable<BlockState> cir) {
        Direction clickedFace = context.getClickedFace();
        if (clickedFace != Direction.UP && clickedFace != Direction.DOWN) return;

        BlockPos existingPos = context.getClickedPos().relative(clickedFace.getOpposite());
        BlockState existing = context.getLevel().getBlockState(existingPos);
        if (!existing.is(Blocks.LADDER)) return;

        Direction facing = existing.getValue(LadderBlock.FACING);
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        cir.setReturnValue(((Block)(Object)this).defaultBlockState()
                .setValue(LadderBlock.FACING, facing)
                .setValue(LadderBlock.WATERLOGGED, fluidState.is(Fluids.WATER)));
    }

    /**
     * Allows a ladder to survive without wall support if a same-facing ladder
     * exists directly above or below it. Used for placement validation and to
     * prevent vanilla's updateShape from immediately removing a vertically-supported ladder
     * when its wall neighbor changes.
     */
    @Inject(method = "canSurvive", at = @At("RETURN"), cancellable = true)
    private void deeperdark$canSurvive(BlockState state, LevelReader level, BlockPos pos,
            CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        Direction facing = state.getValue(LadderBlock.FACING);
        if (deeperdark$isSameFacingLadder(level.getBlockState(pos.above()), facing)
                || deeperdark$isSameFacingLadder(level.getBlockState(pos.below()), facing)) {
            cir.setReturnValue(true);
        }
    }

    /**
     * Sweeping chain-validity check on vertical and wall-direction neighbor changes.
     *
     * Instead of checking only direct neighbors (which leaves floating mid-chain segments
     * when a middle block is broken), this traverses the entire connected chain. If no block
     * in the chain is wall-anchored, this block returns AIR, which triggers neighbor updates
     * on the next block in the chain — cascading the break through all disconnected members.
     *
     * Handles three relevant directions:
     *   UP/DOWN — a ladder in the chain was added or removed vertically
     *   facing.getOpposite() — the wall behind this ladder changed; vanilla already handles
     *     the "wall removed, no vertical support" case (returns AIR). We handle the remaining
     *     case where vertical support keeps canSurvive true but the chain is now anchorless.
     */
    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    private void deeperdark$updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState,
            RandomSource random, CallbackInfoReturnable<BlockState> cir) {
        // Skip if vanilla already decided to remove this block.
        if (cir.getReturnValue().isAir()) return;

        Direction facing = state.getValue(LadderBlock.FACING);
        if (directionToNeighbour != Direction.UP
                && directionToNeighbour != Direction.DOWN
                && directionToNeighbour != facing.getOpposite()) return;

        List<BlockPos> chain = deeperdark$collectChain(level, pos, facing);
        for (BlockPos p : chain) {
            BlockPos wallPos = p.relative(facing.getOpposite());
            if (level.getBlockState(wallPos).isFaceSturdy(level, wallPos, facing)) {
                return; // Chain is wall-anchored — stable.
            }
        }

        // No wall anchor anywhere in the chain: remove this block.
        // The resulting neighbor update will cascade this check to the next block.
        cir.setReturnValue(Blocks.AIR.defaultBlockState());
    }

}
