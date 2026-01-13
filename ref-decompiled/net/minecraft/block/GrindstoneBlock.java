/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.GrindstoneBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.WallMountedBlock
 *  net.minecraft.block.enums.BlockFace
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.GrindstoneScreenHandler
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandlerContext
 *  net.minecraft.screen.SimpleNamedScreenHandlerFactory
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.Property
 *  net.minecraft.text.Text
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.DirectionTransformation
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class GrindstoneBlock
extends WallMountedBlock {
    public static final MapCodec<GrindstoneBlock> CODEC = GrindstoneBlock.createCodec(GrindstoneBlock::new);
    private static final Text TITLE = Text.translatable((String)"container.grindstone_title");
    private final Function<BlockState, VoxelShape> shapeFunction;

    public MapCodec<GrindstoneBlock> getCodec() {
        return CODEC;
    }

    public GrindstoneBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)FACE, (Comparable)BlockFace.WALL));
        this.shapeFunction = this.createShapeFunction();
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        VoxelShape voxelShape = VoxelShapes.union((VoxelShape)Block.createCuboidShape((double)2.0, (double)6.0, (double)7.0, (double)4.0, (double)10.0, (double)16.0), (VoxelShape)Block.createCuboidShape((double)2.0, (double)5.0, (double)3.0, (double)4.0, (double)11.0, (double)9.0));
        VoxelShape voxelShape2 = VoxelShapes.transform((VoxelShape)voxelShape, (DirectionTransformation)DirectionTransformation.INVERT_X);
        VoxelShape voxelShape3 = VoxelShapes.union((VoxelShape)Block.createCuboidZShape((double)8.0, (double)2.0, (double)14.0, (double)0.0, (double)12.0), (VoxelShape[])new VoxelShape[]{voxelShape, voxelShape2});
        Map map = VoxelShapes.createBlockFaceHorizontalFacingShapeMap((VoxelShape)voxelShape3);
        return this.createShapeFunction(state -> (VoxelShape)((Map)map.get(state.get((Property)FACE))).get(state.get((Property)FACING)));
    }

    private VoxelShape getShape(BlockState state) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getShape(state);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getShape(state);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_GRINDSTONE);
        }
        return ActionResult.SUCCESS;
    }

    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new GrindstoneScreenHandler(syncId, inventory, ScreenHandlerContext.create((World)world, (BlockPos)pos)), TITLE);
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, FACE});
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

