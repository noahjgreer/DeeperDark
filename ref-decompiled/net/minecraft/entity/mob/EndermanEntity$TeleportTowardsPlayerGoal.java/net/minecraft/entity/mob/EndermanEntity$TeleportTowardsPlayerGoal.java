/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jspecify.annotations.Nullable;

static class EndermanEntity.TeleportTowardsPlayerGoal
extends ActiveTargetGoal<PlayerEntity> {
    private final EndermanEntity enderman;
    private @Nullable PlayerEntity targetPlayer;
    private int lookAtPlayerWarmup;
    private int ticksSinceUnseenTeleport;
    private final TargetPredicate staringPlayerPredicate;
    private final TargetPredicate validTargetPredicate = TargetPredicate.createAttackable().ignoreVisibility();
    private final TargetPredicate.EntityPredicate angerPredicate;

    public EndermanEntity.TeleportTowardsPlayerGoal(EndermanEntity enderman, @Nullable TargetPredicate.EntityPredicate targetPredicate) {
        super(enderman, PlayerEntity.class, 10, false, false, targetPredicate);
        this.enderman = enderman;
        this.angerPredicate = (playerEntity, world) -> (enderman.isPlayerStaring((PlayerEntity)playerEntity) || enderman.shouldAngerAt(playerEntity, world)) && !enderman.hasPassengerDeep(playerEntity);
        this.staringPlayerPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(this.angerPredicate);
    }

    @Override
    public boolean canStart() {
        this.targetPlayer = EndermanEntity.TeleportTowardsPlayerGoal.getServerWorld(this.enderman).getClosestPlayer(this.staringPlayerPredicate.setBaseMaxDistance(this.getFollowRange()), this.enderman);
        return this.targetPlayer != null;
    }

    @Override
    public void start() {
        this.lookAtPlayerWarmup = this.getTickCount(5);
        this.ticksSinceUnseenTeleport = 0;
        this.enderman.setProvoked();
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        super.stop();
    }

    @Override
    public boolean shouldContinue() {
        if (this.targetPlayer != null) {
            if (!this.angerPredicate.test(this.targetPlayer, EndermanEntity.TeleportTowardsPlayerGoal.getServerWorld(this.enderman))) {
                return false;
            }
            this.enderman.lookAtEntity(this.targetPlayer, 10.0f, 10.0f);
            return true;
        }
        if (this.targetEntity != null) {
            if (this.enderman.hasPassengerDeep(this.targetEntity)) {
                return false;
            }
            if (this.validTargetPredicate.test(EndermanEntity.TeleportTowardsPlayerGoal.getServerWorld(this.enderman), this.enderman, this.targetEntity)) {
                return true;
            }
        }
        return super.shouldContinue();
    }

    @Override
    public void tick() {
        if (this.enderman.getTarget() == null) {
            super.setTargetEntity(null);
        }
        if (this.targetPlayer != null) {
            if (--this.lookAtPlayerWarmup <= 0) {
                this.targetEntity = this.targetPlayer;
                this.targetPlayer = null;
                super.start();
            }
        } else {
            if (this.targetEntity != null && !this.enderman.hasVehicle()) {
                if (this.enderman.isPlayerStaring((PlayerEntity)this.targetEntity)) {
                    if (this.targetEntity.squaredDistanceTo(this.enderman) < 16.0) {
                        this.enderman.teleportRandomly();
                    }
                    this.ticksSinceUnseenTeleport = 0;
                } else if (this.targetEntity.squaredDistanceTo(this.enderman) > 256.0 && this.ticksSinceUnseenTeleport++ >= this.getTickCount(30) && this.enderman.teleportTo(this.targetEntity)) {
                    this.ticksSinceUnseenTeleport = 0;
                }
            }
            super.tick();
        }
    }
}
