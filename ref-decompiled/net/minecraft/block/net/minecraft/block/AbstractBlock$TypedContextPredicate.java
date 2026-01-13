/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@FunctionalInterface
public static interface AbstractBlock.TypedContextPredicate<A> {
    public boolean test(BlockState var1, BlockView var2, BlockPos var3, A var4);
}
