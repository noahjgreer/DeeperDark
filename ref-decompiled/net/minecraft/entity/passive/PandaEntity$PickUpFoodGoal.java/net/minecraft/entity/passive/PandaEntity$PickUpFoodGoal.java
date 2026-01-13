/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.item.ItemStack;

class PandaEntity.PickUpFoodGoal
extends Goal {
    private int startAge;

    public PandaEntity.PickUpFoodGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.startAge > PandaEntity.this.age || PandaEntity.this.isBaby() || PandaEntity.this.isTouchingWater() || !PandaEntity.this.isIdle() || PandaEntity.this.getAskForBambooTicks() > 0) {
            return false;
        }
        if (!PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            return true;
        }
        return !PandaEntity.this.getEntityWorld().getEntitiesByClass(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(6.0, 6.0, 6.0), PandaEntity::canEatFromGround).isEmpty();
    }

    @Override
    public boolean shouldContinue() {
        if (PandaEntity.this.isTouchingWater() || !PandaEntity.this.isLazy() && PandaEntity.this.random.nextInt(PandaEntity.PickUpFoodGoal.toGoalTicks(600)) == 1) {
            return false;
        }
        return PandaEntity.this.random.nextInt(PandaEntity.PickUpFoodGoal.toGoalTicks(2000)) != 1;
    }

    @Override
    public void tick() {
        if (!PandaEntity.this.isSitting() && !PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            PandaEntity.this.stop();
        }
    }

    @Override
    public void start() {
        if (PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            List<ItemEntity> list = PandaEntity.this.getEntityWorld().getEntitiesByClass(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PandaEntity::canEatFromGround);
            if (!list.isEmpty()) {
                PandaEntity.this.getNavigation().startMovingTo(list.getFirst(), 1.2f);
            }
        } else {
            PandaEntity.this.stop();
        }
        this.startAge = 0;
    }

    @Override
    public void stop() {
        ItemStack itemStack = PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!itemStack.isEmpty()) {
            PandaEntity.this.dropStack(PandaEntity.PickUpFoodGoal.castToServerWorld(PandaEntity.this.getEntityWorld()), itemStack);
            PandaEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            int i = PandaEntity.this.isLazy() ? PandaEntity.this.random.nextInt(50) + 10 : PandaEntity.this.random.nextInt(150) + 10;
            this.startAge = PandaEntity.this.age + i * 20;
        }
        PandaEntity.this.setSitting(false);
    }
}
