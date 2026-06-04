package net.noahsarch.deeperdark.mixin;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import net.noahsarch.deeperdark.inventory.ItemBackedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds left-click-to-insert behaviour to vanilla shulker boxes.
 * BoxItem handles our custom boxes via method override; this mixin covers
 * vanilla BlockItem instances (all shulker box colours).
 */
@Mixin(Item.class)
public class ShulkerBoxItemInsertMixin {

    @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
    private void deeperdark$insertIntoShulkerBox(
            ItemStack self, ItemStack other, Slot slot,
            ClickAction clickAction, Player player, SlotAccess carriedAccess,
            CallbackInfoReturnable<Boolean> cir) {
        if (!self.is(ItemTags.SHULKER_BOXES)) return;
        if (clickAction != ClickAction.PRIMARY || other.isEmpty()) return;
        // Block nesting containers inside containers.
        if (ContainerItemUtil.getContainerSize(other) >= 0) return;
        // Block insertion when this exact container item is currently open from inventory.
        for (Slot s : player.containerMenu.slots) {
            if (s.container instanceof ItemBackedContainer ibc && ibc.isTrackingItem(self)) return;
        }
        if (ContainerItemUtil.tryInsert(self, other, carriedAccess, 27)) {
            cir.setReturnValue(true);
        }
    }
}
