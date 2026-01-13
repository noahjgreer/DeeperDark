/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.rule.GameRules;

class FoxEntity.MateGoal
extends AnimalMateGoal {
    public FoxEntity.MateGoal(FoxEntity fox, double chance) {
        super(fox, chance);
    }

    @Override
    public void start() {
        ((FoxEntity)this.animal).stopActions();
        ((FoxEntity)this.mate).stopActions();
        super.start();
    }

    @Override
    protected void breed() {
        FoxEntity foxEntity = (FoxEntity)this.animal.createChild(this.world, this.mate);
        if (foxEntity == null) {
            return;
        }
        ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
        ServerPlayerEntity serverPlayerEntity2 = this.mate.getLovingPlayer();
        ServerPlayerEntity serverPlayerEntity3 = serverPlayerEntity;
        if (serverPlayerEntity != null) {
            foxEntity.trust(serverPlayerEntity);
        } else {
            serverPlayerEntity3 = serverPlayerEntity2;
        }
        if (serverPlayerEntity2 != null && serverPlayerEntity != serverPlayerEntity2) {
            foxEntity.trust(serverPlayerEntity2);
        }
        if (serverPlayerEntity3 != null) {
            serverPlayerEntity3.incrementStat(Stats.ANIMALS_BRED);
            Criteria.BRED_ANIMALS.trigger(serverPlayerEntity3, this.animal, this.mate, foxEntity);
        }
        this.animal.setBreedingAge(6000);
        this.mate.setBreedingAge(6000);
        this.animal.resetLoveTicks();
        this.mate.resetLoveTicks();
        foxEntity.setBreedingAge(-24000);
        foxEntity.refreshPositionAndAngles(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0f, 0.0f);
        this.world.spawnEntityAndPassengers(foxEntity);
        this.world.sendEntityStatus(this.animal, (byte)18);
        if (this.world.getGameRules().getValue(GameRules.DO_MOB_LOOT).booleanValue()) {
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
        }
    }
}
