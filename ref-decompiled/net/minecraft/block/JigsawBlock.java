package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.Orientation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class JigsawBlock extends Block implements BlockEntityProvider, OperatorBlock {
   public static final MapCodec CODEC = createCodec(JigsawBlock::new);
   public static final EnumProperty ORIENTATION;

   public MapCodec getCodec() {
      return CODEC;
   }

   public JigsawBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(ORIENTATION, Orientation.NORTH_UP));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(ORIENTATION);
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(ORIENTATION, rotation.getDirectionTransformation().mapJigsawOrientation((Orientation)state.get(ORIENTATION)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return (BlockState)state.with(ORIENTATION, mirror.getDirectionTransformation().mapJigsawOrientation((Orientation)state.get(ORIENTATION)));
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      Direction direction = ctx.getSide();
      Direction direction2;
      if (direction.getAxis() == Direction.Axis.Y) {
         direction2 = ctx.getHorizontalPlayerFacing().getOpposite();
      } else {
         direction2 = Direction.UP;
      }

      return (BlockState)this.getDefaultState().with(ORIENTATION, Orientation.byDirections(direction, direction2));
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new JigsawBlockEntity(pos, state);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof JigsawBlockEntity && player.isCreativeLevelTwoOp()) {
         player.openJigsawScreen((JigsawBlockEntity)blockEntity);
         return ActionResult.SUCCESS;
      } else {
         return ActionResult.PASS;
      }
   }

   public static boolean attachmentMatches(StructureTemplate.JigsawBlockInfo info1, StructureTemplate.JigsawBlockInfo info2) {
      Direction direction = getFacing(info1.info().state());
      Direction direction2 = getFacing(info2.info().state());
      Direction direction3 = getRotation(info1.info().state());
      Direction direction4 = getRotation(info2.info().state());
      JigsawBlockEntity.Joint joint = info1.jointType();
      boolean bl = joint == JigsawBlockEntity.Joint.ROLLABLE;
      return direction == direction2.getOpposite() && (bl || direction3 == direction4) && info1.target().equals(info2.name());
   }

   public static Direction getFacing(BlockState state) {
      return ((Orientation)state.get(ORIENTATION)).getFacing();
   }

   public static Direction getRotation(BlockState state) {
      return ((Orientation)state.get(ORIENTATION)).getRotation();
   }

   static {
      ORIENTATION = Properties.ORIENTATION;
   }
}
