/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.random.Random;

static class JungleTempleGenerator.CobblestoneRandomizer
extends StructurePiece.BlockRandomizer {
    JungleTempleGenerator.CobblestoneRandomizer() {
    }

    @Override
    public void setBlock(Random random, int x, int y, int z, boolean placeBlock) {
        this.block = random.nextFloat() < 0.4f ? Blocks.COBBLESTONE.getDefaultState() : Blocks.MOSSY_COBBLESTONE.getDefaultState();
    }
}
