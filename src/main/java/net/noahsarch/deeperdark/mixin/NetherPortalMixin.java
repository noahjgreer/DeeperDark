package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

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


    // Redirect ContextPredicate.test calls inside NetherPortal so crying obsidian is accepted as a valid frame block.
    // Signature of the target: net.minecraft.block.AbstractBlock$ContextPredicate.test(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z

    @Redirect(method = "getWidth", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$ContextPredicate;test(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
    private static boolean redirect_getWidth_test(AbstractBlock.ContextPredicate predicate, BlockState state, BlockView world, BlockPos pos) {
        return predicate.test(state, world, pos) || state.isOf(Blocks.CRYING_OBSIDIAN);
    }

    @Redirect(method = "isHorizontalFrameValid", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$ContextPredicate;test(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
    private static boolean redirect_isHorizontalFrameValid_test(AbstractBlock.ContextPredicate predicate, BlockState state, BlockView world, BlockPos pos) {
        return predicate.test(state, world, pos) || state.isOf(Blocks.CRYING_OBSIDIAN);
    }

    @Redirect(method = "getPotentialHeight", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$ContextPredicate;test(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
    private static boolean redirect_getPotentialHeight_test(AbstractBlock.ContextPredicate predicate, BlockState state, BlockView world, BlockPos pos) {
        return predicate.test(state, world, pos) || state.isOf(Blocks.CRYING_OBSIDIAN);
    }
}