package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortal.class)
public class NetherPortalMixin {
    @ModifyConstant(method = "getValidatedWidth", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 2))
    private static int modifyMinWidth(int original) {
        return 1;
    }

    @ModifyConstant(method = "getHeight", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 3))
    private static int modifyMinHeight(int original) {
        return 1;
    }

    @ModifyConstant(method = "isValid", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 2))
    private int modifyIsValidMinWidth(int original) {
        return 1;
    }

    @ModifyConstant(method = "isValid", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 3))
    private int modifyIsValidMinHeight(int original) {
        return 1;
    }

    @Inject(method = "createPortal", at = @At("HEAD"), cancellable = true)
    private void onlyLightBelowMinus64(WorldAccess world, CallbackInfo ci) {
        BlockPos lowerCorner = ((NetherPortalAccessor)(Object)this).getLowerCorner();
        if (world.getDimension().bedWorks() && lowerCorner.getY() > -64) {
            ci.cancel();
        }
    }
}