package net.noahsarch.deeperdark.menu;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.noahsarch.deeperdark.block.CrateBlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Shows a scrolling window of a crate's entries. The server owns every click on those slots:
 * they show copies with the entry's full count, so vanilla's slot logic would dupe or delete.
 */
public class CrateMenu extends AbstractContainerMenu {

    public static final int COLUMNS = 9;
    public static final int VISIBLE_ROWS = 6;
    public static final int VISIBLE_SLOTS = COLUMNS * VISIBLE_ROWS;
    public static final int TOGGLE_AUTO_MERGE_BUTTON = 0;
    /** Button id {@code SCROLL_BUTTON + row} scrolls to that row. */
    public static final int SCROLL_BUTTON = 1;
    private static final String[] ROMAN = {"i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x"};

    private final @Nullable CrateBlockEntity crate;
    private final SimpleContainer display = new SimpleContainer(VISIBLE_SLOTS) {
        @Override public int getMaxStackSize(ItemStack stack) { return Integer.MAX_VALUE; }
    };
    private final DataSlot matchCount = DataSlot.standalone();
    private final DataSlot autoMerge = DataSlot.standalone();
    private final DataSlot searchGeneration = DataSlot.standalone();
    private int scrollRow;
    private String[] searchTerms = {""};
    // Indices of the crate entries matching the search, in crate order.
    private final List<Integer> matches = new ArrayList<>();

    /** Client side: the server sends what the slots show. */
    public CrateMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null);
    }

    public CrateMenu(int containerId, Inventory inventory, @Nullable CrateBlockEntity crate) {
        super(ModMenus.CRATE, containerId);
        this.crate = crate;
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                addSlot(new Slot(display, row * COLUMNS + column, 8 + column * 18, 18 + row * 18) {
                    @Override public boolean mayPlace(ItemStack stack) { return false; }
                    @Override public boolean mayPickup(Player player) { return false; }
                });
            }
        }
        // One lower than ChestMenu: crate.png doesn't shift its inventory half up a pixel as ContainerScreen does.
        addStandardInventorySlots(inventory, 8, 18 + VISIBLE_ROWS * 18 + 14);
        addDataSlot(matchCount);
        addDataSlot(autoMerge);
        addDataSlot(searchGeneration);
        if (crate != null) {
            refresh();
            crate.startOpen(inventory.player);
        }
    }

    public static int maxScrollRow(int entries) {
        return Math.max(0, (entries + COLUMNS - 1) / COLUMNS - VISIBLE_ROWS);
    }

    /** Entries matching the search, which is what scrolls. */
    public int getMatchCount() { return matchCount.get(); }

    public boolean isAutoMerge() { return autoMerge.get() != 0; }

    @Override
    public void clicked(int slotId, int button, ContainerInput input, Player player) {
        if (slotId < 0 || slotId >= VISIBLE_SLOTS || input == ContainerInput.CLONE) {
            super.clicked(slotId, button, input, player);
            return;
        }
        if (crate == null) return;

        // Entries may have moved since the last broadcast; act on what matches now.
        updateMatches();
        int position = scrollRow * COLUMNS + slotId;
        int index = position < matches.size() ? matches.get(position) : -1;
        ItemStack carried = getCarried();
        if (input == ContainerInput.PICKUP && !carried.isEmpty()) {
            ItemStack offered = button == 0 ? carried : carried.copyWithCount(1);
            carried.shrink(offered.getCount() - crate.insert(offered).getCount());
        } else if (input == ContainerInput.PICKUP && index >= 0) {
            int stack = stackSize(index);
            setCarried(crate.withdraw(index, button == 0 ? stack : (stack + 1) / 2));
        } else if (input == ContainerInput.QUICK_MOVE && index >= 0) {
            ItemStack taken = crate.withdraw(index, stackSize(index));
            moveItemStackTo(taken, VISIBLE_SLOTS, slots.size(), true);
            crate.putBack(taken); // always fits: these just left that entry
        } else {
            return;
        }
        crate.setChanged();
    }

    private int stackSize(int index) {
        ItemStack entry = crate.getDisplayStack(index);
        return Math.min(entry.getCount(), entry.getMaxStackSize());
    }

    /** Shift-click from the player's inventory. */
    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (crate == null || slotIndex < VISIBLE_SLOTS) return ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        ItemStack stack = slot.getItem();
        ItemStack rest = crate.insert(stack);
        if (rest.getCount() != stack.getCount()) {
            slot.setByPlayer(rest);
            crate.setChanged();
        }
        // Empty stops vanilla's repeat loop; one shift-click moves one stack.
        return ItemStack.EMPTY;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (crate == null) return false;
        if (id == TOGGLE_AUTO_MERGE_BUTTON) {
            crate.setAutoMerge(!crate.isAutoMerge());
            crate.setChanged();
            return true;
        }
        int row = id - SCROLL_BUTTON;
        if (row < 0) return false;
        // Clamp rather than refuse: the client clamps the same way once the shrunk count reaches it.
        scrollRow = Math.min(row, maxScrollRow(matches.size()));
        return true;
    }

    public void setSearch(String query, int generation) {
        searchTerms = query.toLowerCase(Locale.ROOT).trim().split("\\s+");
        searchGeneration.set(generation);
        scrollRow = 0;
    }

    /** The last search the server applied, so the client can hold clicks until its results arrive. */
    public int getSearchGeneration() { return searchGeneration.get(); }

    private void updateMatches() {
        boolean all = searchTerms.length == 1 && searchTerms[0].isEmpty();
        matches.clear();
        for (int i = 0; i < crate.getEntryCount(); i++) {
            if (all || matchesSearch(crate.getDisplayStack(i))) matches.add(i);
        }
    }

    /**
     * Every word must appear in the name, the item id or an enchantment's name or id. A level word
     * ("v" or "5") instead needs an enchantment of that level matching the word before it, or that
     * whole word in the name ("Potion of Strength II"). Names come from the server's language,
     * so ids are what work across languages.
     */
    private boolean matchesSearch(ItemStack stack) {
        String name = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
        List<String> nameWords = List.of(name.split("\\s+"));
        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet();
        for (int t = 0; t < searchTerms.length; t++) {
            String term = searchTerms[t];
            int level = parseLevel(term);
            boolean found;
            if (level > 0) {
                String enchantment = t > 0 ? searchTerms[t - 1] : "";
                found = nameWords.contains(term) || (level <= ROMAN.length && nameWords.contains(ROMAN[level - 1]))
                    || enchantments.stream().anyMatch(e -> e.getIntValue() == level && enchantmentMatches(e.getKey(), enchantment));
            } else {
                found = name.contains(term)
                    || BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains(term)
                    || enchantments.stream().anyMatch(e -> enchantmentMatches(e.getKey(), term));
            }
            if (!found) return false;
        }
        return true;
    }

    private static boolean enchantmentMatches(Holder<Enchantment> enchantment, String term) {
        String id = enchantment.getRegisteredName();
        return id.substring(id.indexOf(':') + 1).contains(term)
            || enchantment.value().description().getString().toLowerCase(Locale.ROOT).contains(term);
    }

    private static int parseLevel(String term) {
        if (!term.isEmpty() && term.length() <= 3 && term.chars().allMatch(Character::isDigit)) return Integer.parseInt(term);
        return List.of(ROMAN).indexOf(term) + 1;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack carried, Slot slot) {
        return slot.index >= VISIBLE_SLOTS && super.canTakeItemForPickAll(carried, slot);
    }

    @Override
    public boolean canDragTo(Slot slot) {
        return slot.index >= VISIBLE_SLOTS;
    }

    @Override
    public void broadcastChanges() {
        refresh();
        super.broadcastChanges();
    }

    @Override
    public void broadcastFullState() {
        refresh();
        super.broadcastFullState();
    }

    private void refresh() {
        if (crate == null) return;
        updateMatches();
        scrollRow = Math.min(scrollRow, maxScrollRow(matches.size()));
        for (int i = 0; i < VISIBLE_SLOTS; i++) {
            int position = scrollRow * COLUMNS + i;
            display.setItem(i, position < matches.size() ? crate.getDisplayStack(matches.get(position)) : ItemStack.EMPTY);
        }
        matchCount.set(matches.size());
        autoMerge.set(crate.isAutoMerge() ? 1 : 0);
    }

    @Override
    public boolean stillValid(Player player) {
        return crate == null || crate.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (crate != null) crate.stopOpen(player);
    }
}
