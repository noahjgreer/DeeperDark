/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

class PhantomEntity.FindTargetGoal
extends Goal {
    private final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(64.0);
    private int delay = PhantomEntity.FindTargetGoal.toGoalTicks(20);

    PhantomEntity.FindTargetGoal() {
    }

    @Override
    public boolean canStart() {
        if (this.delay > 0) {
            --this.delay;
            return false;
        }
        this.delay = PhantomEntity.FindTargetGoal.toGoalTicks(60);
        ServerWorld serverWorld = PhantomEntity.FindTargetGoal.castToServerWorld(PhantomEntity.this.getEntityWorld());
        List list = serverWorld.getPlayers(this.PLAYERS_IN_RANGE_PREDICATE, PhantomEntity.this, PhantomEntity.this.getBoundingBox().expand(16.0, 64.0, 16.0));
        if (!list.isEmpty()) {
            list.sort(Comparator.comparing(Entity::getY).reversed());
            for (PlayerEntity playerEntity : list) {
                if (!PhantomEntity.this.testTargetPredicate(serverWorld, playerEntity, TargetPredicate.DEFAULT)) continue;
                PhantomEntity.this.setTarget(playerEntity);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = PhantomEntity.this.getTarget();
        if (livingEntity != null) {
            return PhantomEntity.this.testTargetPredicate(PhantomEntity.FindTargetGoal.castToServerWorld(PhantomEntity.this.getEntityWorld()), livingEntity, TargetPredicate.DEFAULT);
        }
        return false;
    }
}
