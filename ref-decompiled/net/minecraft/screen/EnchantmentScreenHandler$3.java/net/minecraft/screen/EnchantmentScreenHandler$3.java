/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

class EnchantmentScreenHandler.3
extends Slot {
    EnchantmentScreenHandler.3(EnchantmentScreenHandler enchantmentScreenHandler, Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isOf(Items.LAPIS_LAZULI);
    }

    @Override
    public Identifier getBackgroundSprite() {
        return EMPTY_LAPIS_LAZULI_SLOT_TEXTURE;
    }
}
