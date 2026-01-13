/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;

class FoxEntity.PickupItemGoal
extends Goal {
    public FoxEntity.PickupItemGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            return false;
        }
        if (FoxEntity.this.getTarget() != null || FoxEntity.this.getAttacker() != null) {
            return false;
        }
        if (!FoxEntity.this.wantsToPickupItem()) {
            return false;
        }
        if (FoxEntity.this.getRandom().nextInt(FoxEntity.PickupItemGoal.toGoalTicks(10)) != 0) {
            return false;
        }
        List<ItemEntity> list = FoxEntity.this.getEntityWorld().getEntitiesByClass(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
        return !list.isEmpty() && FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
    }

    @Override
    public void tick() {
        List<ItemEntity> list = FoxEntity.this.getEntityWorld().getEntitiesByClass(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
        ItemStack itemStack = FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
        if (itemStack.isEmpty() && !list.isEmpty()) {
            FoxEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
        }
    }

    @Override
    public void start() {
        List<ItemEntity> list = FoxEntity.this.getEntityWorld().getEntitiesByClass(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
        if (!list.isEmpty()) {
            FoxEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
        }
    }
}
