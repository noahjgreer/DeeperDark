package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Items;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getItemEnchantmentLevel", at = @At("RETURN"), cancellable = true)
    private static void deeperdark$implicitSilkTouch(Holder<Enchantment> enchantment, ItemInstance stack, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == 0) {
            if (enchantment.is(Enchantments.SILK_TOUCH)) {
                if (stack.is(Items.GOLDEN_PICKAXE) ||
                    stack.is(Items.GOLDEN_AXE) ||
                    stack.is(Items.GOLDEN_SHOVEL) ||
                    stack.is(Items.GOLDEN_HOE) ||
                    stack.is(Items.GOLDEN_SWORD)) {
                    cir.setReturnValue(1);
                }
            }
        }
    }
}

