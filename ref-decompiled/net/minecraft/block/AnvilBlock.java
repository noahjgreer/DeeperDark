/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AnvilBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FallingBlock
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.FallingBlockEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.screen.AnvilScreenHandler
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandlerContext
 *  net.minecraft.screen.SimpleNamedScreenHandlerFactory
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.text.Text
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public class AnvilBlock
extends FallingBlock {
    public static final MapCodec<AnvilBlock> CODEC = AnvilBlock.createCodec(AnvilBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    private static final Map<Direction.Axis, VoxelShape> SHAPES_BY_AXIS = VoxelShapes.createHorizontalAxisShapeMap((VoxelShape)VoxelShapes.union((VoxelShape)Block.createColumnShape((double)12.0, (double)0.0, (double)4.0), (VoxelShape[])new VoxelShape[]{Block.createColumnShape((double)8.0, (double)10.0, (double)4.0, (double)5.0), Block.createColumnShape((double)4.0, (double)8.0, (double)5.0, (double)10.0), Block.createColumnShape((double)10.0, (double)16.0, (double)10.0, (double)16.0)}));
    private static final Text TITLE = Text.translatable((String)"container.repair");
    private static final float FALLING_BLOCK_ENTITY_DAMAGE_MULTIPLIER = 2.0f;
    private static final int FALLING_BLOCK_ENTITY_MAX_DAMAGE = 40;

    public MapCodec<AnvilBlock> getCodec() {
        return CODEC;
    }

    public AnvilBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing().rotateYClockwise());
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_ANVIL);
        }
        return ActionResult.SUCCESS;
    }

    protected @Nullable NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new AnvilScreenHandler(syncId, inventory, ScreenHandlerContext.create((World)world, (BlockPos)pos)), TITLE);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)SHAPES_BY_AXIS.get(((Direction)state.get((Property)FACING)).getAxis());
    }

    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
        entity.setHurtEntities(2.0f, 40);
    }

    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            world.syncWorldEvent(1031, pos, 0);
        }
    }

    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            world.syncWorldEvent(1029, pos, 0);
        }
    }

    public DamageSource getDamageSource(Entity attacker) {
        return attacker.getDamageSources().fallingAnvil(attacker);
    }

    public static @Nullable BlockState getLandingState(BlockState fallingState) {
        if (fallingState.isOf(Blocks.ANVIL)) {
            return (BlockState)Blocks.CHIPPED_ANVIL.getDefaultState().with((Property)FACING, (Comparable)((Direction)fallingState.get((Property)FACING)));
        }
        if (fallingState.isOf(Blocks.CHIPPED_ANVIL)) {
            return (BlockState)Blocks.DAMAGED_ANVIL.getDefaultState().with((Property)FACING, (Comparable)((Direction)fallingState.get((Property)FACING)));
        }
        return null;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return state.getMapColor((BlockView)world, (BlockPos)pos).color;
    }
}

