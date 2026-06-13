package net.noahsarch.deeperdark.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.noahsarch.deeperdark.component.ModComponents;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import net.noahsarch.deeperdark.inventory.ItemBackedVaultEntity;

import java.util.List;
import java.util.function.Consumer;

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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        List<VaultBlockEntity.VaultEntry> entries = stack.get(ModComponents.VAULT_ENTRIES);
        if (entries == null || entries.isEmpty()) {
            tooltip.accept(Component.translatable("item.deeperdark.vault.empty").withStyle(ChatFormatting.GRAY));
            return;
        }
        for (VaultBlockEntity.VaultEntry e : entries) {
            String count = String.format("%,d", e.count);
            tooltip.accept(Component.literal(count + "× ").withStyle(ChatFormatting.GRAY)
                .append(e.representative.getHoverName().copy().withStyle(ChatFormatting.WHITE)));
        }
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack self, ItemStack other, Slot slot,
            ClickAction clickAction, Player player, SlotAccess carriedAccess) {
        if (clickAction != ClickAction.SECONDARY || other.isEmpty()) return false;
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
