package net.noahsarch.deeperdark.entity;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.EnumSet;

/**
 * A simplified version of TemptGoal that doesn't require entity attributes.
 * Used to make villagers follow players holding emeralds.
 */
public class SimpleTemptGoal extends Goal {
    private final MerchantEntity entity;
    private final double speed;
    private PlayerEntity closestPlayer;
    private int cooldown;

    public SimpleTemptGoal(MerchantEntity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }

        this.closestPlayer = ((net.noahsarch.deeperdark.duck.EntityAccessor)this.entity).deeperdark$getWorld().getClosestPlayer(this.entity, 10.0);
        if (this.closestPlayer == null) {
            return false;
        }

        return this.isTemptedBy(this.closestPlayer.getMainHandStack()) || this.isTemptedBy(this.closestPlayer.getOffHandStack());
    }

    private boolean isTemptedBy(ItemStack stack) {
        return stack.isOf(Items.EMERALD);
    }

    @Override
    public boolean shouldContinue() {
        if (this.canStart()) {
            return true;
        }

        if (this.entity.getNavigation().isIdle()) {
            return false;
        }

        return this.closestPlayer != null && this.entity.squaredDistanceTo(this.closestPlayer) < 36.0;
    }

    @Override
    public void stop() {
        this.closestPlayer = null;
        this.entity.getNavigation().stop();
        this.cooldown = toGoalTicks(100);
    }

    @Override
    public void tick() {
        this.entity.getLookControl().lookAt(this.closestPlayer, this.entity.getMaxHeadRotation() + 20, this.entity.getMaxLookPitchChange());

        if (this.entity.squaredDistanceTo(this.closestPlayer) < 6.25) {
            this.entity.getNavigation().stop();
        } else {
            this.entity.getNavigation().startMovingTo(this.closestPlayer, this.speed);
        }
    }
}
