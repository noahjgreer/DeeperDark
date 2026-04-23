package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getLevel", at = @At("RETURN"), cancellable = true)
    private static void deeperdark$implicitSilkTouch(Holder<Enchantment> enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == 0) {
            if (enchantment.is(Enchantments.SILK_TOUCH)) {
                if (stack.getItem() == Items.GOLDEN_PICKAXE ||
                    stack.getItem() == Items.GOLDEN_AXE ||
                    stack.getItem() == Items.GOLDEN_SHOVEL ||
                    stack.getItem() == Items.GOLDEN_HOE ||
                    stack.getItem() == Items.GOLDEN_SWORD) {
                    cir.setReturnValue(1);
                }
            }
        }
    }
}

