/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathContext;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

static class FrogEntity.FrogSwimPathNodeMaker
extends AmphibiousPathNodeMaker {
    private final BlockPos.Mutable pos = new BlockPos.Mutable();

    public FrogEntity.FrogSwimPathNodeMaker(boolean bl) {
        super(bl);
    }

    @Override
    public PathNode getStart() {
        if (!this.entity.isTouchingWater()) {
            return super.getStart();
        }
        return this.getStart(new BlockPos(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY), MathHelper.floor(this.entity.getBoundingBox().minZ)));
    }

    @Override
    public PathNodeType getDefaultNodeType(PathContext context, int x, int y, int z) {
        this.pos.set(x, y - 1, z);
        BlockState blockState = context.getBlockState(this.pos);
        if (blockState.isIn(BlockTags.FROG_PREFER_JUMP_TO)) {
            return PathNodeType.OPEN;
        }
        return super.getDefaultNodeType(context, x, y, z);
    }
}
