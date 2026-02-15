package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.piston.PistonHandler;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixin to make the piston push limit configurable.
 * Vanilla default is 12 blocks.
 * The constant 12 appears 3 times in the tryMove method.
 */
@Mixin(PistonHandler.class)
public class PistonHandlerMixin {

    /**
     * Modify all occurrences of constant 12 in tryMove method.
     * This affects the piston push limit checks.
     */
    @ModifyConstant(method = "tryMove", constant = @Constant(intValue = 12))
    private int deeperdark$modifyPushLimit(int original) {
        return DeeperDarkConfig.get().pistonPushLimit;
    }
}
