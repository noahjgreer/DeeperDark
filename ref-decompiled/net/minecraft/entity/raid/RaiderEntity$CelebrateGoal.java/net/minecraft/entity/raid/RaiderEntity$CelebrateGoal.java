/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.raid;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.village.raid.Raid;

public class RaiderEntity.CelebrateGoal
extends Goal {
    private final RaiderEntity raider;

    RaiderEntity.CelebrateGoal(RaiderEntity raider) {
        this.raider = raider;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        Raid raid = this.raider.getRaid();
        return this.raider.isAlive() && this.raider.getTarget() == null && raid != null && raid.hasLost();
    }

    @Override
    public void start() {
        this.raider.setCelebrating(true);
        super.start();
    }

    @Override
    public void stop() {
        this.raider.setCelebrating(false);
        super.stop();
    }

    @Override
    public void tick() {
        if (!this.raider.isSilent() && this.raider.random.nextInt(this.getTickCount(100)) == 0) {
            RaiderEntity.this.playSound(RaiderEntity.this.getCelebratingSound());
        }
        if (!this.raider.hasVehicle() && this.raider.random.nextInt(this.getTickCount(50)) == 0) {
            this.raider.getJumpControl().setActive();
        }
        super.tick();
    }
}
