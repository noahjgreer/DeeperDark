/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.PillarBlock
 *  net.minecraft.block.PillarBlock$1
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.Direction$Axis
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

/*
 * Exception performing whole class analysis ignored.
 */
public class PillarBlock
extends Block {
    public static final MapCodec<PillarBlock> CODEC = PillarBlock.createCodec(PillarBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

    public MapCodec<? extends PillarBlock> getCodec() {
        return CODEC;
    }

    public PillarBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with((Property)AXIS, (Comparable)Direction.Axis.Y));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return PillarBlock.changeRotation((BlockState)state, (BlockRotation)rotation);
    }

    public static BlockState changeRotation(BlockState state, BlockRotation rotation) {
        switch (1.field_11460[rotation.ordinal()]) {
            case 1: 
            case 2: {
                switch (1.field_11461[((Direction.Axis)state.get((Property)AXIS)).ordinal()]) {
                    case 1: {
                        return (BlockState)state.with((Property)AXIS, (Comparable)Direction.Axis.Z);
                    }
                    case 2: {
                        return (BlockState)state.with((Property)AXIS, (Comparable)Direction.Axis.X);
                    }
                }
                return state;
            }
        }
        return state;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS});
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)AXIS, (Comparable)ctx.getSide().getAxis());
    }
}

