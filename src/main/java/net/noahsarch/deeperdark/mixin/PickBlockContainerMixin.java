package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromBlockPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
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

    /**
     * Scans every container item in the player's inventory for the target item.
     * Prefers the smallest available stack to minimise depletion of large stacks
     * and to maximise the chance of fitting into a nearly-full inventory.
     * Bundles always contribute one item at a time regardless of template count.
     */
    private boolean deeperdark$searchAndExtract(Inventory inventory, ItemStack target) {
        // Pass 1 — find the candidate with the smallest count that actually fits.
        int bestInvSlot = -1;
        int bestInnerSlot = -1;
        int bestCount = Integer.MAX_VALUE;
        boolean bestIsBundle = false;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack holder = inventory.getItem(i);
            if (holder.isEmpty()) continue;

            // Bundles always yield count=1; treat them as a 1-item candidate.
            if (bestCount > 1) {
                BundleContents bundle = holder.get(DataComponents.BUNDLE_CONTENTS);
                if (bundle != null && !bundle.isEmpty()) {
                    List<ItemStackTemplate> templates = bundle.items();
                    for (int j = 0; j < templates.size(); j++) {
                        if (ItemStack.isSameItemSameComponents(templates.get(j).create(), target)) {
                            if (deeperdark$hasRoomForStack(inventory, target.copyWithCount(1))) {
                                bestInvSlot = i;
                                bestInnerSlot = j;
                                bestCount = 1;
                                bestIsBundle = true;
                            }
                            break;
                        }
                    }
                }
            }

            ItemContainerContents contents = holder.get(DataComponents.CONTAINER);
            if (contents != null) {
                List<ItemStack> items = new ArrayList<>();
                contents.allItemsCopyStream().forEach(items::add);
                for (int j = 0; j < items.size(); j++) {
                    ItemStack slot = items.get(j);
                    if (slot.isEmpty() || !ItemStack.isSameItemSameComponents(slot, target)) continue;
                    int count = slot.getCount();
                    if (count < bestCount && deeperdark$hasRoomForStack(inventory, slot)) {
                        bestInvSlot = i;
                        bestInnerSlot = j;
                        bestCount = count;
                        bestIsBundle = false;
                        if (count == 1) break;
                    }
                }
            }

            if (bestCount == 1) break;
        }

        if (bestInvSlot == -1) return false;

        // Pass 2 — extract from the identified slot.
        ItemStack holder = inventory.getItem(bestInvSlot);

        if (bestIsBundle) {
            BundleContents bundle = holder.get(DataComponents.BUNDLE_CONTENTS);
            if (bundle == null) return false;
            List<ItemStackTemplate> templates = bundle.items();
            if (bestInnerSlot >= templates.size()) return false;
            ItemStackTemplate tmpl = templates.get(bestInnerSlot);
            List<ItemStackTemplate> newTemplates = new ArrayList<>(templates);
            if (tmpl.count() > 1) {
                newTemplates.set(bestInnerSlot, tmpl.withCount(tmpl.count() - 1));
            } else {
                newTemplates.remove(bestInnerSlot);
            }
            holder.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(newTemplates));
            deeperdark$addAndSelectItem(inventory, tmpl.create().copyWithCount(1), target);
            return true;
        }

        // Regular container (shulker, custom box, vault — all use CONTAINER component).
        ItemContainerContents contents = holder.get(DataComponents.CONTAINER);
        if (contents == null) return false;
        List<ItemStack> allItems = new ArrayList<>();
        contents.allItemsCopyStream().forEach(allItems::add);
        if (bestInnerSlot >= allItems.size()) return false;
        ItemStack extracted = allItems.get(bestInnerSlot).copy();
        allItems.set(bestInnerSlot, ItemStack.EMPTY);
        holder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(allItems));
        deeperdark$addAndSelectItem(inventory, extracted, target);
        return true;
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
