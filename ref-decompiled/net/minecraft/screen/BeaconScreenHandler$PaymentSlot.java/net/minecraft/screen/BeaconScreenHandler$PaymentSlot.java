/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.Slot;

static class BeaconScreenHandler.PaymentSlot
extends Slot {
    public BeaconScreenHandler.PaymentSlot(Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isIn(ItemTags.BEACON_PAYMENT_ITEMS);
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }
}
