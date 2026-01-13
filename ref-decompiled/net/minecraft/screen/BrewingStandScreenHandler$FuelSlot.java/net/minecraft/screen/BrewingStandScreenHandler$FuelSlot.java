/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

static class BrewingStandScreenHandler.FuelSlot
extends Slot {
    public BrewingStandScreenHandler.FuelSlot(Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return BrewingStandScreenHandler.FuelSlot.matches(stack);
    }

    public static boolean matches(ItemStack stack) {
        return stack.isIn(ItemTags.BREWING_FUEL);
    }

    @Override
    public Identifier getBackgroundSprite() {
        return EMPTY_BREWING_FUEL_SLOT_TEXTURE;
    }
}
