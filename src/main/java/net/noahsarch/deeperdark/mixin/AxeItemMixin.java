package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AxeItem.class)
public class AxeItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void onUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        Map<Block, Block> strippedBlocks = AxeItemAccessor.getStrippedBlocks();
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        if (strippedBlocks.containsKey(state.getBlock())) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}

