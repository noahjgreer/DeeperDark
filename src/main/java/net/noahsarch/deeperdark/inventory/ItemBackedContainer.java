package net.noahsarch.deeperdark.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
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
 */
public class ItemBackedContainer extends SimpleContainer {

    /** Key used inside CUSTOM_DATA to hold the open-session UUID. */
    public static final String OPEN_MARKER_KEY = "deeperdark_open";

    /** Stamped only when the container is opened from the inventory screen (not from right-click in hand). */
    public static final String FROM_SCREEN_MARKER_KEY = "deeperdark_from_screen";

    private final ServerPlayer player;
    private final String openId;
    private final @Nullable SoundEvent closeSound;

    private ItemBackedContainer(ServerPlayer player, ItemStack sourceStack, int size, @Nullable SoundEvent closeSound) {
        super(size);
        this.player = player;
        this.openId = UUID.randomUUID().toString();
        this.closeSound = closeSound;

        // Stamp the item with our UUID so we can find it even after it is split/copied
        // when the player drags it to a different inventory slot.
        CustomData.update(DataComponents.CUSTOM_DATA, sourceStack, tag -> tag.putString(OPEN_MARKER_KEY, openId));

        // Load directly into the backing list to bypass setChanged during initial load.
        NonNullList<ItemStack> loaded = NonNullList.withSize(size, ItemStack.EMPTY);
        sourceStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(loaded);
        for (int i = 0; i < size; i++) {
            this.items.set(i, loaded.get(i));
        }
    }

    public static ItemBackedContainer of(ServerPlayer player, int inventorySlot, int size, @Nullable SoundEvent closeSound) {
        ItemStack stack = player.getInventory().getItem(inventorySlot);
        return new ItemBackedContainer(player, stack, size, closeSound);
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
        // Remove the open marker so the item no longer looks "in use".
        ItemStack current = findMarkedItem();
        if (current != null) {
            CustomData.update(DataComponents.CUSTOM_DATA, current, tag -> {
                tag.remove(OPEN_MARKER_KEY);
                tag.remove(FROM_SCREEN_MARKER_KEY);
            });
            player.inventoryMenu.broadcastChanges();
        }
    }

    private void saveBack() {
        ItemStack target = findMarkedItem();
        if (target == null) return;
        List<ItemStack> list = new ArrayList<>(getContainerSize());
        for (int i = 0; i < getContainerSize(); i++) list.add(getItem(i).copy());
        target.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));
        player.inventoryMenu.broadcastChanges();
    }

    /**
     * Locates the item bearing our open-marker UUID.
     * Checks both the player's inventory slots and the carried (cursor) item, since
     * the player may be mid-drag with the item on the cursor.
     */
    private @Nullable ItemStack findMarkedItem() {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (isMarkedWith(s)) return s;
        }
        ItemStack carried = player.containerMenu.getCarried();
        if (isMarkedWith(carried)) return carried;
        return null;
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
