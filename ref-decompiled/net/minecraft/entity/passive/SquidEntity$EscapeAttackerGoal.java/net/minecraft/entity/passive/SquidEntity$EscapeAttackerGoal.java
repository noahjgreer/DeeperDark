/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

class SquidEntity.EscapeAttackerGoal
extends Goal {
    private static final float field_30375 = 3.0f;
    private static final float field_30376 = 5.0f;
    private static final float field_30377 = 10.0f;
    private int timer;

    SquidEntity.EscapeAttackerGoal() {
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = SquidEntity.this.getAttacker();
        if (SquidEntity.this.isTouchingWater() && livingEntity != null) {
            return SquidEntity.this.squaredDistanceTo(livingEntity) < 100.0;
        }
        return false;
    }

    @Override
    public void start() {
        this.timer = 0;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        ++this.timer;
        LivingEntity livingEntity = SquidEntity.this.getAttacker();
        if (livingEntity == null) {
            return;
        }
        Vec3d vec3d = new Vec3d(SquidEntity.this.getX() - livingEntity.getX(), SquidEntity.this.getY() - livingEntity.getY(), SquidEntity.this.getZ() - livingEntity.getZ());
        BlockState blockState = SquidEntity.this.getEntityWorld().getBlockState(BlockPos.ofFloored(SquidEntity.this.getX() + vec3d.x, SquidEntity.this.getY() + vec3d.y, SquidEntity.this.getZ() + vec3d.z));
        FluidState fluidState = SquidEntity.this.getEntityWorld().getFluidState(BlockPos.ofFloored(SquidEntity.this.getX() + vec3d.x, SquidEntity.this.getY() + vec3d.y, SquidEntity.this.getZ() + vec3d.z));
        if (fluidState.isIn(FluidTags.WATER) || blockState.isAir()) {
            double d = vec3d.length();
            if (d > 0.0) {
                vec3d.normalize();
                double e = 3.0;
                if (d > 5.0) {
                    e -= (d - 5.0) / 5.0;
                }
                if (e > 0.0) {
                    vec3d = vec3d.multiply(e);
                }
            }
            if (blockState.isAir()) {
                vec3d = vec3d.subtract(0.0, vec3d.y, 0.0);
            }
            SquidEntity.this.swimVec = new Vec3d(vec3d.x / 20.0, vec3d.y / 20.0, vec3d.z / 20.0);
        }
        if (this.timer % 10 == 5) {
            SquidEntity.this.getEntityWorld().addParticleClient(ParticleTypes.BUBBLE, SquidEntity.this.getX(), SquidEntity.this.getY(), SquidEntity.this.getZ(), 0.0, 0.0, 0.0);
        }
    }
}
