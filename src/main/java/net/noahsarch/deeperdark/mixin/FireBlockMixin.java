package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onScheduledTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (world.getBlockState(pos.below()).getBlock() == Blocks.GRASS_BLOCK) {
            // 10% chance to turn into coarse dirt
            if (random.nextInt(10) == 0) {
                world.setBlock(pos.below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
            }
        }
    }
}

