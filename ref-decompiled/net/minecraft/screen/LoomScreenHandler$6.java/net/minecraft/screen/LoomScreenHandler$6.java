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

class LoomScreenHandler.6
extends Slot {
    final /* synthetic */ ScreenHandlerContext field_17325;

    LoomScreenHandler.6(Inventory inventory, int i, int j, int k, ScreenHandlerContext screenHandlerContext) {
        this.field_17325 = screenHandlerContext;
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        LoomScreenHandler.this.bannerSlot.takeStack(1);
        LoomScreenHandler.this.dyeSlot.takeStack(1);
        if (!LoomScreenHandler.this.bannerSlot.hasStack() || !LoomScreenHandler.this.dyeSlot.hasStack()) {
            LoomScreenHandler.this.selectedPattern.set(-1);
        }
        this.field_17325.run((world, pos) -> {
            long l = world.getTime();
            if (LoomScreenHandler.this.lastTakeResultTime != l) {
                world.playSound(null, (BlockPos)pos, SoundEvents.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                LoomScreenHandler.this.lastTakeResultTime = l;
            }
        });
        super.onTakeItem(player, stack);
    }
}
