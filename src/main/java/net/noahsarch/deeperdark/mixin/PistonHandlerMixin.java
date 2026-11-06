package net.noahsarch.deeperdark.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Makes the piston push limit (vanilla 12, used three times in addBlockLine) configurable.
 */
@Mixin(PistonStructureResolver.class)
public class PistonHandlerMixin {

    // Not @ModifyConstant: that consumes the constant and crashes Carpet's own pushLimit mixin on it.
    @ModifyExpressionValue(
        method = "addBlockLine(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
        at = @At(value = "CONSTANT", args = "intValue=12")
    )
    private int deeperdark$modifyPushLimit(int original) {
        return DeeperDarkConfig.get().pistonPushLimit;
    }
}
