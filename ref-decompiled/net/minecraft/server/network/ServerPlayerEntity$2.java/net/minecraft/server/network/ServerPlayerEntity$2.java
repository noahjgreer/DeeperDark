/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;

class ServerPlayerEntity.2
implements ScreenHandlerListener {
    ServerPlayerEntity.2() {
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        Slot slot = handler.getSlot(slotId);
        if (slot instanceof CraftingResultSlot) {
            return;
        }
        if (slot.inventory == ServerPlayerEntity.this.getInventory()) {
            Criteria.INVENTORY_CHANGED.trigger(ServerPlayerEntity.this, ServerPlayerEntity.this.getInventory(), stack);
        }
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
    }
}
