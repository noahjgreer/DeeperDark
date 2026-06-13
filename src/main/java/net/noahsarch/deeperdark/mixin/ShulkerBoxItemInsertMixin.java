package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import net.noahsarch.deeperdark.inventory.ItemBackedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Handles left-click cursor-insertion for vanilla shulker boxes and ender chests.
 * Custom boxes use BoxItem.overrideOtherStackedOnMe; vaults use VaultItem.overrideOtherStackedOnMe.
 */
@Mixin(Item.class)
public class ShulkerBoxItemInsertMixin {

    @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
    private void deeperdark$insertIntoContainerItem(
            ItemStack self, ItemStack other, Slot slot,
            ClickAction clickAction, Player player, SlotAccess carriedAccess,
            CallbackInfoReturnable<Boolean> cir) {

        boolean isShulker    = self.is(ItemTags.SHULKER_BOXES);
        boolean isEnderChest = self.is(Items.ENDER_CHEST);
        if (!isShulker && !isEnderChest) return;

        if (clickAction != ClickAction.SECONDARY || other.isEmpty()) return;

        // Block insertion when this item is currently open from inventory (UUID marker present).
        CustomData selfData = self.get(DataComponents.CUSTOM_DATA);
        if (selfData != null && selfData.copyTag().contains(ItemBackedContainer.OPEN_MARKER_KEY)) return;

        if (isShulker) {
            // Per spec: shulker boxes only block other shulker boxes from being inserted.
            if (other.is(ItemTags.SHULKER_BOXES)) return;
            if (ContainerItemUtil.tryInsert(self, other, carriedAccess, 27)) {
                cir.setReturnValue(true);
            }

        } else {
            // Ender chest: insert cursor into the player's personal ender chest inventory.
            // This is server-only — the client gets an update via the server's response.
            if (player.level().isClientSide()) return;
            // Don't intercept ender-chest-on-ender-chest — let vanilla stacking handle it.
            if (other.is(Items.ENDER_CHEST)) return;
            if (other.is(ItemTags.SHULKER_BOXES)) return;
            if (ContainerItemUtil.isVaultItem(other)) return;

            var ec = player.getEnderChestInventory();
            ItemStack remaining = other.copy();

            // Pass 1: merge into existing matching stacks
            for (int i = 0; i < ec.getContainerSize() && !remaining.isEmpty(); i++) {
                ItemStack slot2 = ec.getItem(i);
                if (!slot2.isEmpty() && ItemStack.isSameItemSameComponents(slot2, remaining)) {
                    int space = slot2.getMaxStackSize() - slot2.getCount();
                    int amount = Math.min(space, remaining.getCount());
                    if (amount > 0) {
                        slot2.grow(amount);
                        remaining.shrink(amount);
                    }
                }
            }
            // Pass 2: fill empty slots
            for (int i = 0; i < ec.getContainerSize() && !remaining.isEmpty(); i++) {
                if (ec.getItem(i).isEmpty()) {
                    ec.setItem(i, remaining.copyWithCount(remaining.getCount()));
                    remaining.setCount(0);
                }
            }

            ec.setChanged();
            carriedAccess.set(remaining.isEmpty() ? ItemStack.EMPTY : remaining);
            cir.setReturnValue(true);
        }
    }
}
