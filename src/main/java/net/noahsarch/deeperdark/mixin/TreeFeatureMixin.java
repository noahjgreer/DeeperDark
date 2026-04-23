package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
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
    private void deeperdark$skipHorizontalChecks(LevelSimulatedReader world, int height, BlockPos pos, TreeConfiguration config, CallbackInfoReturnable<Integer> cir) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        // Only check the center column (vertical checks only)
        for (int y = 0; y <= height + 1; y++) {
            mutable.set(pos.getX(), pos.getY() + y, pos.getZ());

            // Check if the trunk position can be replaced
            if (!config.trunkPlacer.isFree((net.minecraft.world.level.WorldGenLevel) world, mutable)) {
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
    private static boolean isVine(LevelSimulatedReader world, BlockPos pos) {
        return world.isStateAtPosition(pos, state -> state.is(net.minecraft.world.level.block.Blocks.VINE));
    }
}
