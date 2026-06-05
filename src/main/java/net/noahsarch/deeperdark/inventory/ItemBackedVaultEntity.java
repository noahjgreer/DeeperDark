package net.noahsarch.deeperdark.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.noahsarch.deeperdark.menu.ModMenus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A VaultBlockEntity backed by the CONTAINER component of a player's inventory item.
 * Uses the same UUID-marker strategy as ItemBackedContainer so the item can be moved
 * between slots while the vault menu remains open.
 */
public class ItemBackedVaultEntity extends VaultBlockEntity {

    private final ServerPlayer player;
    private final String openId;
    private final MenuType<?> menuType;
    private boolean loading = true;

    public ItemBackedVaultEntity(ServerPlayer player, ItemStack sourceStack) {
        super(BlockPos.ZERO, blockStateFor(sourceStack));
        this.player   = player;
        this.openId   = UUID.randomUUID().toString();
        this.menuType = menuTypeFor(sourceStack);

        // Stamp the UUID marker so we can track the item across inventory moves.
        CustomData.update(DataComponents.CUSTOM_DATA, sourceStack,
                tag -> tag.putString(ItemBackedContainer.OPEN_MARKER_KEY, openId));

        // Populate from the CONTAINER component (batched stacks → vault entries).
        ItemContainerContents contents = sourceStack.get(DataComponents.CONTAINER);
        if (contents != null) {
            for (ItemStackTemplate template : contents.nonEmptyItems()) {
                ItemStack s = template.create();
                if (!s.isEmpty()) addItems(s); // addItems guards level==null for sounds
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

    // Open sound is played by Deeperdark.openContainerFromInventory before the menu opens.
    @Override
    public void startOpen(Player player) {}

    @Override
    public void stopOpen(Player player) {
        saveBack();
        ItemStack current = findMarkedItem();
        if (current != null) {
            CustomData.update(DataComponents.CUSTOM_DATA, current, tag -> {
                tag.remove(ItemBackedContainer.OPEN_MARKER_KEY);
                tag.remove(ItemBackedContainer.FROM_SCREEN_MARKER_KEY);
            });
            this.player.inventoryMenu.broadcastChanges();
        }
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5f, 0f);
    }

    private void saveBack() {
        ItemStack target = findMarkedItem();
        if (target == null) return;

        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < getEntryCount(); i++) {
            VaultEntry entry = getEntry(i);
            if (entry == null || entry.count <= 0) continue;
            int batchSize = entry.representative.getMaxStackSize();
            int remaining = entry.count;
            while (remaining > 0) {
                int batch = Math.min(remaining, batchSize);
                stacks.add(entry.representative.copyWithCount(batch));
                remaining -= batch;
            }
        }
        if (stacks.isEmpty()) {
            target.remove(DataComponents.CONTAINER);
        } else {
            target.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(stacks));
        }
        this.player.inventoryMenu.broadcastChanges();
    }

    private ItemStack findMarkedItem() {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (isTrackingItem(s)) return s;
        }
        ItemStack carried = player.containerMenu.getCarried();
        if (isTrackingItem(carried)) return carried;
        return null;
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
