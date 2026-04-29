package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(SugarCaneBlock.class)
public class SugarCaneBlockMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"), method = "canSurvive", locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void allowSugarCaneToGrowOnIce(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState stateBelow, BlockPos below, Iterator<?> iterator, Direction direction, BlockState adjacentBlockState) {
        if (!FabricSeasons.CONFIG.shouldIceBreakSugarCane() && adjacentBlockState.is(Blocks.ICE)) {
            cir.setReturnValue(true);
        }
    }
}
