package net.noahsarch.deeperdark.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.noahsarch.deeperdark.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;

public final class ContainerItemUtil {

    private ContainerItemUtil() {}

    public static int getContainerSize(ItemStack stack) {
        if (stack.is(ItemTags.SHULKER_BOXES)) return 27;
        if (stack.is(ModBlocks.FLIMSY_BOX.asItem())) return 3;
        if (stack.is(ModBlocks.STURDY_BOX.asItem())) return 6;
        if (stack.is(ModBlocks.REINFORCED_BOX.asItem())) return 9;
        return -1;
    }

    /** Returns true if at least 1 of cursor can be placed into the container. */
    public static boolean canInsert(ItemStack containerItem, ItemStack cursor, int size) {
        if (cursor.isEmpty() || size <= 0) return false;
        NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
        containerItem.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        for (ItemStack existing : items) {
            if (existing.isEmpty()) return true;
            if (ItemStack.isSameItemSameComponents(existing, cursor) && existing.getCount() < existing.getMaxStackSize())
                return true;
        }
        return false;
    }

    /**
     * Inserts as many items from cursor into the container as possible.
     * Updates both the container's CONTAINER component and the cursor via carriedAccess.
     * Returns true if at least 1 item was inserted.
     */
    public static boolean tryInsert(ItemStack containerItem, ItemStack cursor, SlotAccess carriedAccess, int size) {
        if (cursor.isEmpty() || size <= 0) return false;

        NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
        containerItem.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);

        ItemStack remaining = cursor.copy();
        int inserted = 0;

        // Pass 1: merge into existing matching stacks
        for (int i = 0; i < size && !remaining.isEmpty(); i++) {
            ItemStack slot = items.get(i);
            if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, remaining)) {
                int space = slot.getMaxStackSize() - slot.getCount();
                int amount = Math.min(space, remaining.getCount());
                if (amount > 0) {
                    slot.grow(amount);
                    remaining.shrink(amount);
                    inserted += amount;
                }
            }
        }

        // Pass 2: fill empty slots
        for (int i = 0; i < size && !remaining.isEmpty(); i++) {
            if (items.get(i).isEmpty()) {
                int amount = remaining.getCount();
                items.set(i, remaining.copyWithCount(amount));
                remaining.shrink(amount);
                inserted += amount;
            }
        }

        if (inserted > 0) {
            List<ItemStack> list = new ArrayList<>(size);
            for (ItemStack s : items) list.add(s);
            containerItem.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));
            carriedAccess.set(remaining.isEmpty() ? ItemStack.EMPTY : remaining);
            return true;
        }
        return false;
    }
}
