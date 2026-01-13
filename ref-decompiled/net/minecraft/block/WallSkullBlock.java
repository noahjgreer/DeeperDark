/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractSkullBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SkullBlock$SkullType
 *  net.minecraft.block.WallSkullBlock
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SkullBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public class WallSkullBlock
extends AbstractSkullBlock {
    public static final MapCodec<WallSkullBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)SkullBlock.SkullType.CODEC.fieldOf("kind").forGetter(AbstractSkullBlock::getSkullType), (App)WallSkullBlock.createSettingsCodec()).apply((Applicative)instance, WallSkullBlock::new));
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    private static final Map<Direction, VoxelShape> SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)8.0, (double)8.0, (double)16.0));

    public MapCodec<? extends WallSkullBlock> getCodec() {
        return CODEC;
    }

    public WallSkullBlock(SkullBlock.SkullType skullType, AbstractBlock.Settings settings) {
        super(skullType, settings);
        this.setDefaultState((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)Direction.NORTH));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)SHAPES_BY_DIRECTION.get(state.get((Property)FACING));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction[] directions;
        BlockState blockState = super.getPlacementState(ctx);
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : directions = ctx.getPlacementDirections()) {
            if (!direction.getAxis().isHorizontal()) continue;
            Direction direction2 = direction.getOpposite();
            blockState = (BlockState)blockState.with((Property)FACING, (Comparable)direction2);
            if (blockView.getBlockState(blockPos.offset(direction)).canReplace(ctx)) continue;
            return blockState;
        }
        return null;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(new Property[]{FACING});
    }
}

