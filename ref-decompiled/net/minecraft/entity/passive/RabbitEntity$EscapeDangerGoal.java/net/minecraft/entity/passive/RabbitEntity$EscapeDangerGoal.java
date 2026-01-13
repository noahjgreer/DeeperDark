/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.passive.RabbitEntity;

static class RabbitEntity.EscapeDangerGoal
extends EscapeDangerGoal {
    private final RabbitEntity rabbit;

    public RabbitEntity.EscapeDangerGoal(RabbitEntity rabbit, double speed) {
        super(rabbit, speed);
        this.rabbit = rabbit;
    }

    @Override
    public void tick() {
        super.tick();
        this.rabbit.setSpeed(this.speed);
    }
}
