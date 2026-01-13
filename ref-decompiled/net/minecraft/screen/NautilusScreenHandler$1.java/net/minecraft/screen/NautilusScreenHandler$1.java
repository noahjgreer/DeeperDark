/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractNautilusEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.NautilusScreenHandler;
import net.minecraft.screen.slot.ArmorSlot;
import net.minecraft.util.Identifier;

class NautilusScreenHandler.1
extends ArmorSlot {
    final /* synthetic */ AbstractNautilusEntity field_64493;

    NautilusScreenHandler.1(NautilusScreenHandler nautilusScreenHandler, Inventory inventory, LivingEntity livingEntity, EquipmentSlot equipmentSlot, int i, int j, int k, Identifier identifier, AbstractNautilusEntity abstractNautilusEntity) {
        this.field_64493 = abstractNautilusEntity;
        super(inventory, livingEntity, equipmentSlot, i, j, k, identifier);
    }

    @Override
    public boolean isEnabled() {
        return this.field_64493.canUseSlot(EquipmentSlot.SADDLE);
    }
}
