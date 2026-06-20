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

@Mixin(LadderBlock.class)
public class LadderBlockMixin {

    @Unique
    private static boolean deeperdark$isSameFacingLadder(BlockState state, Direction facing) {
        return state.is(Blocks.LADDER) && state.getValue(LadderBlock.FACING) == facing;
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

        // The block the player clicked is on the opposite side of the placement position.
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
     * exists directly above or below it.
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
     * Cascade-breaks vertically-chained ladders when vertical support is removed.
     * Returning AIR from updateShape triggers recursive neighbor updates through the chain,
     * matching the immediate-removal pattern already used by vanilla LadderBlock for wall removal.
     */
    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    private void deeperdark$updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState,
            RandomSource random, CallbackInfoReturnable<BlockState> cir) {
        if (directionToNeighbour != Direction.UP && directionToNeighbour != Direction.DOWN) return;
        // Only act if vanilla hasn't already decided to remove the block,
        // and the ladder can no longer survive (no wall support AND no vertical support).
        if (!cir.getReturnValue().isAir() && !state.canSurvive(level, pos)) {
            cir.setReturnValue(Blocks.AIR.defaultBlockState());
        }
    }
}
