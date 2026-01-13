/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.InventoryChangedCriterion
 *  net.minecraft.advancement.criterion.InventoryChangedCriterion$Conditions
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryChangedCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack) {
        int i = 0;
        int j = 0;
        int k = 0;
        for (int l = 0; l < inventory.size(); ++l) {
            ItemStack itemStack = inventory.getStack(l);
            if (itemStack.isEmpty()) {
                ++j;
                continue;
            }
            ++k;
            if (itemStack.getCount() < itemStack.getMaxCount()) continue;
            ++i;
        }
        this.trigger(player, inventory, stack, i, j, k);
    }

    private void trigger(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack, int full, int empty, int occupied) {
        this.trigger(player, conditions -> conditions.matches(inventory, stack, full, empty, occupied));
    }
}

