/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.InfestedBlock
 *  net.minecraft.block.PillarBlock
 *  net.minecraft.block.RotatedInfestedBlock
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.Registries
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.Direction$Axis
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

/*
 * Exception performing whole class analysis ignored.
 */
public class RotatedInfestedBlock
extends InfestedBlock {
    public static final MapCodec<RotatedInfestedBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Registries.BLOCK.getCodec().fieldOf("host").forGetter(InfestedBlock::getRegularBlock), (App)RotatedInfestedBlock.createSettingsCodec()).apply((Applicative)instance, RotatedInfestedBlock::new));

    public MapCodec<RotatedInfestedBlock> getCodec() {
        return CODEC;
    }

    public RotatedInfestedBlock(Block block, AbstractBlock.Settings settings) {
        super(block, settings);
        this.setDefaultState((BlockState)this.getDefaultState().with((Property)PillarBlock.AXIS, (Comparable)Direction.Axis.Y));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return PillarBlock.changeRotation((BlockState)state, (BlockRotation)rotation);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{PillarBlock.AXIS});
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)PillarBlock.AXIS, (Comparable)ctx.getSide().getAxis());
    }
}

