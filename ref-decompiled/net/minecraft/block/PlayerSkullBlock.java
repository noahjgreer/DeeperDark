/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.PlayerSkullBlock
 *  net.minecraft.block.SkullBlock
 *  net.minecraft.block.SkullBlock$SkullType
 *  net.minecraft.block.SkullBlock$Type
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.SkullBlock;

public class PlayerSkullBlock
extends SkullBlock {
    public static final MapCodec<PlayerSkullBlock> CODEC = PlayerSkullBlock.createCodec(PlayerSkullBlock::new);

    public MapCodec<PlayerSkullBlock> getCodec() {
        return CODEC;
    }

    public PlayerSkullBlock(AbstractBlock.Settings settings) {
        super((SkullBlock.SkullType)SkullBlock.Type.PLAYER, settings);
    }
}

