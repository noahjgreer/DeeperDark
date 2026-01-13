/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

@Environment(value=EnvType.CLIENT)
static class CreativeInventoryScreen.LockableSlot
extends Slot {
    public CreativeInventoryScreen.LockableSlot(Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        ItemStack itemStack = this.getStack();
        if (super.canTakeItems(playerEntity) && !itemStack.isEmpty()) {
            return itemStack.isItemEnabled(playerEntity.getEntityWorld().getEnabledFeatures()) && !itemStack.contains(DataComponentTypes.CREATIVE_SLOT_LOCK);
        }
        return itemStack.isEmpty();
    }
}
