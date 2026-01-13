/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import java.util.Optional;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

static class BrewingStandScreenHandler.PotionSlot
extends Slot {
    public BrewingStandScreenHandler.PotionSlot(Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return BrewingStandScreenHandler.PotionSlot.matches(stack);
    }

    @Override
    public int getMaxItemCount() {
        return 1;
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        Optional<RegistryEntry<Potion>> optional = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).potion();
        if (optional.isPresent() && player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            Criteria.BREWED_POTION.trigger(serverPlayerEntity, optional.get());
        }
        super.onTakeItem(player, stack);
    }

    public static boolean matches(ItemStack stack) {
        return stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.GLASS_BOTTLE);
    }

    @Override
    public Identifier getBackgroundSprite() {
        return EMPTY_POTION_SLOT_TEXTURE;
    }
}
