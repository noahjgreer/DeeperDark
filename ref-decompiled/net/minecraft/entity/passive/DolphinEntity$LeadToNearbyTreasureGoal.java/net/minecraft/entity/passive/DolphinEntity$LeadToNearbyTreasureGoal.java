/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

static class DolphinEntity.LeadToNearbyTreasureGoal
extends Goal {
    private final DolphinEntity dolphin;
    private boolean noPathToStructure;

    DolphinEntity.LeadToNearbyTreasureGoal(DolphinEntity dolphin) {
        this.dolphin = dolphin;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStop() {
        return false;
    }

    @Override
    public boolean canStart() {
        return this.dolphin.hasFish() && this.dolphin.getAir() >= 100;
    }

    @Override
    public boolean shouldContinue() {
        BlockPos blockPos = this.dolphin.treasurePos;
        if (blockPos == null) {
            return false;
        }
        return !BlockPos.ofFloored(blockPos.getX(), this.dolphin.getY(), blockPos.getZ()).isWithinDistance(this.dolphin.getEntityPos(), 4.0) && !this.noPathToStructure && this.dolphin.getAir() >= 100;
    }

    @Override
    public void start() {
        if (!(this.dolphin.getEntityWorld() instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)this.dolphin.getEntityWorld();
        this.noPathToStructure = false;
        this.dolphin.getNavigation().stop();
        BlockPos blockPos = this.dolphin.getBlockPos();
        BlockPos blockPos2 = serverWorld.locateStructure(StructureTags.DOLPHIN_LOCATED, blockPos, 50, false);
        if (blockPos2 == null) {
            this.noPathToStructure = true;
            return;
        }
        this.dolphin.treasurePos = blockPos2;
        serverWorld.sendEntityStatus(this.dolphin, (byte)38);
    }

    @Override
    public void stop() {
        BlockPos blockPos = this.dolphin.treasurePos;
        if (blockPos == null || BlockPos.ofFloored(blockPos.getX(), this.dolphin.getY(), blockPos.getZ()).isWithinDistance(this.dolphin.getEntityPos(), 4.0) || this.noPathToStructure) {
            this.dolphin.setHasFish(false);
        }
    }

    @Override
    public void tick() {
        if (this.dolphin.treasurePos == null) {
            return;
        }
        World world = this.dolphin.getEntityWorld();
        if (this.dolphin.isNearTarget() || this.dolphin.getNavigation().isIdle()) {
            BlockPos blockPos;
            Vec3d vec3d = Vec3d.ofCenter(this.dolphin.treasurePos);
            Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.dolphin, 16, 1, vec3d, 0.3926991f);
            if (vec3d2 == null) {
                vec3d2 = NoPenaltyTargeting.findTo(this.dolphin, 8, 4, vec3d, 1.5707963705062866);
            }
            if (!(vec3d2 == null || world.getFluidState(blockPos = BlockPos.ofFloored(vec3d2)).isIn(FluidTags.WATER) && world.getBlockState(blockPos).canPathfindThrough(NavigationType.WATER))) {
                vec3d2 = NoPenaltyTargeting.findTo(this.dolphin, 8, 5, vec3d, 1.5707963705062866);
            }
            if (vec3d2 == null) {
                this.noPathToStructure = true;
                return;
            }
            this.dolphin.getLookControl().lookAt(vec3d2.x, vec3d2.y, vec3d2.z, this.dolphin.getMaxHeadRotation() + 20, this.dolphin.getMaxLookPitchChange());
            this.dolphin.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, 1.3);
            if (world.random.nextInt(this.getTickCount(80)) == 0) {
                world.sendEntityStatus(this.dolphin, (byte)38);
            }
        }
    }
}
