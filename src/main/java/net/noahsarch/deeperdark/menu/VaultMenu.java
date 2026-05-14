package net.noahsarch.deeperdark.menu;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.block.VaultBlockEntity;

import org.jspecify.annotations.Nullable;

public class VaultMenu extends AbstractContainerMenu {

    private final @Nullable VaultBlockEntity vault;
    private final int maxTypes;
    private final DataSlot[] countLow;
    private final DataSlot[] countHigh;

    // Server-side constructor (opened from block entity)
    public VaultMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, VaultBlockEntity vault) {
        super(menuType, containerId);
        this.vault = vault;
        this.maxTypes = vault.getMaxTypes();

        setupSlots(playerInventory, new SimpleContainer(maxTypes), true);

        this.countLow  = new DataSlot[maxTypes];
        this.countHigh = new DataSlot[maxTypes];
        for (int i = 0; i < maxTypes; i++) {
            countLow[i]  = DataSlot.standalone();
            countHigh[i] = DataSlot.standalone();
            addDataSlot(countLow[i]);
            addDataSlot(countHigh[i]);
        }

        vault.startOpen(playerInventory.player);
    }

    // Client-side constructor (packet-created, no block entity)
    public VaultMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, int maxTypes) {
        super(menuType, containerId);
        this.vault    = null;
        this.maxTypes = maxTypes;

        setupSlots(playerInventory, new SimpleContainer(maxTypes), false);

        this.countLow  = new DataSlot[maxTypes];
        this.countHigh = new DataSlot[maxTypes];
        for (int i = 0; i < maxTypes; i++) {
            countLow[i]  = DataSlot.standalone();
            countHigh[i] = DataSlot.standalone();
            addDataSlot(countLow[i]);
            addDataSlot(countHigh[i]);
        }
    }

    private void setupSlots(Inventory playerInventory, SimpleContainer displayContainer, boolean serverSide) {
        int[][] vaultPos;
        int invX, invY;

        if (maxTypes == 1) {
            vaultPos = new int[][]{{137, 32}};
            invX = 8; invY = 84;
        } else if (maxTypes == 3) {
            vaultPos = new int[][]{{134, 51}, {121, 77}, {147, 77}};
            invX = 8; invY = 140;
        } else {
            vaultPos = new int[][]{{149,38},{175,38},{201,38},{149,64},{175,64},{201,64},{149,90},{175,90},{201,90}};
            invX = 36; invY = 137;
        }

        for (int i = 0; i < maxTypes; i++) {
            int x = vaultPos[i][0], y = vaultPos[i][1];
            if (serverSide && vault != null) {
                final int idx = i;
                addSlot(new VaultDisplaySlot(displayContainer, i, x, y, vault, idx));
            } else {
                addSlot(new Slot(displayContainer, i, x, y) {
                    @Override public boolean mayPlace(ItemStack stack) { return false; }
                    @Override public int getMaxStackSize() { return Integer.MAX_VALUE / 2; }
                    @Override public int getMaxStackSize(ItemStack stack) { return Integer.MAX_VALUE / 2; }
                });
            }
        }

        addStandardInventorySlots(playerInventory, invX, invY);
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput containerInput, Player player) {
        if (vault != null && slotId >= 0 && slotId < maxTypes && containerInput == ContainerInput.PICKUP) {
            ItemStack cursor = getCarried();

            if (button == 1) {
                // Right-click on a vault slot: take 1 item from vault onto cursor (if compatible)
                VaultBlockEntity.VaultEntry entry = vault.getEntry(slotId);
                if (entry != null && entry.count > 0) {
                    if (cursor.isEmpty() || ItemStack.isSameItemSameComponents(cursor, entry.representative)) {
                        ItemStack taken = vault.withdraw(slotId, 1);
                        if (!taken.isEmpty()) {
                            if (cursor.isEmpty()) {
                                setCarried(taken);
                            } else {
                                cursor.grow(1);
                                setCarried(cursor);
                            }
                            vault.setChanged();
                        }
                    }
                }
                return;
            }

            // Left-click: deposit cursor items if compatible, otherwise take a full stack
            if (!cursor.isEmpty() && vault.canAccept(cursor)) {
                ItemStack remainder = vault.addItems(cursor.copy());
                setCarried(remainder.isEmpty() ? ItemStack.EMPTY : remainder);
                vault.setChanged();
                return;
            }
            if (cursor.isEmpty()) {
                // Left-click with empty cursor: take up to a full stack
                VaultBlockEntity.VaultEntry entry = vault.getEntry(slotId);
                if (entry != null && entry.count > 0) {
                    int take = Math.min(entry.count, Math.min(entry.representative.getMaxStackSize(), 64));
                    ItemStack taken = vault.withdraw(slotId, take);
                    if (!taken.isEmpty()) {
                        setCarried(taken);
                        vault.setChanged();
                    }
                }
                return;
            }
        }
        super.clicked(slotId, button, containerInput, player);
    }

    @Override
    public void broadcastChanges() {
        if (vault != null) {
            for (int i = 0; i < maxTypes; i++) {
                int c = vault.getCount(i);
                countLow[i].set(c & 0x7FFF);
                countHigh[i].set((c >> 15) & 0x7FFF);
            }
        }
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        int invStart = maxTypes;
        int invEnd   = invStart + 36;

        if (slotIndex < maxTypes && vault != null && slot instanceof VaultDisplaySlot vds) {
            // Vault display slot → withdraw one stack to player inventory
            ItemStack display = slot.getItem();
            if (display.isEmpty()) return ItemStack.EMPTY;

            int available = vault.getCount(vds.entryIndex);
            if (available <= 0) return ItemStack.EMPTY;
            int stackSize = Math.min(available, Math.min(display.getMaxStackSize(), 64));

            ItemStack taken = vault.withdraw(vds.entryIndex, stackSize);
            if (taken.isEmpty()) return ItemStack.EMPTY;

            ItemStack toPlace = taken.copy();
            moveItemStackTo(toPlace, invStart, invEnd, true);
            if (!toPlace.isEmpty()) {
                vault.addItems(toPlace);
            }
            vault.setChanged();
            // Always return EMPTY to prevent the game from repeating quickMoveStack
            // in a loop until the vault is exhausted (one batch per shift-click is intended)
            return ItemStack.EMPTY;

        } else if (slotIndex >= invStart && vault != null) {
            // Player inventory → deposit directly into vault
            ItemStack stack = slot.getItem();
            if (!vault.canAccept(stack)) return ItemStack.EMPTY;
            ItemStack original = stack.copy();
            ItemStack remainder = vault.addItems(stack.copy());
            int deposited = original.getCount() - remainder.getCount();
            if (deposited <= 0) return ItemStack.EMPTY;
            slot.setByPlayer(remainder.isEmpty() ? ItemStack.EMPTY : remainder);
            vault.setChanged();
            return original.copyWithCount(deposited);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return vault == null || vault.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (vault != null) vault.stopOpen(player);
    }

    public int getMaxTypes() { return maxTypes; }

    public int getStoredCount(int index) {
        if (index < 0 || index >= maxTypes) return 0;
        int low  = countLow[index].get()  & 0x7FFF;
        int high = countHigh[index].get() & 0x7FFF;
        return low | (high << 15);
    }

    public ItemStack getVaultDisplayItem(int index) {
        if (index < 0 || index >= maxTypes) return ItemStack.EMPTY;
        return this.slots.get(index).getItem();
    }

    public static int getImageWidth(int maxTypes) {
        return maxTypes == 9 ? 230 : 176;
    }

    public static int getImageHeight(int maxTypes) {
        return switch (maxTypes) {
            case 1  -> 166;
            case 3  -> 222;
            default -> 220;
        };
    }

    static class VaultDisplaySlot extends Slot {
        final VaultBlockEntity vault;
        final int entryIndex;

        VaultDisplaySlot(SimpleContainer dummy, int containerIndex, int x, int y,
                         VaultBlockEntity vault, int entryIndex) {
            super(dummy, containerIndex, x, y);
            this.vault      = vault;
            this.entryIndex = entryIndex;
        }

        @Override public boolean mayPlace(ItemStack stack) { return vault != null && vault.canAccept(stack); }

        @Override public int getMaxStackSize() { return Integer.MAX_VALUE / 2; }
        @Override public int getMaxStackSize(ItemStack stack) { return Integer.MAX_VALUE / 2; }

        @Override
        public boolean mayPickup(Player player) {
            return !vault.getDisplayStack(entryIndex).isEmpty();
        }

        @Override
        public ItemStack getItem() { return vault.getDisplayStack(entryIndex); }

        @Override
        public ItemStack remove(int amount) { return vault.withdraw(entryIndex, amount); }

        @Override public void setByPlayer(ItemStack stack) {}
        @Override public void set(ItemStack stack) {}
    }
}
