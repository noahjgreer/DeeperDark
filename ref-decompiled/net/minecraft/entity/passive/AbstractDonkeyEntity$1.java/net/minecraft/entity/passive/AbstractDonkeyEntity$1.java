/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

class AbstractDonkeyEntity.1
implements StackReference {
    AbstractDonkeyEntity.1() {
    }

    @Override
    public ItemStack get() {
        return AbstractDonkeyEntity.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
    }

    @Override
    public boolean set(ItemStack stack) {
        if (stack.isEmpty()) {
            if (AbstractDonkeyEntity.this.hasChest()) {
                AbstractDonkeyEntity.this.setHasChest(false);
                AbstractDonkeyEntity.this.onChestedStatusChanged();
            }
            return true;
        }
        if (stack.isOf(Items.CHEST)) {
            if (!AbstractDonkeyEntity.this.hasChest()) {
                AbstractDonkeyEntity.this.setHasChest(true);
                AbstractDonkeyEntity.this.onChestedStatusChanged();
            }
            return true;
        }
        return false;
    }
}
