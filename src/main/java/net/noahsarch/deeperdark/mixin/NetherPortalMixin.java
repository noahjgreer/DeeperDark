package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PortalShape.class)
public class NetherPortalMixin {
    @ModifyConstant(method = "getValidatedWidth", constant = @Constant(intValue = 2))
    private static int modifyMinWidth(int original) {
        return 1;
    }

    @ModifyConstant(method = "getHeight", constant = @Constant(intValue = 3))
    private static int modifyMinHeight(int original) {
        return 1;
    }

    @ModifyConstant(method = "isValid", constant = @Constant(intValue = 2))
    private int modifyIsValidMinWidth(int original) {
        return 1;
    }

    @ModifyConstant(method = "isValid", constant = @Constant(intValue = 3))
    private int modifyIsValidMinHeight(int original) {
        return 1;
    }
}
