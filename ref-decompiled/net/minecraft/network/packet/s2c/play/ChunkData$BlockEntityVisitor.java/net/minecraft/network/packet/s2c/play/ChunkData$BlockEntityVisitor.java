/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public static interface ChunkData.BlockEntityVisitor {
    public void accept(BlockPos var1, BlockEntityType<?> var2, @Nullable NbtCompound var3);
}
