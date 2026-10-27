package net.noahsarch.deeperdark.event;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.noahsarch.deeperdark.duck.CollarHolder;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import net.noahsarch.deeperdark.item.CollarItem;

/**
 * Hopper trinket: items the player picks up are routed straight into container items
 * in their inventory that already hold that exact item, so a carried vault or box keeps
 * absorbing its stock without a manual transfer.
 *
 * <p>Routing is deliberately match-only — a container never claims a free slot for an item
 * it wasn't already stocking, so picking something up for the first time behaves normally.
 */
public final class HopperTrinketHandler {

    private HopperTrinketHandler() {}

    /** Returns true if the player is wearing a collar with a hopper trinket. */
    public static boolean hasHopperTrinket(Player player) {
        if (!(player instanceof CollarHolder holder)) return false;
        ItemStack collar = holder.deeperdark$getCollarItem();
        if (collar.isEmpty() || !(collar.getItem() instanceof CollarItem)) return false;
        return collar.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
            .nonEmptyItemCopyStream().anyMatch(s -> s.is(Items.HOPPER));
    }

    /**
     * Routes as much of {@code stack} as possible into matching container items in the
     * player's inventory, shrinking it by the amount moved. Server-side only.
     *
     * @return the number of items deposited
     */
    public static int route(Player player, ItemStack stack) {
        if (player.level().isClientSide() || stack.isEmpty()) return 0;
        if (!hasHopperTrinket(player)) return 0;
        // Containers refuse to nest inside one another; don't try to route one into another.
        if (isContainerItem(stack)) return 0;

        int moved = 0;
        for (ItemStack candidate : player.getInventory().getNonEquipmentItems()) {
            if (stack.isEmpty()) break;
            if (candidate.isEmpty() || candidate == stack) continue;
            // Skip a container the player currently has open — its live contents live in the
            // open menu, not the component, so writing here would be overwritten on close.
            if (ContainerItemUtil.isOpen(candidate)) continue;

            if (ContainerItemUtil.isVaultItem(candidate)) {
                moved += ContainerItemUtil.insertMatchingOnlyVault(candidate, stack);
                continue;
            }

            int size = ContainerItemUtil.getContainerSize(candidate);
            if (size > 0) {
                moved += ContainerItemUtil.insertMatchingOnly(candidate, stack, size);
            }
        }

        if (moved > 0 && player.level() instanceof ServerLevel level) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BUBBLE_POP, SoundSource.PLAYERS, 1.0f, 0.5f);
        }
        return moved;
    }

    private static boolean isContainerItem(ItemStack stack) {
        return ContainerItemUtil.isVaultItem(stack) || ContainerItemUtil.getContainerSize(stack) > 0;
    }
}
