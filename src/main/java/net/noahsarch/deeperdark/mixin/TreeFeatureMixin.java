package net.noahsarch.deeperdark.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to remove horizontal (X and Z) space checks for tree growth.
 * Trees will only check vertical space requirements, allowing saplings
 * to grow even when placed directly next to each other or near blocks.
 */
@Mixin(TreeFeature.class)
public class TreeFeatureMixin {

    /**
     * Inject at the head of getTopPosition to override the horizontal checking behavior.
     * Instead of checking blocks in a radius at each height level, we only check
     * the center column (the trunk position).
     */
    @Inject(method = "getTopPosition", at = @At("HEAD"), cancellable = true)
    private void deeperdark$skipHorizontalChecks(TestableWorld world, int height, BlockPos pos, TreeFeatureConfig config, CallbackInfoReturnable<Integer> cir) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        // Only check the center column (vertical checks only)
        for (int y = 0; y <= height + 1; y++) {
            mutable.set(pos.getX(), pos.getY() + y, pos.getZ());

            // Check if the trunk position can be replaced
            if (!config.trunkPlacer.canReplaceOrIsLog(world, mutable)) {
                // If we can't place at this vertical position, return adjusted height
                cir.setReturnValue(y - 2);
                return;
            }

            // Also check for vines if needed
            if (!config.ignoreVines && isVine(world, mutable)) {
                cir.setReturnValue(y - 2);
                return;
            }
        }

        // All vertical checks passed, return full height
        cir.setReturnValue(height);
    }

    /**
     * Helper method to check for vines (mirrors TreeFeature.isVine)
     */
    @Unique
    private static boolean isVine(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isOf(net.minecraft.block.Blocks.VINE));
    }
}
