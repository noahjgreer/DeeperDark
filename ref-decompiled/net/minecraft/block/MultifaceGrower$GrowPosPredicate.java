/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.MultifaceGrower;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@FunctionalInterface
public static interface MultifaceGrower.GrowPosPredicate {
    public boolean test(BlockView var1, BlockPos var2, MultifaceGrower.GrowPos var3);
}
