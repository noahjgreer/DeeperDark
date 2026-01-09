package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TorchflowerBlock extends CropBlock {
   public static final MapCodec CODEC = createCodec(TorchflowerBlock::new);
   public static final int field_42775 = 1;
   public static final IntProperty AGE;
   private static final VoxelShape[] SHAPES_BY_AGE;
   private static final int field_44479 = 1;

   public MapCodec getCodec() {
      return CODEC;
   }

   public TorchflowerBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(AGE);
   }

   public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES_BY_AGE[this.getAge(state)];
   }

   protected IntProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 2;
   }

   protected ItemConvertible getSeedsItem() {
      return Items.TORCHFLOWER_SEEDS;
   }

   public BlockState withAge(int age) {
      return age == 2 ? Blocks.TORCHFLOWER.getDefaultState() : super.withAge(age);
   }

   public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (random.nextInt(3) != 0) {
         super.randomTick(state, world, pos, random);
      }

   }

   protected int getGrowthAmount(World world) {
      return 1;
   }

   static {
      AGE = Properties.AGE_1;
      SHAPES_BY_AGE = Block.createShapeArray(1, (age) -> {
         return Block.createColumnShape(6.0, 0.0, (double)(6 + age * 4));
      });
   }
}
