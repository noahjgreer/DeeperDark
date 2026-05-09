package net.noahsarch.deeperdark.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.component.CollarFuelData;
import net.noahsarch.deeperdark.component.ModComponents;
import net.noahsarch.deeperdark.duck.CollarHolder;

import java.util.function.Consumer;

public class CollarItem extends Item {

    private final CollarTier tier;

    /** Legacy constructor for the untiered generic collar. */
    public CollarItem(Properties properties) {
        super(properties);
        this.tier = null;
    }

    public CollarItem(CollarTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    /** Returns the collar's tier, or {@code null} for the legacy untiered collar. */
    public CollarTier getTier() {
        return tier;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer) {
            ItemStack stack = player.getItemInHand(hand);
            if (player instanceof CollarHolder holder && holder.deeperdark$getCollarItem().isEmpty()) {
                holder.deeperdark$setCollarItem(stack.copy());
                player.setItemInHand(hand, ItemStack.EMPTY);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        if (tier != null) {
            CollarFuelData fuel = stack.getOrDefault(ModComponents.COLLAR_FUEL, CollarFuelData.EMPTY);
            if (tier.fireMax > 0) {
                tooltip.accept(Component.translatable("item.deeperdark.collar.fuel.fire",
                    fuel.fireTicks(), tier.fireMax).withStyle(ChatFormatting.GOLD));
            }
            if (tier.waterMax > 0) {
                tooltip.accept(Component.translatable("item.deeperdark.collar.fuel.water",
                    fuel.waterTicks(), tier.waterMax).withStyle(ChatFormatting.AQUA));
            }
        }
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        if (contents.nonEmptyItemCopyStream().findAny().isEmpty()) {
            tooltip.accept(Component.translatable("item.deeperdark.collar.empty").withStyle(ChatFormatting.GRAY));
        }
        // Trinket names are shown automatically by DataComponents.CONTAINER's TooltipProvider
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        if (tier != null) {
            CollarFuelData fuel = stack.getOrDefault(ModComponents.COLLAR_FUEL, CollarFuelData.EMPTY);
            int fire  = Math.min(fuel.fireTicks(),  tier.fireMax);
            int water = Math.min(fuel.waterTicks(), tier.waterMax);
            boolean hasAny = fire > 0 || water > 0;
            boolean isFull = fire >= tier.fireMax && water >= tier.waterMax;
            return hasAny && !isFull;
        }
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        return contents.nonEmptyItemCopyStream().anyMatch(s -> s.isDamageableItem() && s.getDamageValue() > 0);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (tier != null) {
            CollarFuelData fuel = stack.getOrDefault(ModComponents.COLLAR_FUEL, CollarFuelData.EMPTY);
            double totalMax = tier.fireMax + tier.waterMax;
            double totalCur = Math.min(fuel.fireTicks(), tier.fireMax) + Math.min(fuel.waterTicks(), tier.waterMax);
            if (totalMax == 0) return 13;
            return (int) Math.round(13.0 * totalCur / totalMax);
        }
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
        return (int) Math.round(13.0 * sumCurrentDurability / sumMaxDurability);
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
