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

public class BoxItem extends BlockItem {

    public BoxItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    /** Like shulker boxes: a box in a bundle in a box... would nest without limit. */
    @Override
    public boolean canFitInsideContainerItems() {
        return false;
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
        if (ContainerItemUtil.isOpen(self) || !ContainerItemUtil.canStore(ContainerItemUtil.BOX_TIER, other)) return false;
        int size = ContainerItemUtil.getContainerSize(self);
        return ContainerItemUtil.tryInsert(self, other, carriedAccess, size);
    }
}
