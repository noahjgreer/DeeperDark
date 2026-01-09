package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class WallRedstoneTorchBlock extends RedstoneTorchBlock {
   public static final MapCodec CODEC = createCodec(WallRedstoneTorchBlock::new);
   public static final EnumProperty FACING;
   public static final BooleanProperty LIT;

   public MapCodec getCodec() {
      return CODEC;
   }

   public WallRedstoneTorchBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(LIT, true));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return WallTorchBlock.getBoundingShape(state);
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return WallTorchBlock.canPlaceAt(world, pos, (Direction)state.get(FACING));
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : state;
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState blockState = Blocks.WALL_TORCH.getPlacementState(ctx);
      return blockState == null ? null : (BlockState)this.getDefaultState().with(FACING, (Direction)blockState.get(FACING));
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if ((Boolean)state.get(LIT)) {
         Direction direction = ((Direction)state.get(FACING)).getOpposite();
         double d = 0.27;
         double e = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetX();
         double f = (double)pos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2 + 0.22;
         double g = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetZ();
         world.addParticleClient(DustParticleEffect.DEFAULT, e, f, g, 0.0, 0.0, 0.0);
      }
   }

   protected boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
      Direction direction = ((Direction)state.get(FACING)).getOpposite();
      return world.isEmittingRedstonePower(pos.offset(direction), direction);
   }

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Boolean)state.get(LIT) && state.get(FACING) != direction ? 15 : 0;
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, LIT);
   }

   @Nullable
   protected WireOrientation getEmissionOrientation(World world, BlockState state) {
      return OrientationHelper.getEmissionOrientation(world, ((Direction)state.get(FACING)).getOpposite(), Direction.UP);
   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      LIT = RedstoneTorchBlock.LIT;
   }
}
