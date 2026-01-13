/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockEntityProvider
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.JigsawBlock
 *  net.minecraft.block.OperatorBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.JigsawBlockEntity
 *  net.minecraft.block.entity.JigsawBlockEntity$Joint
 *  net.minecraft.block.enums.Orientation
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.structure.StructureTemplate$JigsawBlockInfo
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.Orientation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public class JigsawBlock
extends Block
implements BlockEntityProvider,
OperatorBlock {
    public static final MapCodec<JigsawBlock> CODEC = JigsawBlock.createCodec(JigsawBlock::new);
    public static final EnumProperty<Orientation> ORIENTATION = Properties.ORIENTATION;

    public MapCodec<JigsawBlock> getCodec() {
        return CODEC;
    }

    public JigsawBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)ORIENTATION, (Comparable)Orientation.NORTH_UP));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ORIENTATION});
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)ORIENTATION, (Comparable)rotation.getDirectionTransformation().mapJigsawOrientation((Orientation)state.get((Property)ORIENTATION)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)state.with((Property)ORIENTATION, (Comparable)mirror.getDirectionTransformation().mapJigsawOrientation((Orientation)state.get((Property)ORIENTATION)));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        Direction direction2 = direction.getAxis() == Direction.Axis.Y ? ctx.getHorizontalPlayerFacing().getOpposite() : Direction.UP;
        return (BlockState)this.getDefaultState().with((Property)ORIENTATION, (Comparable)Orientation.byDirections((Direction)direction, (Direction)direction2));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new JigsawBlockEntity(pos, state);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof JigsawBlockEntity && player.isCreativeLevelTwoOp()) {
            player.openJigsawScreen((JigsawBlockEntity)blockEntity);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static boolean attachmentMatches(StructureTemplate.JigsawBlockInfo info1, StructureTemplate.JigsawBlockInfo info2) {
        Direction direction = JigsawBlock.getFacing((BlockState)info1.info().state());
        Direction direction2 = JigsawBlock.getFacing((BlockState)info2.info().state());
        Direction direction3 = JigsawBlock.getRotation((BlockState)info1.info().state());
        Direction direction4 = JigsawBlock.getRotation((BlockState)info2.info().state());
        JigsawBlockEntity.Joint joint = info1.jointType();
        boolean bl = joint == JigsawBlockEntity.Joint.ROLLABLE;
        return direction == direction2.getOpposite() && (bl || direction3 == direction4) && info1.target().equals((Object)info2.name());
    }

    public static Direction getFacing(BlockState state) {
        return ((Orientation)state.get((Property)ORIENTATION)).getFacing();
    }

    public static Direction getRotation(BlockState state) {
        return ((Orientation)state.get((Property)ORIENTATION)).getRotation();
    }
}

