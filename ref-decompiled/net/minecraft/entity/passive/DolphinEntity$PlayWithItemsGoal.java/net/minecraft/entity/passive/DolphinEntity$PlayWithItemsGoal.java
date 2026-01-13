/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

class DolphinEntity.PlayWithItemsGoal
extends Goal {
    private int nextPlayingTime;

    DolphinEntity.PlayWithItemsGoal() {
    }

    @Override
    public boolean canStart() {
        if (this.nextPlayingTime > DolphinEntity.this.age) {
            return false;
        }
        List<ItemEntity> list = DolphinEntity.this.getEntityWorld().getEntitiesByClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), CAN_TAKE);
        return !list.isEmpty() || !DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
    }

    @Override
    public void start() {
        List<ItemEntity> list = DolphinEntity.this.getEntityWorld().getEntitiesByClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), CAN_TAKE);
        if (!list.isEmpty()) {
            DolphinEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
            DolphinEntity.this.playSound(SoundEvents.ENTITY_DOLPHIN_PLAY, 1.0f, 1.0f);
        }
        this.nextPlayingTime = 0;
    }

    @Override
    public void stop() {
        ItemStack itemStack = DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!itemStack.isEmpty()) {
            this.spitOutItem(itemStack);
            DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.nextPlayingTime = DolphinEntity.this.age + DolphinEntity.this.random.nextInt(100);
        }
    }

    @Override
    public void tick() {
        List<ItemEntity> list = DolphinEntity.this.getEntityWorld().getEntitiesByClass(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), CAN_TAKE);
        ItemStack itemStack = DolphinEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!itemStack.isEmpty()) {
            this.spitOutItem(itemStack);
            DolphinEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        } else if (!list.isEmpty()) {
            DolphinEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
        }
    }

    private void spitOutItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        double d = DolphinEntity.this.getEyeY() - (double)0.3f;
        ItemEntity itemEntity = new ItemEntity(DolphinEntity.this.getEntityWorld(), DolphinEntity.this.getX(), d, DolphinEntity.this.getZ(), stack);
        itemEntity.setPickupDelay(40);
        itemEntity.setThrower(DolphinEntity.this);
        float f = 0.3f;
        float g = DolphinEntity.this.random.nextFloat() * ((float)Math.PI * 2);
        float h = 0.02f * DolphinEntity.this.random.nextFloat();
        itemEntity.setVelocity(0.3f * -MathHelper.sin(DolphinEntity.this.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(DolphinEntity.this.getPitch() * ((float)Math.PI / 180)) + MathHelper.cos(g) * h, 0.3f * MathHelper.sin(DolphinEntity.this.getPitch() * ((float)Math.PI / 180)) * 1.5f, 0.3f * MathHelper.cos(DolphinEntity.this.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(DolphinEntity.this.getPitch() * ((float)Math.PI / 180)) + MathHelper.sin(g) * h);
        DolphinEntity.this.getEntityWorld().spawnEntity(itemEntity);
    }
}
