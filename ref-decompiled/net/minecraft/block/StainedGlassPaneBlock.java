/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.PaneBlock
 *  net.minecraft.block.Stainable
 *  net.minecraft.block.StainedGlassPaneBlock
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.DyeColor
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.Stainable;
import net.minecraft.state.property.Property;
import net.minecraft.util.DyeColor;

/*
 * Exception performing whole class analysis ignored.
 */
public class StainedGlassPaneBlock
extends PaneBlock
implements Stainable {
    public static final MapCodec<StainedGlassPaneBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DyeColor.CODEC.fieldOf("color").forGetter(StainedGlassPaneBlock::getColor), (App)StainedGlassPaneBlock.createSettingsCodec()).apply((Applicative)instance, StainedGlassPaneBlock::new));
    private final DyeColor color;

    public MapCodec<StainedGlassPaneBlock> getCodec() {
        return CODEC;
    }

    public StainedGlassPaneBlock(DyeColor color, AbstractBlock.Settings settings) {
        super(settings);
        this.color = color;
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)NORTH, (Comparable)Boolean.valueOf(false))).with((Property)EAST, (Comparable)Boolean.valueOf(false))).with((Property)SOUTH, (Comparable)Boolean.valueOf(false))).with((Property)WEST, (Comparable)Boolean.valueOf(false))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public DyeColor getColor() {
        return this.color;
    }
}

