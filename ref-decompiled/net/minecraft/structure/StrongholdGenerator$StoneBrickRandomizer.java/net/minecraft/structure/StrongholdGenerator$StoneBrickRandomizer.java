/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.random.Random;

static class StrongholdGenerator.StoneBrickRandomizer
extends StructurePiece.BlockRandomizer {
    StrongholdGenerator.StoneBrickRandomizer() {
    }

    @Override
    public void setBlock(Random random, int x, int y, int z, boolean placeBlock) {
        float f;
        this.block = placeBlock ? ((f = random.nextFloat()) < 0.2f ? Blocks.CRACKED_STONE_BRICKS.getDefaultState() : (f < 0.5f ? Blocks.MOSSY_STONE_BRICKS.getDefaultState() : (f < 0.55f ? Blocks.INFESTED_STONE_BRICKS.getDefaultState() : Blocks.STONE_BRICKS.getDefaultState()))) : Blocks.CAVE_AIR.getDefaultState();
    }
}
