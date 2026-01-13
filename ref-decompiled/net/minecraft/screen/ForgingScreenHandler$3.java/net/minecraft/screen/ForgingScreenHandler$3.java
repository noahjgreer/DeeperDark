/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

class ForgingScreenHandler.3
extends Slot {
    ForgingScreenHandler.3(Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return ForgingScreenHandler.this.canTakeOutput(playerEntity, this.hasStack());
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        ForgingScreenHandler.this.onTakeOutput(player, stack);
    }
}
