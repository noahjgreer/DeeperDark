package net.noahsarch.deeperdark.menu;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.block.CrateBlockEntity;
import org.jspecify.annotations.Nullable;

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

    private final @Nullable CrateBlockEntity crate;
    private final SimpleContainer display = new SimpleContainer(VISIBLE_SLOTS) {
        @Override public int getMaxStackSize(ItemStack stack) { return Integer.MAX_VALUE; }
    };
    private final DataSlot entryCount = DataSlot.standalone();
    private final DataSlot autoMerge = DataSlot.standalone();
    private int scrollRow;

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
        addStandardInventorySlots(inventory, 8, 18 + VISIBLE_ROWS * 18 + 13);
        addDataSlot(entryCount);
        addDataSlot(autoMerge);
        if (crate != null) {
            refresh();
            crate.startOpen(inventory.player);
        }
    }

    public static int maxScrollRow(int entries) {
        return Math.max(0, (entries + COLUMNS - 1) / COLUMNS - VISIBLE_ROWS);
    }

    public int getEntryCount() { return entryCount.get(); }

    public boolean isAutoMerge() { return autoMerge.get() != 0; }

    @Override
    public void clicked(int slotId, int button, ContainerInput input, Player player) {
        if (slotId < 0 || slotId >= VISIBLE_SLOTS || input == ContainerInput.CLONE) {
            super.clicked(slotId, button, input, player);
            return;
        }
        if (crate == null) return;

        int index = scrollRow * COLUMNS + slotId;
        ItemStack carried = getCarried();
        if (input == ContainerInput.PICKUP && !carried.isEmpty()) {
            ItemStack offered = button == 0 ? carried : carried.copyWithCount(1);
            carried.shrink(offered.getCount() - crate.insert(offered).getCount());
        } else if (input == ContainerInput.PICKUP && index < crate.getEntryCount()) {
            int stack = stackSize(index);
            setCarried(crate.withdraw(index, button == 0 ? stack : (stack + 1) / 2));
        } else if (input == ContainerInput.QUICK_MOVE && index < crate.getEntryCount()) {
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
        scrollRow = Math.min(row, maxScrollRow(crate.getEntryCount()));
        return true;
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
        scrollRow = Math.min(scrollRow, maxScrollRow(crate.getEntryCount()));
        for (int i = 0; i < VISIBLE_SLOTS; i++) {
            display.setItem(i, crate.getDisplayStack(scrollRow * COLUMNS + i));
        }
        entryCount.set(crate.getEntryCount());
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
