package net.noahsarch.deeperdark.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import net.noahsarch.deeperdark.block.ModBlocks;
import net.noahsarch.deeperdark.block.VaultBlockEntity;
import net.noahsarch.deeperdark.component.ModComponents;
import net.noahsarch.deeperdark.menu.ModMenus;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A VaultBlockEntity backed by the CONTAINER component of a player's inventory item
 * (top-level) or a slot inside another open container (nested).
 * Uses the same UUID-marker strategy as ItemBackedContainer so the item can be moved
 * between slots while the vault menu remains open.
 */
public class ItemBackedVaultEntity extends VaultBlockEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemBackedVaultEntity.class);

    private final ServerPlayer player;
    private final String openId;
    private final MenuType<?> menuType;
    private boolean loading = true;

    /** Non-null when this vault was opened from inside another open container. */
    private final @Nullable Container parentStorage;

    /** Slot index inside parentStorage, or -1 for top-level vaults. */
    private final int slotInParent;

    /** Guard against double-invocation of stopOpen (stale echo close packets). */
    private boolean stopOpenCalled = false;

    /** Opens a vault item held directly in the player's inventory. */
    public ItemBackedVaultEntity(ServerPlayer player, ItemStack sourceStack) {
        this(player, sourceStack, null, -1);
    }

    /** Opens a vault item that lives inside another open container (nested). */
    public ItemBackedVaultEntity(ServerPlayer player, Container parentStorage, int slotInParent) {
        this(player, parentStorage.getItem(slotInParent), parentStorage, slotInParent);
    }

    private ItemBackedVaultEntity(ServerPlayer player, ItemStack sourceStack,
                                   @Nullable Container parentStorage, int slotInParent) {
        super(BlockPos.ZERO, blockStateFor(sourceStack));
        this.player   = player;
        this.openId   = UUID.randomUUID().toString();
        this.menuType = menuTypeFor(sourceStack);
        this.parentStorage = parentStorage;
        this.slotInParent  = slotInParent;

        // Stamp the UUID marker so we can track the item across inventory moves.
        CustomData.update(DataComponents.CUSTOM_DATA, sourceStack,
                tag -> tag.putString(ItemBackedContainer.OPEN_MARKER_KEY, openId));

        // Populate from vault_entries (lossless); fall back to CONTAINER for legacy items.
        List<VaultEntry> stored = sourceStack.get(ModComponents.VAULT_ENTRIES);
        if (stored != null) {
            for (VaultEntry e : stored) {
                addItems(e.representative.copyWithCount(e.count));
            }
        } else {
            ItemContainerContents contents = sourceStack.get(DataComponents.CONTAINER);
            if (contents != null) {
                for (ItemStackTemplate template : contents.nonEmptyItems()) {
                    ItemStack s = template.create();
                    if (!s.isEmpty()) addItems(s);
                }
            }
        }
        loading = false;
    }

    public MenuType<?> getVaultMenuType() { return menuType; }

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
        return p == player && findMarkedItem() != null;
    }

    // Suppress the block-entity update path; just write back to the item.
    @Override
    public void setChanged() {
        if (!loading) saveBack();
    }

    @Override
    public void startOpen(Player player) {}

    @Override
    public void stopOpen(Player player) {
        if (stopOpenCalled) return;
        stopOpenCalled = true;

        try {
            saveBack();
        } catch (Exception e) {
            LOGGER.error("[DD-vault] saveBack failed in stopOpen — closing anyway to avoid stuck state: {}", e.getMessage());
        }

        ItemStack current = findMarkedItem();
        if (current != null) {
            CustomData.update(DataComponents.CUSTOM_DATA, current, tag -> {
                tag.remove(ItemBackedContainer.OPEN_MARKER_KEY);
                tag.remove(ItemBackedContainer.FROM_SCREEN_MARKER_KEY);
            });
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5f, 0f);

        if (parentStorage != null) {
            if (slotInParent >= 0) {
                parentStorage.setChanged();
            }
            int parentSlot = findNestedFromSlot();
            if (parentSlot >= 0) {
                clearNestedFromMarker(parentSlot);
                net.noahsarch.deeperdark.Deeperdark.PENDING_PARENT_REOPENS.put(player.getUUID(), parentSlot);
            } else {
                this.player.inventoryMenu.broadcastChanges();
            }
        } else {
            this.player.inventoryMenu.broadcastChanges();
        }
    }

    private void saveBack() {
        ItemStack target = findMarkedItem();
        if (target == null) return;

        // Snapshot live entries into new VaultEntry instances for safe component storage.
        List<VaultEntry> snapshot = new ArrayList<>();
        for (int i = 0; i < getEntryCount(); i++) {
            VaultEntry e = getEntry(i);
            if (e == null || e.count <= 0) continue;
            snapshot.add(new VaultEntry(e.representative, e.count));
        }

        if (snapshot.isEmpty()) {
            target.remove(ModComponents.VAULT_ENTRIES);
            target.remove(DataComponents.CONTAINER);
        } else {
            // Write lossless full data.
            target.set(ModComponents.VAULT_ENTRIES, snapshot);

            // Regenerate capped CONTAINER preview (≤256 stacks) for tooltip mods.
            List<ItemStack> preview = new ArrayList<>();
            outer:
            for (VaultEntry e : snapshot) {
                int batchSize = e.representative.getMaxStackSize();
                int remaining = e.count;
                while (remaining > 0) {
                    if (preview.size() >= 256) break outer;
                    int batch = Math.min(remaining, batchSize);
                    preview.add(e.representative.copyWithCount(batch));
                    remaining -= batch;
                }
            }
            target.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(preview));
        }
        this.player.inventoryMenu.broadcastChanges();
    }

    private @Nullable ItemStack findMarkedItem() {
        if (parentStorage != null) {
            for (int i = 0; i < parentStorage.getContainerSize(); i++) {
                ItemStack s = parentStorage.getItem(i);
                if (isTrackingItem(s)) return s;
            }
        } else {
            Inventory inv = player.getInventory();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack s = inv.getItem(i);
                if (isTrackingItem(s)) return s;
            }
        }
        ItemStack carried = player.containerMenu.getCarried();
        if (isTrackingItem(carried)) return carried;
        return null;
    }

    private int findNestedFromSlot() {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            CustomData data = s.get(DataComponents.CUSTOM_DATA);
            if (data != null && data.copyTag().contains(ItemBackedContainer.NESTED_FROM_KEY)) return i;
        }
        return -1;
    }

    private void clearNestedFromMarker(int parentSlot) {
        ItemStack parentItem = player.getInventory().getItem(parentSlot);
        if (!parentItem.isEmpty()) {
            CustomData.update(DataComponents.CUSTOM_DATA, parentItem,
                    tag -> tag.remove(ItemBackedContainer.NESTED_FROM_KEY));
        }
    }

    private static BlockState blockStateFor(ItemStack stack) {
        if (stack.is(ModBlocks.SMALL_ITEM_VAULT.asItem()))  return ModBlocks.SMALL_ITEM_VAULT.defaultBlockState();
        if (stack.is(ModBlocks.MEDIUM_ITEM_VAULT.asItem())) return ModBlocks.MEDIUM_ITEM_VAULT.defaultBlockState();
        return ModBlocks.LARGE_ITEM_VAULT.defaultBlockState();
    }

    private static MenuType<?> menuTypeFor(ItemStack stack) {
        if (stack.is(ModBlocks.SMALL_ITEM_VAULT.asItem()))  return ModMenus.SMALL_VAULT;
        if (stack.is(ModBlocks.MEDIUM_ITEM_VAULT.asItem())) return ModMenus.MEDIUM_VAULT;
        return ModMenus.LARGE_VAULT;
    }
}
