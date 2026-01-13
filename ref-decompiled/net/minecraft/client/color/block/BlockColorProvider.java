/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.color.block.BlockColorProvider
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.BlockRenderView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.color.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface BlockColorProvider {
    public int getColor(BlockState var1, @Nullable BlockRenderView var2, @Nullable BlockPos var3, int var4);
}

