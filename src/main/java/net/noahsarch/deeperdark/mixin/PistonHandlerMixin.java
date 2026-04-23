package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixin to make the piston push limit configurable.
 * Vanilla default is 12 blocks.
 * The constant 12 appears in addBlockLine in current mappings.
 */
@Mixin(PistonStructureResolver.class)
public class PistonHandlerMixin {

    /**
     * Modify all occurrences of constant 12 in addBlockLine.
     * This affects the piston push limit checks.
     */
    @ModifyConstant(
        method = "addBlockLine(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
        constant = @Constant(intValue = 12)
    )
    private int deeperdark$modifyPushLimit(int original) {
        return DeeperDarkConfig.get().pistonPushLimit;
    }
}
