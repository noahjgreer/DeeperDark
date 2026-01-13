/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.slot.Slot;

class CartographyTableScreenHandler.3
extends Slot {
    CartographyTableScreenHandler.3(CartographyTableScreenHandler cartographyTableScreenHandler, Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.contains(DataComponentTypes.MAP_ID);
    }
}
