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
            vaultPos = new int[][]{{133, 50}, {120, 76}, {146, 76}};
            invX = 8; invY = 100;
        } else {
            vaultPos = new int[][]{{149,22},{175,22},{201,22},{149,48},{175,48},{201,48},{149,74},{175,74},{201,74}};
            invX = 36; invY = 134;
        }

        for (int i = 0; i < maxTypes; i++) {
            int x = vaultPos[i][0], y = vaultPos[i][1];
            if (serverSide && vault != null) {
                final int idx = i;
                addSlot(new VaultDisplaySlot(displayContainer, i, x, y, vault, idx));
            } else {
                addSlot(new Slot(displayContainer, i, x, y) {
                    @Override public boolean mayPlace(ItemStack stack) { return false; }
                });
            }
        }

        addStandardInventorySlots(playerInventory, invX, invY);
    }

    // Deposit items from cursor onto a vault display slot
    @Override
    public void clicked(int slotId, int button, ContainerInput containerInput, Player player) {
        if (vault != null && slotId >= 0 && slotId < maxTypes && containerInput == ContainerInput.PICKUP) {
            ItemStack cursor = getCarried();
            if (!cursor.isEmpty() && vault.canAccept(cursor)) {
                ItemStack remainder = vault.addItems(cursor.copy());
                setCarried(remainder.isEmpty() ? ItemStack.EMPTY : remainder);
                vault.setChanged();
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
            int stackSize = Math.min(available, display.getMaxStackSize());

            ItemStack taken = vault.withdraw(vds.entryIndex, stackSize);
            if (taken.isEmpty()) return ItemStack.EMPTY;

            ItemStack toPlace = taken.copy();
            moveItemStackTo(toPlace, invStart, invEnd, true);
            int placed = taken.getCount() - toPlace.getCount();

            if (!toPlace.isEmpty()) {
                vault.addItems(toPlace);
            }
            vault.setChanged();
            return placed > 0 ? taken.copyWithCount(placed) : ItemStack.EMPTY;

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
        return maxTypes == 9 ? 226 : 176;
    }

    public static int getImageHeight(int maxTypes) {
        return switch (maxTypes) {
            case 1  -> 166;
            case 3  -> 182;
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

        @Override public boolean mayPlace(ItemStack stack) { return false; }

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
