package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromBlockPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import net.noahsarch.deeperdark.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends pick-block (middle-click) in survival to search inside shulker boxes,
 * boxes, and bundles held in the player's inventory when the item isn't found
 * directly in the inventory.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class PickBlockContainerMixin {

    @Shadow
    private ServerPlayer player;

    /**
     * Primary interception point: runs before other mods (e.g. Litematica) can
     * cancel handlePickItemFromBlock and prevent tryPickItem from being reached.
     * Independently resolves the target item from the packet so container search
     * works regardless of what happens in tryPickItem.
     */
    @Inject(method = "handlePickItemFromBlock", at = @At("HEAD"))
    private void deeperdark$pickFromContainersOnBlock(
            ServerboundPickItemFromBlockPacket packet, CallbackInfo ci) {
        // handlePickItemFromBlock is invoked twice per packet: once on the Netty IO thread
        // (before PacketUtils.ensureRunningOnSameThread reschedules it) and once on the server
        // thread. Inventory/world access is only safe on the server thread.
        if (!this.player.level().getServer().isSameThread()) return;

        if (this.player.hasInfiniteMaterials()) return;

        ServerLevel level = this.player.level();
        BlockPos pos = packet.pos();
        if (!this.player.isWithinBlockInteractionRange(pos, 1.0)) return;
        if (!level.isLoaded(pos)) return;

        BlockState blockState = level.getBlockState(pos);
        ItemStack target = blockState.getCloneItemStack(level, pos, false);
        if (target.isEmpty()) return;
        if (!target.isItemEnabled(level.enabledFeatures())) return;

        Inventory inventory = this.player.getInventory();
        if (inventory.findSlotMatchingItem(target) != -1) return;

        boolean found = deeperdark$searchAndExtract(inventory, target);
        if (found) {
            this.player.connection.send(new ClientboundSetHeldSlotPacket(inventory.getSelectedSlot()));
            this.player.inventoryMenu.broadcastChanges();
        }
    }

    @Inject(method = "tryPickItem", at = @At("HEAD"))
    private void deeperdark$pickFromContainers(ItemStack itemStack, CallbackInfo ci) {
        if (!itemStack.isItemEnabled(this.player.level().enabledFeatures()))
            return;
        if (this.player.hasInfiniteMaterials())
            return;

        Inventory inventory = this.player.getInventory();
        // Item is directly in inventory — vanilla will find it; nothing to do.
        if (inventory.findSlotMatchingItem(itemStack) != -1)
            return;

        if (deeperdark$searchAndExtract(inventory, itemStack)) {
            this.player.connection.send(new ClientboundSetHeldSlotPacket(inventory.getSelectedSlot()));
            this.player.inventoryMenu.broadcastChanges();
        }
    }

    private boolean deeperdark$searchAndExtract(Inventory inventory, ItemStack target) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slot = inventory.getItem(i);
            if (slot.isEmpty())
                continue;

            BundleContents bundle = slot.get(DataComponents.BUNDLE_CONTENTS);
            if (bundle != null && !bundle.isEmpty()) {
                if (deeperdark$extractFromBundle(slot, bundle, target, inventory))
                    return true;
            }

            ItemContainerContents container = slot.get(DataComponents.CONTAINER);
            if (container != null) {
                if (deeperdark$isVaultItem(slot)) {
                    if (deeperdark$extractFromVault(slot, container, target, inventory))
                        return true;
                } else if (deeperdark$extractFromContainer(slot, container, target, inventory))
                    return true;
            }
        }
        return false;
    }

    private boolean deeperdark$extractFromBundle(
            ItemStack bundleItem, BundleContents contents, ItemStack target, Inventory inventory) {
        List<ItemStackTemplate> templates = contents.items();
        for (int i = 0; i < templates.size(); i++) {
            ItemStackTemplate tmpl = templates.get(i);
            ItemStack candidate = tmpl.create();
            if (!ItemStack.isSameItemSameComponents(candidate, target))
                continue;
            if (!deeperdark$hasRoomForStack(inventory, target))
                return false;

            // Rebuild the bundle's item list with one fewer of the matched item.
            // Avoids BundleContents.Mutable.toggleSelectedItem, which deselects when
            // selectedItem already equals i (default=0), causing removeOne to return null.
            List<ItemStackTemplate> newTemplates = new ArrayList<>(templates);
            if (tmpl.count() > 1) {
                newTemplates.set(i, tmpl.withCount(tmpl.count() - 1));
            } else {
                newTemplates.remove(i);
            }
            bundleItem.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(newTemplates));
            deeperdark$addAndSelectItem(inventory, candidate.copyWithCount(1), target);
            return true;
        }
        return false;
    }

    private boolean deeperdark$extractFromContainer(
            ItemStack containerItem, ItemContainerContents contents, ItemStack target, Inventory inventory) {
        List<ItemStack> allItems = new ArrayList<>();
        contents.allItemsCopyStream().forEach(allItems::add);

        for (int i = 0; i < allItems.size(); i++) {
            ItemStack slot = allItems.get(i);
            if (slot.isEmpty() || !ItemStack.isSameItemSameComponents(slot, target))
                continue;
            if (!deeperdark$hasRoomForStack(inventory, slot))
                return false;

            ItemStack extracted = slot.copy();
            allItems.set(i, ItemStack.EMPTY);
            containerItem.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(allItems));
            deeperdark$addAndSelectItem(inventory, extracted, target);
            return true;
        }
        return false;
    }

    private boolean deeperdark$extractFromVault(
            ItemStack vaultItem, ItemContainerContents contents, ItemStack target, Inventory inventory) {
        List<ItemStack> allItems = new ArrayList<>();
        contents.allItemsCopyStream().forEach(allItems::add);

        for (int i = 0; i < allItems.size(); i++) {
            ItemStack slot = allItems.get(i);
            if (slot.isEmpty() || !ItemStack.isSameItemSameComponents(slot, target))
                continue;
            if (!deeperdark$hasRoomForStack(inventory, slot))
                return false;

            ItemStack extracted = slot.copy();
            allItems.set(i, ItemStack.EMPTY);
            vaultItem.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(allItems));
            deeperdark$addAndSelectItem(inventory, extracted, target);
            return true;
        }
        return false;
    }

    private static boolean deeperdark$isVaultItem(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }
        return blockItem.getBlock() == ModBlocks.SMALL_ITEM_VAULT
                || blockItem.getBlock() == ModBlocks.MEDIUM_ITEM_VAULT
                || blockItem.getBlock() == ModBlocks.LARGE_ITEM_VAULT;
    }

    /**
     * Returns true if the full stack can fit in the main inventory.
     */
    private static boolean deeperdark$hasRoomForStack(Inventory inventory, ItemStack stack) {
        int remaining = stack.getCount();
        int maxStack = stack.getMaxStackSize();
        for (int i = 0; i < 36; i++) {
            ItemStack s = inventory.getItem(i);
            if (s.isEmpty()) {
                remaining -= maxStack;
            } else if (ItemStack.isSameItemSameComponents(s, stack)) {
                remaining -= Math.max(0, s.getMaxStackSize() - s.getCount());
            }
            if (remaining <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the extracted stack, then selects a matching hotbar slot if present.
     */
    private static void deeperdark$addAndSelectItem(Inventory inventory, ItemStack extracted, ItemStack target) {
        inventory.add(extracted);
        int slotWithItem = inventory.findSlotMatchingItem(target);
        if (slotWithItem != -1) {
            if (Inventory.isHotbarSlot(slotWithItem)) {
                inventory.setSelectedSlot(slotWithItem);
            } else {
                inventory.pickSlot(slotWithItem);
            }
        }
    }
}
