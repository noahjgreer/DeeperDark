/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.screen.slot.Slot;

class ForgingScreenHandler.2
extends Slot {
    final /* synthetic */ ForgingSlotsManager.ForgingSlot field_54950;

    ForgingScreenHandler.2(ForgingScreenHandler forgingScreenHandler, Inventory inventory, int i, int j, int k, ForgingSlotsManager.ForgingSlot forgingSlot) {
        this.field_54950 = forgingSlot;
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return this.field_54950.mayPlace().test(stack);
    }
}
