/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.structure.MineshaftStructure;

static abstract class MineshaftGenerator.MineshaftPart
extends StructurePiece {
    protected MineshaftStructure.Type mineshaftType;

    public MineshaftGenerator.MineshaftPart(StructurePieceType structurePieceType, int chainLength, MineshaftStructure.Type type, BlockBox box) {
        super(structurePieceType, chainLength, box);
        this.mineshaftType = type;
    }

    public MineshaftGenerator.MineshaftPart(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
        super(structurePieceType, nbtCompound);
        this.mineshaftType = MineshaftStructure.Type.byId(nbtCompound.getInt("MST", 0));
    }

    @Override
    protected boolean canAddBlock(WorldView world, int x, int y, int z, BlockBox box) {
        BlockState blockState = this.getBlockAt(world, x, y, z, box);
        return !blockState.isOf(this.mineshaftType.getPlanks().getBlock()) && !blockState.isOf(this.mineshaftType.getLog().getBlock()) && !blockState.isOf(this.mineshaftType.getFence().getBlock()) && !blockState.isOf(Blocks.IRON_CHAIN);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        nbt.putInt("MST", this.mineshaftType.ordinal());
    }

    protected boolean isSolidCeiling(BlockView world, BlockBox boundingBox, int minX, int maxX, int y, int z) {
        for (int i = minX; i <= maxX; ++i) {
            if (!this.getBlockAt(world, i, y + 1, z, boundingBox).isAir()) continue;
            return false;
        }
        return true;
    }

    protected boolean cannotGenerate(WorldAccess world, BlockBox box) {
        int p;
        int o;
        int n;
        int m;
        int i = Math.max(this.boundingBox.getMinX() - 1, box.getMinX());
        int j = Math.max(this.boundingBox.getMinY() - 1, box.getMinY());
        int k = Math.max(this.boundingBox.getMinZ() - 1, box.getMinZ());
        int l = Math.min(this.boundingBox.getMaxX() + 1, box.getMaxX());
        BlockPos.Mutable mutable = new BlockPos.Mutable((i + l) / 2, (j + (m = Math.min(this.boundingBox.getMaxY() + 1, box.getMaxY()))) / 2, (k + (n = Math.min(this.boundingBox.getMaxZ() + 1, box.getMaxZ()))) / 2);
        if (world.getBiome(mutable).isIn(BiomeTags.MINESHAFT_BLOCKING)) {
            return true;
        }
        for (o = i; o <= l; ++o) {
            for (p = k; p <= n; ++p) {
                if (world.getBlockState(mutable.set(o, j, p)).isLiquid()) {
                    return true;
                }
                if (!world.getBlockState(mutable.set(o, m, p)).isLiquid()) continue;
                return true;
            }
        }
        for (o = i; o <= l; ++o) {
            for (p = j; p <= m; ++p) {
                if (world.getBlockState(mutable.set(o, p, k)).isLiquid()) {
                    return true;
                }
                if (!world.getBlockState(mutable.set(o, p, n)).isLiquid()) continue;
                return true;
            }
        }
        for (o = k; o <= n; ++o) {
            for (p = j; p <= m; ++p) {
                if (world.getBlockState(mutable.set(i, p, o)).isLiquid()) {
                    return true;
                }
                if (!world.getBlockState(mutable.set(l, p, o)).isLiquid()) continue;
                return true;
            }
        }
        return false;
    }

    protected void tryPlaceFloor(StructureWorldAccess world, BlockBox box, BlockState state, int x, int y, int z) {
        if (!this.isUnderSeaLevel(world, x, y, z, box)) {
            return;
        }
        BlockPos.Mutable blockPos = this.offsetPos(x, y, z);
        BlockState blockState = world.getBlockState(blockPos);
        if (!blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
            world.setBlockState(blockPos, state, 2);
        }
    }
}
