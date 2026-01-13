/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.StonecutterBlock
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandlerContext
 *  net.minecraft.screen.SimpleNamedScreenHandlerFactory
 *  net.minecraft.screen.StonecutterScreenHandler
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.text.Text
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public class StonecutterBlock
extends Block {
    public static final MapCodec<StonecutterBlock> CODEC = StonecutterBlock.createCodec(StonecutterBlock::new);
    private static final Text TITLE = Text.translatable((String)"container.stonecutter");
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)9.0);

    public MapCodec<StonecutterBlock> getCodec() {
        return CODEC;
    }

    public StonecutterBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing().getOpposite());
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_STONECUTTER);
        }
        return ActionResult.SUCCESS;
    }

    protected @Nullable NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, playerInventory, player) -> new StonecutterScreenHandler(syncId, playerInventory, ScreenHandlerContext.create((World)world, (BlockPos)pos)), TITLE);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

