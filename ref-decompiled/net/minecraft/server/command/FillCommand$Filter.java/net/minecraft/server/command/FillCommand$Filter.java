/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.command;

import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public static interface FillCommand.Filter {
    public static final FillCommand.Filter IDENTITY = (box, pos, block, world) -> block;

    public @Nullable BlockStateArgument filter(BlockBox var1, BlockPos var2, BlockStateArgument var3, ServerWorld var4);
}
