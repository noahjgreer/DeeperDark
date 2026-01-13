/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;

class MobEntity.1
implements SingleStackInventory {
    final /* synthetic */ EquipmentSlot field_55954;

    MobEntity.1() {
        this.field_55954 = equipmentSlot;
    }

    @Override
    public ItemStack getStack() {
        return MobEntity.this.getEquippedStack(this.field_55954);
    }

    @Override
    public void setStack(ItemStack stack) {
        MobEntity.this.equipStack(this.field_55954, stack);
        if (!stack.isEmpty()) {
            MobEntity.this.setDropGuaranteed(this.field_55954);
            MobEntity.this.setPersistent();
        }
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.getVehicle() == MobEntity.this || player.canInteractWithEntity(MobEntity.this, 4.0);
    }
}
