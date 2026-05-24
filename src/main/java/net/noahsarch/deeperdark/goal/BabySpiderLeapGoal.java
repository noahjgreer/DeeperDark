package net.noahsarch.deeperdark.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

// Replaces the vanilla LeapAtTargetGoal for baby spiders — jumps twice as frequently
// (1/2 chance per tick vs vanilla 1/3) and with the same yd as normal spider (0.4).
public class BabySpiderLeapGoal extends Goal {
    private final Mob mob;
    private LivingEntity target;

    public BabySpiderLeapGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.hasControllingPassenger()) return false;
        this.target = this.mob.getTarget();
        if (this.target == null) return false;
        double d = this.mob.distanceToSqr(this.target);
        if (d < 4.0 || d > 16.0) return false;
        return this.mob.onGround() && this.mob.getRandom().nextInt(2) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.onGround();
    }

    @Override
    public void start() {
        Vec3 movement = this.mob.getDeltaMovement();
        Vec3 delta = new Vec3(this.target.getX() - this.mob.getX(), 0.0, this.target.getZ() - this.mob.getZ());
        if (delta.lengthSqr() > 1.0E-7) {
            delta = delta.normalize().scale(0.4).add(movement.scale(0.2));
        }
        this.mob.setDeltaMovement(delta.x, 0.4F, delta.z);
    }
}
