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

class HorseScreenHandler.2
extends ArmorSlot {
    final /* synthetic */ AbstractHorseEntity field_56446;
    final /* synthetic */ boolean field_56447;

    HorseScreenHandler.2(HorseScreenHandler horseScreenHandler, Inventory inventory, LivingEntity livingEntity, EquipmentSlot equipmentSlot, int i, int j, int k, Identifier identifier, AbstractHorseEntity abstractHorseEntity, boolean bl) {
        this.field_56446 = abstractHorseEntity;
        this.field_56447 = bl;
        super(inventory, livingEntity, equipmentSlot, i, j, k, identifier);
    }

    @Override
    public boolean isEnabled() {
        return this.field_56446.canUseSlot(EquipmentSlot.BODY) && (this.field_56446.getType().isIn(EntityTypeTags.CAN_WEAR_HORSE_ARMOR) || this.field_56447);
    }
}
