package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SnowyBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class PodzolBlockMixin {

    @Unique
    private boolean deeperdark$isPodzol() {
        return ((Object) this) == Blocks.PODZOL;
    }

    @Inject(method = "isRandomlyTicking", at = @At("HEAD"), cancellable = true)
    private void deeperdark$enableRandomTick(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (deeperdark$isPodzol()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void deeperdark$randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
            CallbackInfo ci) {
        if (!deeperdark$isPodzol())
            return;

        if (!deeperdark$canSurvive(state, level, pos)) {
            level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
        } else if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            BlockState podzolDefault = Blocks.PODZOL.defaultBlockState();
            for (int i = 0; i < 4; i++) {
                BlockPos testPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (level.getBlockState(testPos).is(Blocks.DIRT)
                        && deeperdark$canPropagate(podzolDefault, level, testPos)) {
                    BlockState testAboveState = level.getBlockState(testPos.above());
                    level.setBlockAndUpdate(testPos,
                            podzolDefault.setValue(SnowyBlock.SNOWY, deeperdark$isSnowySetting(testAboveState)));
                }
            }
        }
    }

    // Mirrors SpreadingSnowyBlock.canStayAlive: podzol dies if covered by something
    // opaque or waterlogged.
    // Single-layer snow is exempted (it doesn't block enough light to kill the
    // block).
    @Unique
    private static boolean deeperdark$canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        if (aboveState.is(Blocks.SNOW) && aboveState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (aboveState.getFluidState().isFull()) {
            return false;
        } else {
            int lightBlock = LightEngine.getLightBlockInto(state, aboveState, Direction.UP,
                    aboveState.getLightDampening());
            return lightBlock < 15;
        }
    }

    @Unique
    private static boolean deeperdark$canPropagate(BlockState podzolDefault, LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        return deeperdark$canSurvive(podzolDefault, level, pos) && !level.getFluidState(above).is(FluidTags.WATER);
    }

    @Unique
    private static boolean deeperdark$isSnowySetting(BlockState aboveState) {
        return aboveState.is(BlockTags.SNOW);
    }
}
