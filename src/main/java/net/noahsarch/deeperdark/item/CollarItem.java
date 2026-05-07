package net.noahsarch.deeperdark.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.duck.CollarHolder;
import net.noahsarch.deeperdark.menu.CollarMenu;
import net.noahsarch.deeperdark.menu.ModMenus;

import java.util.function.Consumer;

public class CollarItem extends Item {

    public CollarItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(hand);

            // Shift + right-click: equip the collar directly into the collar slot
            if (player.isShiftKeyDown()) {
                if (player instanceof CollarHolder holder && holder.deeperdark$getCollarItem().isEmpty()) {
                    holder.deeperdark$setCollarItem(stack.copy());
                    player.setItemInHand(hand, ItemStack.EMPTY);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }

            if (!(serverPlayer.containerMenu instanceof InventoryMenu)) {
                return InteractionResult.PASS;
            }
            serverPlayer.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) -> new CollarMenu(ModMenus.COLLAR, containerId, inventory, stack),
                stack.getHoverName()
            ));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        if (contents.nonEmptyItemCopyStream().findAny().isEmpty()) {
            tooltip.accept(Component.translatable("item.deeperdark.collar.empty").withStyle(ChatFormatting.GRAY));
        }
        // Trinket names are shown automatically by DataComponents.CONTAINER's TooltipProvider
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        return contents.nonEmptyItemCopyStream().anyMatch(s -> s.isDamageableItem() && s.getDamageValue() > 0);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        double sumCurrentDurability = 0;
        double sumMaxDurability = 0;
        for (ItemStack s : (Iterable<ItemStack>) contents.nonEmptyItemCopyStream()::iterator) {
            if (s.isDamageableItem()) {
                sumCurrentDurability += (s.getMaxDamage() - s.getDamageValue());
                sumMaxDurability += s.getMaxDamage();
            }
        }
        if (sumMaxDurability == 0) return 13;
        double D = sumCurrentDurability / sumMaxDurability;
        return (int) Math.round(13.0 * D);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        int width = getBarWidth(stack);
        float f = Math.max(0.0F, (float) width / 13.0F);
        int r = (int) ((1.0F - f) * 255.0F);
        int g = (int) (f * 255.0F);
        return 0xFF000000 | (r << 16) | (g << 8);
    }
}
