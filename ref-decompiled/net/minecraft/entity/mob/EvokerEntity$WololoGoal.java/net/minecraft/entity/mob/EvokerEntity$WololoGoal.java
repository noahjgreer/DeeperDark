/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.List;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.world.rule.GameRules;

public class EvokerEntity.WololoGoal
extends SpellcastingIllagerEntity.CastSpellGoal {
    private final TargetPredicate convertibleSheepPredicate;

    public EvokerEntity.WololoGoal() {
        super(EvokerEntity.this);
        this.convertibleSheepPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0).setPredicate((sheep, world) -> ((SheepEntity)sheep).getColor() == DyeColor.BLUE);
    }

    @Override
    public boolean canStart() {
        if (EvokerEntity.this.getTarget() != null) {
            return false;
        }
        if (EvokerEntity.this.isSpellcasting()) {
            return false;
        }
        if (EvokerEntity.this.age < this.startTime) {
            return false;
        }
        ServerWorld serverWorld = EvokerEntity.WololoGoal.castToServerWorld(EvokerEntity.this.getEntityWorld());
        if (!serverWorld.getGameRules().getValue(GameRules.DO_MOB_GRIEFING).booleanValue()) {
            return false;
        }
        List list = serverWorld.getTargets(SheepEntity.class, this.convertibleSheepPredicate, EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0, 4.0, 16.0));
        if (list.isEmpty()) {
            return false;
        }
        EvokerEntity.this.setWololoTarget((SheepEntity)list.get(EvokerEntity.this.random.nextInt(list.size())));
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return EvokerEntity.this.getWololoTarget() != null && this.spellCooldown > 0;
    }

    @Override
    public void stop() {
        super.stop();
        EvokerEntity.this.setWololoTarget(null);
    }

    @Override
    protected void castSpell() {
        SheepEntity sheepEntity = EvokerEntity.this.getWololoTarget();
        if (sheepEntity != null && sheepEntity.isAlive()) {
            sheepEntity.setColor(DyeColor.RED);
        }
    }

    @Override
    protected int getInitialCooldown() {
        return 40;
    }

    @Override
    protected int getSpellTicks() {
        return 60;
    }

    @Override
    protected int startTimeDelay() {
        return 140;
    }

    @Override
    protected SoundEvent getSoundPrepare() {
        return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
    }

    @Override
    protected SpellcastingIllagerEntity.Spell getSpell() {
        return SpellcastingIllagerEntity.Spell.WOLOLO;
    }
}
