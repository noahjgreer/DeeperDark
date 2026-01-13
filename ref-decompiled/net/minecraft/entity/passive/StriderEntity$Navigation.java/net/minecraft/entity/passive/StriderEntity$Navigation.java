/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

static class StriderEntity.Navigation
extends MobNavigation {
    StriderEntity.Navigation(StriderEntity entity, World world) {
        super(entity, world);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new LandPathNodeMaker();
        return new PathNodeNavigator(this.nodeMaker, range);
    }

    @Override
    protected boolean canWalkOnPath(PathNodeType pathType) {
        if (pathType == PathNodeType.LAVA || pathType == PathNodeType.DAMAGE_FIRE || pathType == PathNodeType.DANGER_FIRE) {
            return true;
        }
        return super.canWalkOnPath(pathType);
    }

    @Override
    public boolean isValidPosition(BlockPos pos) {
        return this.world.getBlockState(pos).isOf(Blocks.LAVA) || super.isValidPosition(pos);
    }
}
