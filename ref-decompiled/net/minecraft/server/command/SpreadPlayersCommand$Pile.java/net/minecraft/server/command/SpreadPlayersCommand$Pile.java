/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;

static class SpreadPlayersCommand.Pile {
    double x;
    double z;

    SpreadPlayersCommand.Pile() {
    }

    double getDistance(SpreadPlayersCommand.Pile other) {
        double d = this.x - other.x;
        double e = this.z - other.z;
        return Math.sqrt(d * d + e * e);
    }

    void normalize() {
        double d = this.absolute();
        this.x /= d;
        this.z /= d;
    }

    double absolute() {
        return Math.sqrt(this.x * this.x + this.z * this.z);
    }

    public void subtract(SpreadPlayersCommand.Pile other) {
        this.x -= other.x;
        this.z -= other.z;
    }

    public boolean clamp(double minX, double minZ, double maxX, double maxZ) {
        boolean bl = false;
        if (this.x < minX) {
            this.x = minX;
            bl = true;
        } else if (this.x > maxX) {
            this.x = maxX;
            bl = true;
        }
        if (this.z < minZ) {
            this.z = minZ;
            bl = true;
        } else if (this.z > maxZ) {
            this.z = maxZ;
            bl = true;
        }
        return bl;
    }

    public int getY(BlockView blockView, int maxY) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(this.x, (double)(maxY + 1), this.z);
        boolean bl = blockView.getBlockState(mutable).isAir();
        mutable.move(Direction.DOWN);
        boolean bl2 = blockView.getBlockState(mutable).isAir();
        while (mutable.getY() > blockView.getBottomY()) {
            mutable.move(Direction.DOWN);
            boolean bl3 = blockView.getBlockState(mutable).isAir();
            if (!bl3 && bl2 && bl) {
                return mutable.getY() + 1;
            }
            bl = bl2;
            bl2 = bl3;
        }
        return maxY + 1;
    }

    public boolean isSafe(BlockView world, int maxY) {
        BlockPos blockPos = BlockPos.ofFloored(this.x, this.getY(world, maxY) - 1, this.z);
        BlockState blockState = world.getBlockState(blockPos);
        return blockPos.getY() < maxY && !blockState.isLiquid() && !blockState.isIn(BlockTags.FIRE);
    }

    public void setPileLocation(Random random, double minX, double minZ, double maxX, double maxZ) {
        this.x = MathHelper.nextDouble(random, minX, maxX);
        this.z = MathHelper.nextDouble(random, minZ, maxZ);
    }
}
