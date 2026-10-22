package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin to remove horizontal (X and Z) space checks for tree growth.
 * Trees will only check vertical space requirements, allowing saplings
 * to grow even when placed directly next to each other or near blocks.
 */
@Mixin(TreeFeature.class)
public class TreeFeatureMixin {

    /**
     * getMaxFreeTreeHeight checks a square of this radius at each height; a radius of 0
     * leaves only the trunk column.
     */
    @Redirect(method = "getMaxFreeTreeHeight", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/feature/featuresize/FeatureSize;getSizeAtHeight(II)I"))
    private int deeperdark$onlyCheckTrunkColumn(FeatureSize size, int treeHeight, int yo) {
        return 0;
    }
}
