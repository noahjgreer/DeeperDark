package net.noahsarch.deeperdark.block;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import net.noahsarch.deeperdark.inventory.ItemBackedContainer;

public class BoxItem extends BlockItem {

    public BoxItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    /**
     * Left-click with cursor item on a box in any inventory slot → insert as many as fit.
     * Mirrors bundle insertion behaviour so the "+" indicator interaction feels native.
     */
    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack self, ItemStack other, Slot slot,
            ClickAction clickAction, Player player, SlotAccess carriedAccess) {
        if (clickAction != ClickAction.SECONDARY || other.isEmpty()) return false;
        // Block nesting containers inside containers.
        if (ContainerItemUtil.getContainerSize(other) >= 0) return false;
        // Block insertion when this exact container item is currently open from inventory.
        for (Slot s : player.containerMenu.slots) {
            if (s.container instanceof ItemBackedContainer ibc && ibc.isTrackingItem(self)) return false;
        }
        int size = ContainerItemUtil.getContainerSize(self);
        return ContainerItemUtil.tryInsert(self, other, carriedAccess, size);
    }
}
