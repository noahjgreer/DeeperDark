/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

class EvokerEntity.SummonVexGoal
extends SpellcastingIllagerEntity.CastSpellGoal {
    private final TargetPredicate closeVexPredicate;

    EvokerEntity.SummonVexGoal() {
        super(EvokerEntity.this);
        this.closeVexPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0).ignoreVisibility().ignoreDistanceScalingFactor();
    }

    @Override
    public boolean canStart() {
        if (!super.canStart()) {
            return false;
        }
        int i = EvokerEntity.SummonVexGoal.castToServerWorld(EvokerEntity.this.getEntityWorld()).getTargets(VexEntity.class, this.closeVexPredicate, EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0)).size();
        return EvokerEntity.this.random.nextInt(8) + 1 > i;
    }

    @Override
    protected int getSpellTicks() {
        return 100;
    }

    @Override
    protected int startTimeDelay() {
        return 340;
    }

    @Override
    protected void castSpell() {
        ServerWorld serverWorld = (ServerWorld)EvokerEntity.this.getEntityWorld();
        Team team = EvokerEntity.this.getScoreboardTeam();
        for (int i = 0; i < 3; ++i) {
            BlockPos blockPos = EvokerEntity.this.getBlockPos().add(-2 + EvokerEntity.this.random.nextInt(5), 1, -2 + EvokerEntity.this.random.nextInt(5));
            VexEntity vexEntity = EntityType.VEX.create(EvokerEntity.this.getEntityWorld(), SpawnReason.MOB_SUMMONED);
            if (vexEntity == null) continue;
            vexEntity.refreshPositionAndAngles(blockPos, 0.0f, 0.0f);
            vexEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null);
            vexEntity.setOwner(EvokerEntity.this);
            vexEntity.setBounds(blockPos);
            vexEntity.setLifeTicks(20 * (30 + EvokerEntity.this.random.nextInt(90)));
            if (team != null) {
                serverWorld.getScoreboard().addScoreHolderToTeam(vexEntity.getNameForScoreboard(), team);
            }
            serverWorld.spawnEntityAndPassengers(vexEntity);
            serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, blockPos, GameEvent.Emitter.of(EvokerEntity.this));
        }
    }

    @Override
    protected SoundEvent getSoundPrepare() {
        return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
    }

    @Override
    protected SpellcastingIllagerEntity.Spell getSpell() {
        return SpellcastingIllagerEntity.Spell.SUMMON_VEX;
    }
}
