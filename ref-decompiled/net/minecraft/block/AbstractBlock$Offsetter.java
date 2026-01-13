/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@FunctionalInterface
public static interface AbstractBlock.Offsetter {
    public Vec3d evaluate(BlockState var1, BlockPos var2);
}
