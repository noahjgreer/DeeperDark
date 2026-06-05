package net.noahsarch.deeperdark.inventory;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Wraps any Container and attaches a UUID marker to a player's inventory item.
 * Provides stillValid() tracking and stopOpen() cleanup without reading/writing
 * the CONTAINER component — used for ender chests whose data is managed externally.
 */
public class OpenMarkedContainer implements Container {

    private final Container delegate;
    private final ServerPlayer player;
    private final String openId;
    private final @Nullable SoundEvent closeSound;

    public OpenMarkedContainer(Container delegate, ServerPlayer player,
                               ItemStack itemToMark, @Nullable SoundEvent closeSound) {
        this.delegate = delegate;
        this.player = player;
        this.openId = UUID.randomUUID().toString();
        this.closeSound = closeSound;
        CustomData.update(DataComponents.CUSTOM_DATA, itemToMark,
                tag -> tag.putString(ItemBackedContainer.OPEN_MARKER_KEY, openId));
    }

    public boolean isTrackingItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return false;
        CompoundTag tag = data.copyTag();
        return tag.contains(ItemBackedContainer.OPEN_MARKER_KEY)
                && openId.equals(tag.getStringOr(ItemBackedContainer.OPEN_MARKER_KEY, ""));
    }

    @Override
    public boolean stillValid(Player p) {
        if (p != player) return false;
        return findMarkedItem() != null;
    }

    @Override
    public void startOpen(ContainerUser user) { delegate.startOpen(user); }

    @Override
    public void stopOpen(ContainerUser user) {
        delegate.stopOpen(user);
        if (closeSound != null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    closeSound, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        ItemStack current = findMarkedItem();
        if (current != null) {
            CustomData.update(DataComponents.CUSTOM_DATA, current, tag -> {
                tag.remove(ItemBackedContainer.OPEN_MARKER_KEY);
                tag.remove(ItemBackedContainer.FROM_SCREEN_MARKER_KEY);
            });
            player.inventoryMenu.broadcastChanges();
        }
    }

    private @Nullable ItemStack findMarkedItem() {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (isTrackingItem(s)) return s;
        }
        ItemStack carried = player.containerMenu.getCarried();
        if (isTrackingItem(carried)) return carried;
        return null;
    }

    @Override public int getContainerSize() { return delegate.getContainerSize(); }
    @Override public boolean isEmpty() { return delegate.isEmpty(); }
    @Override public ItemStack getItem(int slot) { return delegate.getItem(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { return delegate.removeItem(slot, amount); }
    @Override public ItemStack removeItemNoUpdate(int slot) { return delegate.removeItemNoUpdate(slot); }
    @Override public void setItem(int slot, ItemStack stack) { delegate.setItem(slot, stack); }
    @Override public void setChanged() { delegate.setChanged(); }
    @Override public void clearContent() { delegate.clearContent(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return delegate.canPlaceItem(slot, stack); }
}
