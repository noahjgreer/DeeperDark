/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

class CartographyTableScreenHandler.5
extends Slot {
    final /* synthetic */ ScreenHandlerContext field_17301;

    CartographyTableScreenHandler.5(Inventory inventory, int i, int j, int k, ScreenHandlerContext screenHandlerContext) {
        this.field_17301 = screenHandlerContext;
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        ((Slot)CartographyTableScreenHandler.this.slots.get(0)).takeStack(1);
        ((Slot)CartographyTableScreenHandler.this.slots.get(1)).takeStack(1);
        stack.getItem().onCraftByPlayer(stack, player);
        this.field_17301.run((world, pos) -> {
            long l = world.getTime();
            if (CartographyTableScreenHandler.this.lastTakeResultTime != l) {
                world.playSound(null, (BlockPos)pos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                CartographyTableScreenHandler.this.lastTakeResultTime = l;
            }
        });
        super.onTakeItem(player, stack);
    }
}
