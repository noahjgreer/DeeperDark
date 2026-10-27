package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.noahsarch.deeperdark.block.GlassDoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronBarsBlock.class)
public class IronBarsBlockMixin {

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void deeperdark$connectToGlassDoors(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState neighbour = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
            if (GlassDoorBlock.hugsPane(neighbour, direction.getOpposite())) {
                state = state.setValue(CrossCollisionBlock.PROPERTY_BY_DIRECTION.get(direction), true);
            }
        }
        cir.setReturnValue(state);
    }

    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    private void deeperdark$connectToGlassDoors(BlockState state, LevelReader level, ScheduledTickAccess ticks,
            BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState,
            RandomSource random, CallbackInfoReturnable<BlockState> cir) {
        if (GlassDoorBlock.hugsPane(neighbourState, directionToNeighbour.getOpposite())) {
            cir.setReturnValue(cir.getReturnValue().setValue(CrossCollisionBlock.PROPERTY_BY_DIRECTION.get(directionToNeighbour), true));
        }
    }
}
