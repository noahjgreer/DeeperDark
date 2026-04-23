package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PortalShape.class)
public class NetherPortalMixin {
    @ModifyConstant(
        method = "calculateWidth(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I",
        constant = @Constant(intValue = 2)
    )
    private static int modifyMinWidth(int original) {
        return 1;
    }

    @ModifyConstant(
        method = "calculateHeight(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;ILorg/apache/commons/lang3/mutable/MutableInt;)I",
        constant = @Constant(intValue = 3)
    )
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
