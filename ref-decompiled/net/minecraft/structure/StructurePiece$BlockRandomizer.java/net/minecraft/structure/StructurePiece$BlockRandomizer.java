/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.random.Random;

public static abstract class StructurePiece.BlockRandomizer {
    protected BlockState block = Blocks.AIR.getDefaultState();

    public abstract void setBlock(Random var1, int var2, int var3, int var4, boolean var5);

    public BlockState getBlock() {
        return this.block;
    }
}
