package net.noahsarch.deeperdark.entity;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.EnumSet;
import net.minecraft.world.entity.npc.villager.AbstractVillager;

/**
 * A simplified version of TemptGoal that doesn't require entity attributes.
 * Used to make villagers follow players holding emeralds.
 */
public class SimpleTemptGoal extends Goal {
    private final AbstractVillager entity;
    private final double speed;
    private Player closestPlayer;
    private int cooldown;

    public SimpleTemptGoal(AbstractVillager entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }

        this.closestPlayer = ((net.noahsarch.deeperdark.duck.EntityAccessor)this.entity).deeperdark$getWorld().getNearestPlayer(this.entity, 10.0);
        if (this.closestPlayer == null) {
            return false;
        }

        return isTemptedBy(this.closestPlayer.getMainHandItem()) || isTemptedBy(this.closestPlayer.getOffhandItem());
    }

    private boolean isTemptedBy(ItemStack stack) {
        return stack.getItem() == Items.EMERALD;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.canUse()) {
            return true;
        }

        if (!this.entity.getNavigation().isInProgress()) {
            return false;
        }

        return this.closestPlayer != null && this.entity.distanceToSqr(this.closestPlayer) < 36.0;
    }

    @Override
    public void stop() {
        this.closestPlayer = null;
        this.entity.getNavigation().stop();
        this.cooldown = adjustedTickDelay(100);
    }

    @Override
    public void tick() {
        this.entity.getLookControl().setLookAt(this.closestPlayer, this.entity.getMaxHeadYRot() + 20, this.entity.getMaxHeadXRot());

        if (this.entity.distanceToSqr(this.closestPlayer) < 6.25) {
            this.entity.getNavigation().stop();
        } else {
            this.entity.getNavigation().moveTo(this.closestPlayer, this.speed);
        }
    }
}
