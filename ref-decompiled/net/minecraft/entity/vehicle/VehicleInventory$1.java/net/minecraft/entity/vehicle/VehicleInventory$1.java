/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.vehicle;

import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;

class VehicleInventory.1
implements StackReference {
    final /* synthetic */ int field_38209;

    VehicleInventory.1() {
        this.field_38209 = i;
    }

    @Override
    public ItemStack get() {
        return VehicleInventory.this.getInventoryStack(this.field_38209);
    }

    @Override
    public boolean set(ItemStack stack) {
        VehicleInventory.this.setInventoryStack(this.field_38209, stack);
        return true;
    }
}
