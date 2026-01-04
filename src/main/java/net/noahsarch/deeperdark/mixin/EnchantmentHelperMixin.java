package net.noahsarch.deeperdark.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getLevel", at = @At("RETURN"), cancellable = true)
    private static void deeperdark$implicitSilkTouch(RegistryEntry<Enchantment> enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == 0) {
            if (enchantment.matchesKey(Enchantments.SILK_TOUCH)) {
                if (stack.isOf(Items.GOLDEN_PICKAXE) ||
                    stack.isOf(Items.GOLDEN_AXE) ||
                    stack.isOf(Items.GOLDEN_SHOVEL) ||
                    stack.isOf(Items.GOLDEN_HOE) ||
                    stack.isOf(Items.GOLDEN_SWORD)) {
                    cir.setReturnValue(1);
                }
            }
        }
    }
}

