package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {

    @Shadow private Container enchantSlots;

    @Inject(method = "slotsChanged", at = @At("HEAD"))
    private void deeperdark$addShearsEnchantable(Container container, CallbackInfo ci) {
        if (container == this.enchantSlots) {
            ItemStack stack = container.getItem(0);
            if (stack.is(Items.SHEARS) && !stack.has(DataComponents.ENCHANTABLE)) {
                stack.set(DataComponents.ENCHANTABLE, new Enchantable(15));
            }
        }
    }

    @Inject(method = "slotsChanged", at = @At("RETURN"))
    private void deeperdark$removeShearsEnchantable(Container container, CallbackInfo ci) {
        if (container == this.enchantSlots) {
            ItemStack stack = container.getItem(0);
            if (stack.is(Items.SHEARS)) {
                stack.remove(DataComponents.ENCHANTABLE);
            }
        }
    }
}
