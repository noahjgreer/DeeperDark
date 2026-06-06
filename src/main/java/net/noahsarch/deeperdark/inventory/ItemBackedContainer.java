package net.noahsarch.deeperdark.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A SimpleContainer backed by the CONTAINER data component on a player's inventory item.
 *
 * The source item is tracked by a UUID written into its CUSTOM_DATA component rather than
 * by object identity. This survives the ItemStack.split() copy that MC performs when the
 * player drags the item between inventory slots — the copy inherits all data components,
 * including the UUID marker, so the container stays valid.
 *
 * Changes are written back in real time via setChanged(), keeping the item's NBT current
 * even before the menu closes.
 *
 * When opened as a nested container (inside an already-open container), {@code parentStorage}
 * is non-null and is searched instead of the player inventory to locate the tracked item.
 * A {@link #NESTED_FROM_KEY} int value is stamped on the parent container's item in the
 * player's inventory so that closing the nested container can reopen the parent.
 */
public class ItemBackedContainer extends SimpleContainer {

    /** Key used inside CUSTOM_DATA to hold the open-session UUID. */
    public static final String OPEN_MARKER_KEY = "deeperdark_open";

    /** Stamped only when the container is opened from the inventory screen (not from right-click in hand). */
    public static final String FROM_SCREEN_MARKER_KEY = "deeperdark_from_screen";

    /**
     * Stamped on the PARENT container item (in the player's inventory) while a nested
     * child container is open. Stores the parent's player-inventory slot index as an int.
     * When the child closes, this value is used to reopen the parent.
     */
    public static final String NESTED_FROM_KEY = "deeperdark_nested_from";

    private final ServerPlayer player;
    private final String openId;
    private final @Nullable SoundEvent closeSound;

    /**
     * Non-null when this container was opened from inside another open container.
     * Searched instead of the player inventory when looking for the tracked item.
     */
    private final @Nullable Container parentStorage;

    private ItemBackedContainer(ServerPlayer player, ItemStack sourceStack, int size,
                                 @Nullable SoundEvent closeSound, @Nullable Container parentStorage) {
        super(size);
        this.player = player;
        this.openId = UUID.randomUUID().toString();
        this.closeSound = closeSound;
        this.parentStorage = parentStorage;

        CustomData.update(DataComponents.CUSTOM_DATA, sourceStack, tag -> tag.putString(OPEN_MARKER_KEY, openId));

        NonNullList<ItemStack> loaded = NonNullList.withSize(size, ItemStack.EMPTY);
        sourceStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(loaded);
        for (int i = 0; i < size; i++) {
            this.items.set(i, loaded.get(i));
        }
    }

    /** Opens a container item from the player's own inventory at {@code inventorySlot}. */
    public static ItemBackedContainer of(ServerPlayer player, int inventorySlot, int size, @Nullable SoundEvent closeSound) {
        ItemStack stack = player.getInventory().getItem(inventorySlot);
        return new ItemBackedContainer(player, stack, size, closeSound, null);
    }

    /**
     * Opens a container item that lives inside another open container (e.g. a shulker box
     * inside an ender chest). The tracked item is looked up in {@code parentStorage} rather
     * than the player's main inventory.
     */
    public static ItemBackedContainer ofNested(ServerPlayer player, Container parentStorage,
                                                int slotInParent, int size, @Nullable SoundEvent closeSound) {
        ItemStack stack = parentStorage.getItem(slotInParent);
        return new ItemBackedContainer(player, stack, size, closeSound, parentStorage);
    }

    /** Returns true if {@code stack} is the item this container is currently tracking. */
    public boolean isTrackingItem(ItemStack stack) {
        return !stack.isEmpty() && openId.equals(markerOf(stack));
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (isTrackingItem(stack)) return false;
        if (ContainerItemUtil.getContainerSize(stack) >= 0) return false;
        return super.canPlaceItem(slot, stack);
    }

    @Override
    public boolean stillValid(Player p) {
        return p == this.player && findMarkedItem() != null;
    }

    /** Write contents back to the source item on every change so NBT is always current. */
    @Override
    public void setChanged() {
        super.setChanged();
        saveBack();
    }

    @Override
    public void stopOpen(ContainerUser user) {
        super.stopOpen(user);
        if (closeSound != null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    closeSound, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        saveBack();
        ItemStack current = findMarkedItem();
        if (current != null) {
            CustomData.update(DataComponents.CUSTOM_DATA, current, tag -> {
                tag.remove(OPEN_MARKER_KEY);
                tag.remove(FROM_SCREEN_MARKER_KEY);
            });
        }

        // Check whether a parent container is waiting to be reopened.
        int parentSlot = findNestedFromSlot();
        if (parentSlot >= 0) {
            clearNestedFromMarker(parentSlot);
            // Defer to avoid calling openMenu during the current menu's removed() teardown.
            player.level().getServer().execute(() -> {
                if (!player.isRemoved()) {
                    net.noahsarch.deeperdark.Deeperdark.openContainerFromInventory(player, parentSlot, true);
                }
            });
        } else {
            player.inventoryMenu.broadcastChanges();
        }
    }

    private void saveBack() {
        ItemStack target = findMarkedItem();
        if (target == null) return;
        List<ItemStack> list = new ArrayList<>(getContainerSize());
        for (int i = 0; i < getContainerSize(); i++) list.add(getItem(i).copy());
        target.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));

        if (parentStorage != null) {
            // Cascade: if the parent is itself a tracked container, persist it too.
            parentStorage.setChanged();
        } else {
            player.inventoryMenu.broadcastChanges();
        }
    }

    /**
     * Locates the item bearing our open-marker UUID.
     * For nested containers, searches {@link #parentStorage}; otherwise searches the
     * player's inventory and cursor.
     */
    private @Nullable ItemStack findMarkedItem() {
        if (parentStorage != null) {
            for (int i = 0; i < parentStorage.getContainerSize(); i++) {
                ItemStack s = parentStorage.getItem(i);
                if (isMarkedWith(s)) return s;
            }
        } else {
            Inventory inv = player.getInventory();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack s = inv.getItem(i);
                if (isMarkedWith(s)) return s;
            }
        }
        ItemStack carried = player.containerMenu.getCarried();
        if (isMarkedWith(carried)) return carried;
        return null;
    }

    /**
     * Scans the player's inventory for an item bearing {@link #NESTED_FROM_KEY}, which
     * indicates a parent container is waiting to be reopened. Returns the stored slot
     * index, or -1 if not found.
     */
    private int findNestedFromSlot() {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            CustomData data = s.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
                CompoundTag tag = data.copyTag();
                if (tag.contains(NESTED_FROM_KEY)) {
                    return tag.getIntOr(NESTED_FROM_KEY, -1);
                }
            }
        }
        return -1;
    }

    private void clearNestedFromMarker(int parentSlot) {
        ItemStack parentItem = player.getInventory().getItem(parentSlot);
        if (!parentItem.isEmpty()) {
            CustomData.update(DataComponents.CUSTOM_DATA, parentItem, tag -> tag.remove(NESTED_FROM_KEY));
        }
    }

    private boolean isMarkedWith(ItemStack stack) {
        return !stack.isEmpty() && openId.equals(markerOf(stack));
    }

    private static @Nullable String markerOf(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();
        return tag.contains(OPEN_MARKER_KEY) ? tag.getStringOr(OPEN_MARKER_KEY, "") : null;
    }
}
