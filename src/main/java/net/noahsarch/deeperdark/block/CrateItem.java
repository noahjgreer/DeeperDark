package net.noahsarch.deeperdark.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.noahsarch.deeperdark.component.ModComponents;

import java.util.function.Consumer;

public class CrateItem extends BlockItem {

    public CrateItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        CrateContents contents = stack.get(ModComponents.CRATE_CONTENTS);
        if (contents == null || contents.entries().isEmpty()) {
            tooltip.accept(Component.translatable("item.deeperdark.crate.empty").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.accept(Component.translatable("item.deeperdark.crate.contents",
                    String.format("%,d", contents.totalCount()),
                    Component.translatable(contents.entries().getFirst().representative.getItem().getDescriptionId()))
                .withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("item.deeperdark.crate.variants", contents.entries().size())
                .withStyle(ChatFormatting.DARK_GRAY));
        }
        if (contents != null && contents.autoMerge()) {
            tooltip.accept(Component.translatable("item.deeperdark.crate.auto_merge").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    // Bundles hold any non-container item, so a crate of bundles holding crates would nest forever.
    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }
}
