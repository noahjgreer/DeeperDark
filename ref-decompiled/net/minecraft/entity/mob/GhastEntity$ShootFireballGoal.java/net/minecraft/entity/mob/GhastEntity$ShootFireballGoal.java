/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

static class GhastEntity.ShootFireballGoal
extends Goal {
    private final GhastEntity ghast;
    public int cooldown;

    public GhastEntity.ShootFireballGoal(GhastEntity ghast) {
        this.ghast = ghast;
    }

    @Override
    public boolean canStart() {
        return this.ghast.getTarget() != null;
    }

    @Override
    public void start() {
        this.cooldown = 0;
    }

    @Override
    public void stop() {
        this.ghast.setShooting(false);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.ghast.getTarget();
        if (livingEntity == null) {
            return;
        }
        double d = 64.0;
        if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0 && this.ghast.canSee(livingEntity)) {
            World world = this.ghast.getEntityWorld();
            ++this.cooldown;
            if (this.cooldown == 10 && !this.ghast.isSilent()) {
                world.syncWorldEvent(null, 1015, this.ghast.getBlockPos(), 0);
            }
            if (this.cooldown == 20) {
                double e = 4.0;
                Vec3d vec3d = this.ghast.getRotationVec(1.0f);
                double f = livingEntity.getX() - (this.ghast.getX() + vec3d.x * 4.0);
                double g = livingEntity.getBodyY(0.5) - (0.5 + this.ghast.getBodyY(0.5));
                double h = livingEntity.getZ() - (this.ghast.getZ() + vec3d.z * 4.0);
                Vec3d vec3d2 = new Vec3d(f, g, h);
                if (!this.ghast.isSilent()) {
                    world.syncWorldEvent(null, 1016, this.ghast.getBlockPos(), 0);
                }
                FireballEntity fireballEntity = new FireballEntity(world, (LivingEntity)this.ghast, vec3d2.normalize(), this.ghast.getFireballStrength());
                fireballEntity.setPosition(this.ghast.getX() + vec3d.x * 4.0, this.ghast.getBodyY(0.5) + 0.5, fireballEntity.getZ() + vec3d.z * 4.0);
                world.spawnEntity(fireballEntity);
                this.cooldown = -40;
            }
        } else if (this.cooldown > 0) {
            --this.cooldown;
        }
        this.ghast.setShooting(this.cooldown > 10);
    }
}
