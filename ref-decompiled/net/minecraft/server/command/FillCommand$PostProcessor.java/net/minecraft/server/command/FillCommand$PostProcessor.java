/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public static interface FillCommand.PostProcessor {
    public static final FillCommand.PostProcessor EMPTY = (world, pos) -> false;

    public boolean affect(ServerWorld var1, BlockPos var2);
}
