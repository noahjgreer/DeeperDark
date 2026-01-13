/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathContext;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

class CreakingEntity.CreakingLandPathNodeMaker
extends LandPathNodeMaker {
    private static final int field_54896 = 1024;

    CreakingEntity.CreakingLandPathNodeMaker() {
    }

    @Override
    public PathNodeType getDefaultNodeType(PathContext context, int x, int y, int z) {
        BlockPos blockPos = CreakingEntity.this.getHomePos();
        if (blockPos == null) {
            return super.getDefaultNodeType(context, x, y, z);
        }
        double d = blockPos.getSquaredDistance(new Vec3i(x, y, z));
        if (d > 1024.0 && d >= blockPos.getSquaredDistance(context.getEntityPos())) {
            return PathNodeType.BLOCKED;
        }
        return super.getDefaultNodeType(context, x, y, z);
    }
}
