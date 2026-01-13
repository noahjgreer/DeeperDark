/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.HayBlock
 *  net.minecraft.block.PillarBlock
 *  net.minecraft.entity.Entity
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HayBlock
extends PillarBlock {
    public static final MapCodec<HayBlock> CODEC = HayBlock.createCodec(HayBlock::new);

    public MapCodec<HayBlock> getCodec() {
        return CODEC;
    }

    public HayBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)AXIS, (Comparable)Direction.Axis.Y));
    }

    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        entity.handleFallDamage(fallDistance, 0.2f, world.getDamageSources().fall());
    }
}

