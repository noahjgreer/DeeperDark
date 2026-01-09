package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class NetherWartBlock extends PlantBlock {
   public static final MapCodec CODEC = createCodec(NetherWartBlock::new);
   public static final int MAX_AGE = 3;
   public static final IntProperty AGE;
   private static final VoxelShape[] SHAPES_BY_AGE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public NetherWartBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES_BY_AGE[(Integer)state.get(AGE)];
   }

   protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
      return floor.isOf(Blocks.SOUL_SAND);
   }

   protected boolean hasRandomTicks(BlockState state) {
      return (Integer)state.get(AGE) < 3;
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      int i = (Integer)state.get(AGE);
      if (i < 3 && random.nextInt(10) == 0) {
         state = (BlockState)state.with(AGE, i + 1);
         world.setBlockState(pos, state, 2);
      }

   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return new ItemStack(Items.NETHER_WART);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(AGE);
   }

   static {
      AGE = Properties.AGE_3;
      SHAPES_BY_AGE = Block.createShapeArray(3, (age) -> {
         return Block.createColumnShape(16.0, 0.0, (double)(5 + age * 3));
      });
   }
}
