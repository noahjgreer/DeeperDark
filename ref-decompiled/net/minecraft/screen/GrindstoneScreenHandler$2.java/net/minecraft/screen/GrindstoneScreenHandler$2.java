/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.slot.Slot;

class GrindstoneScreenHandler.2
extends Slot {
    GrindstoneScreenHandler.2(GrindstoneScreenHandler grindstoneScreenHandler, Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isDamageable() || EnchantmentHelper.hasEnchantments(stack);
    }
}
