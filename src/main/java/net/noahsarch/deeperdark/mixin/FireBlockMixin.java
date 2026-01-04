package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Inject(method = "scheduledTick", at = @At("HEAD"))
    private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.getBlockState(pos.down()).isOf(Blocks.GRASS_BLOCK)) {
            // 10% chance to turn into coarse dirt
            if (random.nextInt(10) == 0) {
                world.setBlockState(pos.down(), Blocks.COARSE_DIRT.getDefaultState());
            }
        }
    }
}

