/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.TintedGlassBlock
 *  net.minecraft.block.TransparentBlock
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.TransparentBlock;

public class TintedGlassBlock
extends TransparentBlock {
    public static final MapCodec<TintedGlassBlock> CODEC = TintedGlassBlock.createCodec(TintedGlassBlock::new);

    public MapCodec<TintedGlassBlock> getCodec() {
        return CODEC;
    }

    public TintedGlassBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected boolean isTransparent(BlockState state) {
        return false;
    }

    protected int getOpacity(BlockState state) {
        return 15;
    }
}

