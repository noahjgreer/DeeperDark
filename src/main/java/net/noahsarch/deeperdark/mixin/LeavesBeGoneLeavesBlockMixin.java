package net.noahsarch.deeperdark.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.ported.LeavesDistanceHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LeavesBlock.class)
public abstract class LeavesBeGoneLeavesBlockMixin extends Block {

    public LeavesBeGoneLeavesBlockMixin(Properties properties) {
        super(properties);
    }

    @ModifyExpressionValue(
            method = "updateShape",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LeavesBlock;getDistanceAt(Lnet/minecraft/world/level/block/state/BlockState;)I")
    )
    private int deeperdark$updateShapeDistance(int distanceAt, BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (!DeeperDarkConfig.get().leavesBeGoneIgnoreOtherLeafTypes) return distanceAt;
        return LeavesDistanceHelper.updateDistance(state, neighbourState, distanceAt);
    }

    @ModifyExpressionValue(
            method = "updateDistance",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LeavesBlock;getDistanceAt(Lnet/minecraft/world/level/block/state/BlockState;)I")
    )
    private static int deeperdark$updateDistanceNeighbor(int distanceAt, BlockState state, LevelAccessor level, BlockPos pos, @Local BlockPos.MutableBlockPos mutableBlockPos) {
        if (!DeeperDarkConfig.get().leavesBeGoneIgnoreOtherLeafTypes) return distanceAt;
        return LeavesDistanceHelper.updateDistance(state, level.getBlockState(mutableBlockPos), distanceAt);
    }
}
