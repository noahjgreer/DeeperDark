/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.slot.Slot;

class EnchantmentScreenHandler.2
extends Slot {
    EnchantmentScreenHandler.2(EnchantmentScreenHandler enchantmentScreenHandler, Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }
}
