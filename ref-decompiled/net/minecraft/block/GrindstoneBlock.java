package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
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

public class GrindstoneBlock extends WallMountedBlock {
   public static final MapCodec CODEC = createCodec(GrindstoneBlock::new);
   private static final Text TITLE = Text.translatable("container.grindstone_title");
   private final Function shapeFunction;

   public MapCodec getCodec() {
      return CODEC;
   }

   public GrindstoneBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(FACE, BlockFace.WALL));
      this.shapeFunction = this.createShapeFunction();
   }

   private Function createShapeFunction() {
      VoxelShape voxelShape = VoxelShapes.union(Block.createCuboidShape(2.0, 6.0, 7.0, 4.0, 10.0, 16.0), Block.createCuboidShape(2.0, 5.0, 3.0, 4.0, 11.0, 9.0));
      VoxelShape voxelShape2 = VoxelShapes.transform(voxelShape, DirectionTransformation.INVERT_X);
      VoxelShape voxelShape3 = VoxelShapes.union(Block.createCuboidZShape(8.0, 2.0, 14.0, 0.0, 12.0), voxelShape, voxelShape2);
      Map map = VoxelShapes.createBlockFaceHorizontalFacingShapeMap(voxelShape3);
      return this.createShapeFunction((state) -> {
         return (VoxelShape)((Map)map.get(state.get(FACE))).get(state.get(FACING));
      });
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
      if (!world.isClient) {
         player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
         player.incrementStat(Stats.INTERACT_WITH_GRINDSTONE);
      }

      return ActionResult.SUCCESS;
   }

   protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
      return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
         return new GrindstoneScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
      }, TITLE);
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, FACE);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }
}
