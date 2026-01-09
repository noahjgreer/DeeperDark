package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class HangingMossBlock extends Block implements Fertilizable {
   public static final MapCodec CODEC = createCodec(HangingMossBlock::new);
   private static final VoxelShape SHAPE = Block.createColumnShape(14.0, 0.0, 16.0);
   private static final VoxelShape TIP_SHAPE = Block.createColumnShape(14.0, 2.0, 16.0);
   public static final BooleanProperty TIP;

   public MapCodec getCodec() {
      return CODEC;
   }

   public HangingMossBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(TIP, true));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (Boolean)state.get(TIP) ? TIP_SHAPE : SHAPE;
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if (random.nextInt(500) == 0) {
         BlockState blockState = world.getBlockState(pos.up());
         if (blockState.isIn(BlockTags.PALE_OAK_LOGS) || blockState.isOf(Blocks.PALE_OAK_LEAVES)) {
            world.playSoundClient((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_PALE_HANGING_MOSS_IDLE, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
         }
      }

   }

   protected boolean isTransparent(BlockState state) {
      return true;
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return this.canPlaceAt(world, pos);
   }

   private boolean canPlaceAt(BlockView world, BlockPos pos) {
      BlockPos blockPos = pos.offset(Direction.UP);
      BlockState blockState = world.getBlockState(blockPos);
      return MultifaceBlock.canGrowOn(world, Direction.UP, blockPos, blockState) || blockState.isOf(Blocks.PALE_HANGING_MOSS);
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (!this.canPlaceAt(world, pos)) {
         tickView.scheduleBlockTick(pos, this, 1);
      }

      return (BlockState)state.with(TIP, !world.getBlockState(pos.down()).isOf(this));
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (!this.canPlaceAt(world, pos)) {
         world.breakBlock(pos, true);
      }

   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(TIP);
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return this.canGrowInto(world.getBlockState(this.getTipPos(world, pos).down()));
   }

   private boolean canGrowInto(BlockState state) {
      return state.isAir();
   }

   public BlockPos getTipPos(BlockView world, BlockPos pos) {
      BlockPos.Mutable mutable = pos.mutableCopy();

      BlockState blockState;
      do {
         mutable.move(Direction.DOWN);
         blockState = world.getBlockState(mutable);
      } while(blockState.isOf(this));

      return mutable.offset(Direction.UP).toImmutable();
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      BlockPos blockPos = this.getTipPos(world, pos).down();
      if (this.canGrowInto(world.getBlockState(blockPos))) {
         world.setBlockState(blockPos, (BlockState)state.with(TIP, true));
      }
   }

   static {
      TIP = Properties.TIP;
   }
}
