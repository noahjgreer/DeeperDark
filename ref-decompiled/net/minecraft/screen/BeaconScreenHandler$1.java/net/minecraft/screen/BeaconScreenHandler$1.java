/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.BeaconScreenHandler;

class BeaconScreenHandler.1
extends SimpleInventory {
    BeaconScreenHandler.1(BeaconScreenHandler beaconScreenHandler, int i) {
        super(i);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.isIn(ItemTags.BEACON_PAYMENT_ITEMS);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }
}
