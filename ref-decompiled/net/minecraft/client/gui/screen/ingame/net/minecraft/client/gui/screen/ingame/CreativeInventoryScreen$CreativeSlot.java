/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class CreativeInventoryScreen.CreativeSlot
extends Slot {
    final Slot slot;

    public CreativeInventoryScreen.CreativeSlot(Slot slot, int invSlot, int x, int y) {
        super(slot.inventory, invSlot, x, y);
        this.slot = slot;
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.slot.onTakeItem(player, stack);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return this.slot.canInsert(stack);
    }

    @Override
    public ItemStack getStack() {
        return this.slot.getStack();
    }

    @Override
    public boolean hasStack() {
        return this.slot.hasStack();
    }

    @Override
    public void setStack(ItemStack stack, ItemStack previousStack) {
        this.slot.setStack(stack, previousStack);
    }

    @Override
    public void setStackNoCallbacks(ItemStack stack) {
        this.slot.setStackNoCallbacks(stack);
    }

    @Override
    public void markDirty() {
        this.slot.markDirty();
    }

    @Override
    public int getMaxItemCount() {
        return this.slot.getMaxItemCount();
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return this.slot.getMaxItemCount(stack);
    }

    @Override
    public @Nullable Identifier getBackgroundSprite() {
        return this.slot.getBackgroundSprite();
    }

    @Override
    public ItemStack takeStack(int amount) {
        return this.slot.takeStack(amount);
    }

    @Override
    public boolean isEnabled() {
        return this.slot.isEnabled();
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return this.slot.canTakeItems(playerEntity);
    }
}
