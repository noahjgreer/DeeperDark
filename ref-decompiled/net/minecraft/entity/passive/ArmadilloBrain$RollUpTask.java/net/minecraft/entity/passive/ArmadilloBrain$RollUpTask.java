/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;

public static class ArmadilloBrain.RollUpTask
extends MultiTickTask<ArmadilloEntity> {
    static final int RUN_TIME_IN_TICKS = 5 * TimeHelper.MINUTE_IN_SECONDS * 20;
    static final int field_49088 = 5;
    static final int field_49089 = 75;
    int ticksUntilPeek = 0;
    boolean considerPeeking;

    public ArmadilloBrain.RollUpTask() {
        super(Map.of(), RUN_TIME_IN_TICKS);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
        boolean bl;
        super.keepRunning(serverWorld, armadilloEntity, l);
        if (this.ticksUntilPeek > 0) {
            --this.ticksUntilPeek;
        }
        if (armadilloEntity.shouldSwitchToScaredState()) {
            armadilloEntity.setState(ArmadilloEntity.State.SCARED);
            if (armadilloEntity.isOnGround()) {
                armadilloEntity.playSoundIfNotSilent(SoundEvents.ENTITY_ARMADILLO_LAND);
            }
            return;
        }
        ArmadilloEntity.State state = armadilloEntity.getState();
        long m = armadilloEntity.getBrain().getMemoryExpiry(MemoryModuleType.DANGER_DETECTED_RECENTLY);
        boolean bl2 = bl = m > 75L;
        if (bl != this.considerPeeking) {
            this.ticksUntilPeek = this.calculateTicksUntilPeek(armadilloEntity);
        }
        this.considerPeeking = bl;
        if (state == ArmadilloEntity.State.SCARED) {
            if (this.ticksUntilPeek == 0 && armadilloEntity.isOnGround() && bl) {
                serverWorld.sendEntityStatus(armadilloEntity, (byte)64);
                this.ticksUntilPeek = this.calculateTicksUntilPeek(armadilloEntity);
            }
            if (m < (long)ArmadilloEntity.State.UNROLLING.getLengthInTicks()) {
                armadilloEntity.playSoundIfNotSilent(SoundEvents.ENTITY_ARMADILLO_UNROLL_START);
                armadilloEntity.setState(ArmadilloEntity.State.UNROLLING);
            }
        } else if (state == ArmadilloEntity.State.UNROLLING && m > (long)ArmadilloEntity.State.UNROLLING.getLengthInTicks()) {
            armadilloEntity.setState(ArmadilloEntity.State.SCARED);
        }
    }

    private int calculateTicksUntilPeek(ArmadilloEntity entity) {
        return ArmadilloEntity.State.SCARED.getLengthInTicks() + entity.getRandom().nextBetween(100, 400);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, ArmadilloEntity armadilloEntity) {
        return armadilloEntity.isOnGround();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
        return armadilloEntity.getState().shouldRunRollUpTask();
    }

    @Override
    protected void run(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
        armadilloEntity.startRolling();
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
        if (!armadilloEntity.canRollUp()) {
            armadilloEntity.unroll();
        }
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (ArmadilloEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (ArmadilloEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (ArmadilloEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (ArmadilloEntity)entity, time);
    }
}
