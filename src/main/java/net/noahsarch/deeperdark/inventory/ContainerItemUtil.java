package net.noahsarch.deeperdark.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.ItemContainerContents;
import net.noahsarch.deeperdark.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;

public final class ContainerItemUtil {

    private ContainerItemUtil() {}

    /** Returns slot count for shulker/custom boxes, or -1 for non-container items (vaults use getVaultMaxTypes). */
    public static int getContainerSize(ItemStack stack) {
        if (stack.is(ItemTags.SHULKER_BOXES)) return 27;
        if (stack.is(ModBlocks.FLIMSY_BOX.asItem())) return 3;
        if (stack.is(ModBlocks.STURDY_BOX.asItem())) return 6;
        if (stack.is(ModBlocks.REINFORCED_BOX.asItem())) return 9;
        return -1;
    }

    /** Returns the max item-type count for vault items, or -1 if not a vault. */
    public static int getVaultMaxTypes(ItemStack stack) {
        if (stack.is(ModBlocks.SMALL_ITEM_VAULT.asItem())) return 1;
        if (stack.is(ModBlocks.MEDIUM_ITEM_VAULT.asItem())) return 3;
        if (stack.is(ModBlocks.LARGE_ITEM_VAULT.asItem())) return 9;
        return -1;
    }

    public static boolean isVaultItem(ItemStack stack) {
        return getVaultMaxTypes(stack) >= 0;
    }

    /**
     * Returns true if the cursor can be quick-inserted into the vault item.
     * Reads the CONTAINER component to check existing entry types and capacity.
     */
    public static boolean canVaultInsert(ItemStack vaultItem, ItemStack cursor) {
        if (cursor.isEmpty()) return false;
        int maxTypes = getVaultMaxTypes(vaultItem);
        if (maxTypes < 0) return false;

        ItemContainerContents contents = vaultItem.get(DataComponents.CONTAINER);
        if (contents == null) return true; // Empty vault, any item can start a new entry

        // Reconstruct unique entry types from the batched CONTAINER data
        List<ItemStack> entries = new ArrayList<>();
        for (ItemStackTemplate template : contents.nonEmptyItems()) {
            ItemStack s = template.create();
            if (s.isEmpty()) continue;
            boolean seen = false;
            for (ItemStack e : entries) {
                if (ItemStack.isSameItemSameComponents(e, s)) { seen = true; break; }
            }
            if (!seen && entries.size() < maxTypes) entries.add(s.copyWithCount(1));
        }

        // Cursor matches an existing type → vault always has room (unbounded per type)
        for (ItemStack e : entries) {
            if (ItemStack.isSameItemSameComponents(e, cursor)) return true;
        }
        // New type → needs an empty slot
        return entries.size() < maxTypes;
    }

    /**
     * Inserts all cursor items into the vault's CONTAINER component (merging by type).
     * Vaults have unlimited per-type capacity, so the cursor is always fully consumed.
     */
    public static boolean tryVaultInsert(ItemStack vaultItem, ItemStack cursor, SlotAccess carriedAccess) {
        if (!canVaultInsert(vaultItem, cursor)) return false;
        int maxTypes = getVaultMaxTypes(vaultItem);

        // Decode current entries
        ItemContainerContents contents = vaultItem.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        List<ItemStack> entries = new ArrayList<>();
        for (ItemStackTemplate template : contents.nonEmptyItems()) {
            ItemStack s = template.create();
            if (s.isEmpty()) continue;
            boolean merged = false;
            for (ItemStack e : entries) {
                if (ItemStack.isSameItemSameComponents(e, s)) {
                    long sum = (long) e.getCount() + s.getCount();
                    e.setCount((int) Math.min(sum, Integer.MAX_VALUE));
                    merged = true;
                    break;
                }
            }
            if (!merged && entries.size() < maxTypes) entries.add(s.copy());
        }

        // Add cursor items into matching entry or create a new entry
        boolean inserted = false;
        for (ItemStack e : entries) {
            if (ItemStack.isSameItemSameComponents(e, cursor)) {
                long sum = (long) e.getCount() + cursor.getCount();
                e.setCount((int) Math.min(sum, Integer.MAX_VALUE));
                inserted = true;
                break;
            }
        }
        if (!inserted) entries.add(cursor.copy());

        // Re-encode entries back to the CONTAINER component (batch by maxStackSize)
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemStack e : entries) {
            int batchSize = e.getMaxStackSize();
            int remaining = e.getCount();
            while (remaining > 0) {
                int batch = Math.min(remaining, batchSize);
                stacks.add(e.copyWithCount(batch));
                remaining -= batch;
            }
        }
        vaultItem.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(stacks));
        carriedAccess.set(ItemStack.EMPTY); // Vault always accepts all inserted items
        return true;
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
