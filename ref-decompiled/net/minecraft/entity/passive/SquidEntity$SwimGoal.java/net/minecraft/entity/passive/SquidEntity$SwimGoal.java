/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

static class SquidEntity.SwimGoal
extends Goal {
    private final SquidEntity squid;

    public SquidEntity.SwimGoal(SquidEntity squid) {
        this.squid = squid;
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public void tick() {
        int i = this.squid.getDespawnCounter();
        if (i > 100) {
            this.squid.swimVec = Vec3d.ZERO;
        } else if (this.squid.getRandom().nextInt(SquidEntity.SwimGoal.toGoalTicks(50)) == 0 || !this.squid.touchingWater || !this.squid.hasSwimmingVector()) {
            float f = this.squid.getRandom().nextFloat() * ((float)Math.PI * 2);
            this.squid.swimVec = new Vec3d(MathHelper.cos(f) * 0.2f, -0.1f + this.squid.getRandom().nextFloat() * 0.2f, MathHelper.sin(f) * 0.2f);
        }
    }
}
