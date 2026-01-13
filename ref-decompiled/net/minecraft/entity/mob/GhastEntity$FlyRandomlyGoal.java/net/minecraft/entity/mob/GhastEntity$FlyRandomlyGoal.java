/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public static class GhastEntity.FlyRandomlyGoal
extends Goal {
    private static final int field_59707 = 64;
    private final MobEntity ghast;
    private final int blockCheckDistance;

    public GhastEntity.FlyRandomlyGoal(MobEntity ghast) {
        this(ghast, 0);
    }

    public GhastEntity.FlyRandomlyGoal(MobEntity ghast, int blockCheckDistance) {
        this.ghast = ghast;
        this.blockCheckDistance = blockCheckDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        double f;
        double e;
        MoveControl moveControl = this.ghast.getMoveControl();
        if (!moveControl.isMoving()) {
            return true;
        }
        double d = moveControl.getTargetX() - this.ghast.getX();
        double g = d * d + (e = moveControl.getTargetY() - this.ghast.getY()) * e + (f = moveControl.getTargetZ() - this.ghast.getZ()) * f;
        return g < 1.0 || g > 3600.0;
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    @Override
    public void start() {
        Vec3d vec3d = GhastEntity.FlyRandomlyGoal.locateTarget(this.ghast, this.blockCheckDistance);
        this.ghast.getMoveControl().moveTo(vec3d.getX(), vec3d.getY(), vec3d.getZ(), 1.0);
    }

    public static Vec3d locateTarget(MobEntity ghast, int blockCheckDistance) {
        BlockPos blockPos;
        int j;
        World world = ghast.getEntityWorld();
        Random random = ghast.getRandom();
        Vec3d vec3d = ghast.getEntityPos();
        Vec3d vec3d2 = null;
        for (int i = 0; i < 64; ++i) {
            vec3d2 = GhastEntity.FlyRandomlyGoal.getTargetPos(ghast, vec3d, random);
            if (vec3d2 == null || !GhastEntity.FlyRandomlyGoal.isTargetValid(world, vec3d2, blockCheckDistance)) continue;
            return vec3d2;
        }
        if (vec3d2 == null) {
            vec3d2 = GhastEntity.FlyRandomlyGoal.addRandom(vec3d, random);
        }
        if ((j = world.getTopY(Heightmap.Type.MOTION_BLOCKING, (blockPos = BlockPos.ofFloored(vec3d2)).getX(), blockPos.getZ())) < blockPos.getY() && j > world.getBottomY()) {
            vec3d2 = new Vec3d(vec3d2.getX(), ghast.getY() - Math.abs(ghast.getY() - vec3d2.getY()), vec3d2.getZ());
        }
        return vec3d2;
    }

    private static boolean isTargetValid(World world, Vec3d pos, int blockCheckDistance) {
        if (blockCheckDistance <= 0) {
            return true;
        }
        BlockPos blockPos = BlockPos.ofFloored(pos);
        if (!world.getBlockState(blockPos).isAir()) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            for (int i = 1; i < blockCheckDistance; ++i) {
                BlockPos blockPos2 = blockPos.offset(direction, i);
                if (world.getBlockState(blockPos2).isAir()) continue;
                return true;
            }
        }
        return false;
    }

    private static Vec3d addRandom(Vec3d pos, Random random) {
        double d = pos.getX() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
        double e = pos.getY() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
        double f = pos.getZ() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
        return new Vec3d(d, e, f);
    }

    private static @Nullable Vec3d getTargetPos(MobEntity ghast, Vec3d pos, Random random) {
        Vec3d vec3d = GhastEntity.FlyRandomlyGoal.addRandom(pos, random);
        if (ghast.hasPositionTarget() && !ghast.isInPositionTargetRange(vec3d)) {
            return null;
        }
        return vec3d;
    }
}
