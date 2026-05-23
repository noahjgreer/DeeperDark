package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.duck.CollarHolder;
import net.noahsarch.deeperdark.item.CollarItem;
import net.noahsarch.deeperdark.menu.CollarSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends AbstractContainerMenu {

    protected InventoryMenuMixin() { super(null, 0); }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void deeperdark$addCollarSlot(Inventory inventory, boolean onServer, Player player, CallbackInfo ci) {
        if (player instanceof CollarHolder holder) {
            this.addSlot(new CollarSlot(holder, 77, 8));
        }
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void deeperdark$collarQuickMove(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        // Find the collar slot added by our mixin
        CollarSlot collarSlot = null;
        int collarSlotIndex = -1;
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i) instanceof CollarSlot cs) {
                collarSlot = cs;
                collarSlotIndex = i;
                break;
            }
        }
        if (collarSlot == null) return;

        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) return;

        if (slotIndex == collarSlotIndex) {
            // Shift-click FROM collar slot: move to main inventory then hotbar
            ItemStack stack = slot.getItem();
            ItemStack result = stack.copy();
            if (!this.moveItemStackTo(stack, 9, collarSlotIndex, false)) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            cir.setReturnValue(result);
            return;
        }

        // Shift-click a saddle from main inventory/hotbar → HEAD slot (slot 5) if empty
        if (slotIndex >= 9 && slotIndex <= 44 && slot.getItem().is(net.minecraft.world.item.Items.SADDLE)) {
            Slot headSlot = this.slots.get(5);
            if (!headSlot.hasItem()) {
                ItemStack stack = slot.getItem();
                ItemStack result = stack.copy();
                headSlot.setByPlayer(stack.copy());
                slot.setByPlayer(ItemStack.EMPTY);
                cir.setReturnValue(result);
                return;
            }
        }

        // Shift-click a CollarItem from any non-collar slot → move to collar slot if empty
        if (slot.getItem().getItem() instanceof CollarItem && !collarSlot.hasItem()) {
            ItemStack stack = slot.getItem();
            ItemStack result = stack.copy();
            collarSlot.setByPlayer(stack.copy());
            slot.setByPlayer(ItemStack.EMPTY);
            cir.setReturnValue(result);
        }
    }
}
