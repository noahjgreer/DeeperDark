/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.FacingBlock
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.util.math.Direction
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public abstract class FacingBlock
extends Block {
    public static final EnumProperty<Direction> FACING = Properties.FACING;

    protected FacingBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected abstract MapCodec<? extends FacingBlock> getCodec();
}

