/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.SkullBlock$SkullType
 *  net.minecraft.block.SkullBlock$Type
 *  net.minecraft.block.WallPlayerSkullBlock
 *  net.minecraft.block.WallSkullBlock
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;

public class WallPlayerSkullBlock
extends WallSkullBlock {
    public static final MapCodec<WallPlayerSkullBlock> CODEC = WallPlayerSkullBlock.createCodec(WallPlayerSkullBlock::new);

    public MapCodec<WallPlayerSkullBlock> getCodec() {
        return CODEC;
    }

    public WallPlayerSkullBlock(AbstractBlock.Settings settings) {
        super((SkullBlock.SkullType)SkullBlock.Type.PLAYER, settings);
    }
}

