/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.slot.Slot;

class LoomScreenHandler.3
extends Slot {
    LoomScreenHandler.3(LoomScreenHandler loomScreenHandler, Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof BannerItem;
    }
}
