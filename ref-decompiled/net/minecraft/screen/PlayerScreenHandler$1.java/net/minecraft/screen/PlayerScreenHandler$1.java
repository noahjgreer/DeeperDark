/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

class PlayerScreenHandler.1
extends Slot {
    final /* synthetic */ PlayerEntity field_39410;

    PlayerScreenHandler.1(PlayerScreenHandler playerScreenHandler, Inventory inventory, int i, int j, int k, PlayerEntity playerEntity) {
        this.field_39410 = playerEntity;
        super(inventory, i, j, k);
    }

    @Override
    public void setStack(ItemStack stack, ItemStack previousStack) {
        this.field_39410.onEquipStack(EquipmentSlot.OFFHAND, previousStack, stack);
        super.setStack(stack, previousStack);
    }

    @Override
    public Identifier getBackgroundSprite() {
        return EMPTY_OFF_HAND_SLOT_TEXTURE;
    }
}
