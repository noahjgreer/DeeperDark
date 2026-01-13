/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

class StonecutterScreenHandler.2
extends Slot {
    final /* synthetic */ ScreenHandlerContext field_17638;

    StonecutterScreenHandler.2(Inventory inventory, int i, int j, int k, ScreenHandlerContext screenHandlerContext) {
        this.field_17638 = screenHandlerContext;
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        stack.onCraftByPlayer(player, stack.getCount());
        StonecutterScreenHandler.this.output.unlockLastRecipe(player, this.getInputStacks());
        ItemStack itemStack = StonecutterScreenHandler.this.inputSlot.takeStack(1);
        if (!itemStack.isEmpty()) {
            StonecutterScreenHandler.this.populateResult(StonecutterScreenHandler.this.selectedRecipe.get());
        }
        this.field_17638.run((world, pos) -> {
            long l = world.getTime();
            if (StonecutterScreenHandler.this.lastTakeTime != l) {
                world.playSound(null, (BlockPos)pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                StonecutterScreenHandler.this.lastTakeTime = l;
            }
        });
        super.onTakeItem(player, stack);
    }

    private List<ItemStack> getInputStacks() {
        return List.of(StonecutterScreenHandler.this.inputSlot.getStack());
    }
}
