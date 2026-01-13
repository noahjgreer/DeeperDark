/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.slot.Slot;

class CartographyTableScreenHandler.4
extends Slot {
    CartographyTableScreenHandler.4(CartographyTableScreenHandler cartographyTableScreenHandler, Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isOf(Items.PAPER) || stack.isOf(Items.MAP) || stack.isOf(Items.GLASS_PANE);
    }
}
