/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jspecify.annotations.Nullable;

static class ParrotEntity.FlyOntoTreeGoal
extends FlyGoal {
    public ParrotEntity.FlyOntoTreeGoal(PathAwareEntity pathAwareEntity, double d) {
        super(pathAwareEntity, d);
    }

    @Override
    protected @Nullable Vec3d getWanderTarget() {
        Vec3d vec3d = null;
        if (this.mob.isTouchingWater()) {
            vec3d = FuzzyTargeting.find(this.mob, 15, 15);
        }
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            vec3d = this.locateTree();
        }
        return vec3d == null ? super.getWanderTarget() : vec3d;
    }

    private @Nullable Vec3d locateTree() {
        BlockPos blockPos = this.mob.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        Iterable<BlockPos> iterable = BlockPos.iterate(MathHelper.floor(this.mob.getX() - 3.0), MathHelper.floor(this.mob.getY() - 6.0), MathHelper.floor(this.mob.getZ() - 3.0), MathHelper.floor(this.mob.getX() + 3.0), MathHelper.floor(this.mob.getY() + 6.0), MathHelper.floor(this.mob.getZ() + 3.0));
        for (BlockPos blockPos2 : iterable) {
            BlockState blockState;
            boolean bl;
            if (blockPos.equals(blockPos2) || !(bl = (blockState = this.mob.getEntityWorld().getBlockState(mutable2.set((Vec3i)blockPos2, Direction.DOWN))).getBlock() instanceof LeavesBlock || blockState.isIn(BlockTags.LOGS)) || !this.mob.getEntityWorld().isAir(blockPos2) || !this.mob.getEntityWorld().isAir(mutable.set((Vec3i)blockPos2, Direction.UP))) continue;
            return Vec3d.ofBottomCenter(blockPos2);
        }
        return null;
    }
}
