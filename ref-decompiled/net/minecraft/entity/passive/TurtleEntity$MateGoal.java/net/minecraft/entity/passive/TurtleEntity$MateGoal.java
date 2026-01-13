/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.rule.GameRules;

static class TurtleEntity.MateGoal
extends AnimalMateGoal {
    private final TurtleEntity turtle;

    TurtleEntity.MateGoal(TurtleEntity turtle, double speed) {
        super(turtle, speed);
        this.turtle = turtle;
    }

    @Override
    public boolean canStart() {
        return super.canStart() && !this.turtle.hasEgg();
    }

    @Override
    protected void breed() {
        ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
        if (serverPlayerEntity == null && this.mate.getLovingPlayer() != null) {
            serverPlayerEntity = this.mate.getLovingPlayer();
        }
        if (serverPlayerEntity != null) {
            serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
            Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this.animal, this.mate, null);
        }
        this.turtle.setHasEgg(true);
        this.animal.setBreedingAge(6000);
        this.mate.setBreedingAge(6000);
        this.animal.resetLoveTicks();
        this.mate.resetLoveTicks();
        Random random = this.animal.getRandom();
        if (TurtleEntity.MateGoal.castToServerWorld(this.world).getGameRules().getValue(GameRules.DO_MOB_LOOT).booleanValue()) {
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
        }
    }
}
