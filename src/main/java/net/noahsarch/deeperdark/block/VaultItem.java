package net.noahsarch.deeperdark.block;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import net.noahsarch.deeperdark.inventory.ItemBackedVaultEntity;

/**
 * BlockItem for vault blocks. Overrides overrideOtherStackedOnMe to support
 * left-click cursor-insertion (the "+" quick-add feature) while blocking
 * shulker boxes and other vaults from being nested inside.
 */
public class VaultItem extends BlockItem {

    public VaultItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack self, ItemStack other, Slot slot,
            ClickAction clickAction, Player player, SlotAccess carriedAccess) {
        if (clickAction != ClickAction.PRIMARY || other.isEmpty()) return false;
        // Per spec: vaults block other vaults and shulker boxes from being inserted.
        if (ContainerItemUtil.isVaultItem(other)) return false;
        if (other.is(ItemTags.SHULKER_BOXES)) return false;
        // Block insertion when this exact vault item is currently open from inventory.
        for (Slot s : player.containerMenu.slots) {
            if (s.container instanceof ItemBackedVaultEntity ive && ive.isTrackingItem(self)) return false;
        }
        return ContainerItemUtil.tryVaultInsert(self, other, carriedAccess);
    }
}
