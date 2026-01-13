/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;

static class StackReference.2
implements StackReference {
    final /* synthetic */ LivingEntity field_64136;
    final /* synthetic */ EquipmentSlot field_64137;
    final /* synthetic */ Predicate field_64138;

    StackReference.2() {
        this.field_64136 = livingEntity;
        this.field_64137 = equipmentSlot;
        this.field_64138 = predicate;
    }

    @Override
    public ItemStack get() {
        return this.field_64136.getEquippedStack(this.field_64137);
    }

    @Override
    public boolean set(ItemStack stack) {
        if (!this.field_64138.test(stack)) {
            return false;
        }
        this.field_64136.equipStack(this.field_64137, stack);
        return true;
    }
}
