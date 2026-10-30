package net.noahsarch.deeperdark.mixin;

import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.minecraft.world.inventory.LoomMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LoomMenu.class)
public class LoomScreenHandlerMixin {
    @ModifyConstant(method = "slotsChanged", constant = @Constant(intValue = 6))
    private int increasePatternLimit(int original) {
        return DeeperDarkConfig.get().unlimitedLoomPatterns ? Integer.MAX_VALUE : original;
    }
}

