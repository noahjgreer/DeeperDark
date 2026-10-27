package net.noahsarch.deeperdark.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.noahsarch.deeperdark.block.ModBlocks;
import net.noahsarch.deeperdark.block.VaultBlockEntity;
import net.noahsarch.deeperdark.component.ModComponents;

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
     * True if the item is open in a menu, or is an item-opened box/shulker/vault whose nested child
     * is open. Its live contents are in that menu, so inserting into it or moving it into a
     * container loses items.
     */
    public static boolean isOpen(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().contains(ItemBackedContainer.OPEN_MARKER_KEY);
    }

    /**
     * Returns true if the cursor can be quick-inserted into the vault item.
     * Uses deeperdark:vault_entries as source of truth; falls back to CONTAINER for legacy items.
     */
    public static boolean canVaultInsert(ItemStack vaultItem, ItemStack cursor) {
        if (cursor.isEmpty()) return false;
        int maxTypes = getVaultMaxTypes(vaultItem);
        if (maxTypes < 0) return false;

        List<VaultBlockEntity.VaultEntry> entries = vaultItem.get(ModComponents.VAULT_ENTRIES);
        if (entries != null) {
            for (VaultBlockEntity.VaultEntry e : entries) {
                if (ItemStack.isSameItemSameComponents(e.representative, cursor)) return true;
            }
            return entries.size() < maxTypes;
        }

        // Legacy fallback: vault item predates the vault_entries component.
        ItemContainerContents contents = vaultItem.get(DataComponents.CONTAINER);
        if (contents == null) return true;
        List<ItemStack> types = new ArrayList<>();
        for (ItemStackTemplate template : contents.nonEmptyItems()) {
            ItemStack s = template.create();
            if (s.isEmpty()) continue;
            boolean seen = false;
            for (ItemStack e : types) {
                if (ItemStack.isSameItemSameComponents(e, s)) { seen = true; break; }
            }
            if (!seen && types.size() < maxTypes) types.add(s.copyWithCount(1));
        }
        for (ItemStack e : types) {
            if (ItemStack.isSameItemSameComponents(e, cursor)) return true;
        }
        return types.size() < maxTypes;
    }

    /**
     * Inserts all cursor items into the vault item, updating both deeperdark:vault_entries
     * (lossless full data) and DataComponents.CONTAINER (capped tooltip preview).
     */
    public static boolean tryVaultInsert(ItemStack vaultItem, ItemStack cursor, SlotAccess carriedAccess) {
        if (!canVaultInsert(vaultItem, cursor)) return false;
        int maxTypes = getVaultMaxTypes(vaultItem);

        List<VaultBlockEntity.VaultEntry> entries = readVaultEntries(vaultItem, maxTypes);

        // Merge cursor into matching entry or add a new one.
        boolean inserted = false;
        for (VaultBlockEntity.VaultEntry e : entries) {
            if (ItemStack.isSameItemSameComponents(e.representative, cursor)) {
                long sum = (long) e.count + cursor.getCount();
                e.count = (int) Math.min(sum, Integer.MAX_VALUE);
                inserted = true;
                break;
            }
        }
        if (!inserted) entries.add(new VaultBlockEntity.VaultEntry(cursor.copyWithCount(1), cursor.getCount()));

        writeVaultEntries(vaultItem, entries);
        carriedAccess.set(ItemStack.EMPTY);
        return true;
    }

    /**
     * Reads a vault item's contents as a mutable entry list, reconstructing from the
     * legacy CONTAINER component for items that predate deeperdark:vault_entries.
     */
    public static List<VaultBlockEntity.VaultEntry> readVaultEntries(ItemStack vaultItem, int maxTypes) {
        List<VaultBlockEntity.VaultEntry> rawEntries = vaultItem.get(ModComponents.VAULT_ENTRIES);
        List<VaultBlockEntity.VaultEntry> entries = new ArrayList<>();
        if (rawEntries != null) {
            for (VaultBlockEntity.VaultEntry e : rawEntries) {
                entries.add(new VaultBlockEntity.VaultEntry(e.representative, e.count));
            }
            return entries;
        }

        ItemContainerContents contents = vaultItem.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        for (ItemStackTemplate template : contents.nonEmptyItems()) {
            ItemStack s = template.create();
            if (s.isEmpty()) continue;
            boolean merged = false;
            for (VaultBlockEntity.VaultEntry e : entries) {
                if (ItemStack.isSameItemSameComponents(e.representative, s)) {
                    long sum = (long) e.count + s.getCount();
                    e.count = (int) Math.min(sum, Integer.MAX_VALUE);
                    merged = true;
                    break;
                }
            }
            if (!merged && entries.size() < maxTypes)
                entries.add(new VaultBlockEntity.VaultEntry(s.copyWithCount(1), s.getCount()));
        }
        return entries;
    }

    /**
     * Writes the lossless deeperdark:vault_entries component and regenerates the
     * capped CONTAINER preview (≤256 stacks) that tooltip mods read.
     */
    public static void writeVaultEntries(ItemStack vaultItem, List<VaultBlockEntity.VaultEntry> entries) {
        vaultItem.set(ModComponents.VAULT_ENTRIES, entries);

        List<ItemStack> preview = new ArrayList<>();
        outer:
        for (VaultBlockEntity.VaultEntry e : entries) {
            int batchSize = e.representative.getMaxStackSize();
            int remaining = e.count;
            while (remaining > 0) {
                if (preview.size() >= 256) break outer;
                int batch = Math.min(remaining, batchSize);
                preview.add(e.representative.copyWithCount(batch));
                remaining -= batch;
            }
        }
        if (!preview.isEmpty()) {
            vaultItem.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(preview));
        } else {
            vaultItem.remove(DataComponents.CONTAINER);
        }
    }

    /**
     * Merges {@code incoming} into stacks the box already holds of that exact item, never
     * opening a new slot. Shrinks {@code incoming} by the amount moved and returns that amount.
     * Used by the hopper trinket, which must only route items to containers already stocking them.
     */
    public static int insertMatchingOnly(ItemStack containerItem, ItemStack incoming, int size) {
        if (incoming.isEmpty() || size <= 0) return 0;

        NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
        containerItem.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);

        int inserted = 0;
        for (int i = 0; i < size && !incoming.isEmpty(); i++) {
            ItemStack slot = items.get(i);
            if (slot.isEmpty() || !ItemStack.isSameItemSameComponents(slot, incoming)) continue;
            int amount = Math.min(slot.getMaxStackSize() - slot.getCount(), incoming.getCount());
            if (amount > 0) {
                slot.grow(amount);
                incoming.shrink(amount);
                inserted += amount;
            }
        }

        if (inserted > 0) {
            containerItem.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(new ArrayList<>(items)));
        }
        return inserted;
    }

    /**
     * Vault counterpart of {@link #insertMatchingOnly}: adds to an existing vault entry only,
     * never claiming a free entry slot. Shrinks {@code incoming} and returns the amount moved.
     */
    public static int insertMatchingOnlyVault(ItemStack vaultItem, ItemStack incoming) {
        int maxTypes = getVaultMaxTypes(vaultItem);
        if (maxTypes < 0 || incoming.isEmpty()) return 0;

        List<VaultBlockEntity.VaultEntry> entries = readVaultEntries(vaultItem, maxTypes);
        for (VaultBlockEntity.VaultEntry e : entries) {
            if (!ItemStack.isSameItemSameComponents(e.representative, incoming)) continue;
            // Vault entries are effectively unbounded; cap at Integer.MAX_VALUE like tryVaultInsert.
            int room = Integer.MAX_VALUE - e.count;
            int amount = Math.min(room, incoming.getCount());
            if (amount <= 0) return 0;
            e.count += amount;
            incoming.shrink(amount);
            writeVaultEntries(vaultItem, entries);
            return amount;
        }
        return 0;
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
