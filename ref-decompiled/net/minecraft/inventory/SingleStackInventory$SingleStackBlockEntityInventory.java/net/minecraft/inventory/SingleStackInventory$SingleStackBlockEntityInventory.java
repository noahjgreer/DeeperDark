/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;

public static interface SingleStackInventory.SingleStackBlockEntityInventory
extends SingleStackInventory {
    public BlockEntity asBlockEntity();

    @Override
    default public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this.asBlockEntity(), player);
    }
}
