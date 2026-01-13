/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.slot.ArmorSlot;
import net.minecraft.util.Identifier;

class HorseScreenHandler.1
extends ArmorSlot {
    final /* synthetic */ AbstractHorseEntity field_56295;

    HorseScreenHandler.1(HorseScreenHandler horseScreenHandler, Inventory inventory, LivingEntity livingEntity, EquipmentSlot equipmentSlot, int i, int j, int k, Identifier identifier, AbstractHorseEntity abstractHorseEntity) {
        this.field_56295 = abstractHorseEntity;
        super(inventory, livingEntity, equipmentSlot, i, j, k, identifier);
    }

    @Override
    public boolean isEnabled() {
        return this.field_56295.canUseSlot(EquipmentSlot.SADDLE) && this.field_56295.getType().isIn(EntityTypeTags.CAN_EQUIP_SADDLE);
    }
}
