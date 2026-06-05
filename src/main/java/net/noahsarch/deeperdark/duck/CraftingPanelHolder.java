package net.noahsarch.deeperdark.duck;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * Mixed into InventoryMenu to expose the collar crafting panel's open/closed state
 * and internal containers to the screen mixin and AbstractCraftingMenuMixin.
 */
public interface CraftingPanelHolder {
    boolean deeperdark$isPanelOpen();
    void deeperdark$setPanelOpen(boolean open);

    /** When true, getInputGridSlots/getResultSlot return the vanilla 2×2 slots. */
    boolean deeperdark$isUseVanillaSlots();
    void deeperdark$setUseVanillaSlots(boolean use);

    CraftingContainer deeperdark$getExtraCraftSlots();
    ResultContainer deeperdark$getExtraResultSlots();

    static boolean hasCraftingTrinket(Player player) {
        if (!(player instanceof CollarHolder holder)) return false;
        ItemStack collar = holder.deeperdark$getCollarItem();
        if (collar.isEmpty()) return false;
        ItemContainerContents contents = collar.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> trinkets = NonNullList.withSize(5, ItemStack.EMPTY);
        contents.copyInto(trinkets);
        return trinkets.stream().anyMatch(s -> s.is(Items.CRAFTING_TABLE));
    }
}
