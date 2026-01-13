/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBannerBlock
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BannerBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.RotationPropertyHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class BannerBlock
extends AbstractBannerBlock {
    public static final MapCodec<BannerBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DyeColor.CODEC.fieldOf("color").forGetter(AbstractBannerBlock::getColor), (App)BannerBlock.createSettingsCodec()).apply((Applicative)instance, BannerBlock::new));
    public static final IntProperty ROTATION = Properties.ROTATION;
    private static final Map<DyeColor, Block> COLORED_BANNERS = Maps.newHashMap();
    private static final VoxelShape SHAPE = Block.createColumnShape((double)8.0, (double)0.0, (double)16.0);

    public MapCodec<BannerBlock> getCodec() {
        return CODEC;
    }

    public BannerBlock(DyeColor dyeColor, AbstractBlock.Settings settings) {
        super(dyeColor, settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)ROTATION, (Comparable)Integer.valueOf(0)));
        COLORED_BANNERS.put(dyeColor, this);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSolid();
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)ROTATION, (Comparable)Integer.valueOf(RotationPropertyHelper.fromYaw((float)(ctx.getPlayerYaw() + 180.0f))));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)ROTATION, (Comparable)Integer.valueOf(rotation.rotate(((Integer)state.get((Property)ROTATION)).intValue(), 16)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)state.with((Property)ROTATION, (Comparable)Integer.valueOf(mirror.mirror(((Integer)state.get((Property)ROTATION)).intValue(), 16)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ROTATION});
    }

    public static Block getForColor(DyeColor color) {
        return COLORED_BANNERS.getOrDefault(color, Blocks.WHITE_BANNER);
    }
}

