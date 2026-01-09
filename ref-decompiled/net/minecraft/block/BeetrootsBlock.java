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

public class BeetrootsBlock extends CropBlock {
   public static final MapCodec CODEC = createCodec(BeetrootsBlock::new);
   public static final int BEETROOTS_MAX_AGE = 3;
   public static final IntProperty AGE;
   private static final VoxelShape[] SHAPES_BY_AGE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public BeetrootsBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected IntProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 3;
   }

   protected ItemConvertible getSeedsItem() {
      return Items.BEETROOT_SEEDS;
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (random.nextInt(3) != 0) {
         super.randomTick(state, world, pos, random);
      }

   }

   protected int getGrowthAmount(World world) {
      return super.getGrowthAmount(world) / 3;
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(AGE);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES_BY_AGE[this.getAge(state)];
   }

   static {
      AGE = Properties.AGE_3;
      SHAPES_BY_AGE = Block.createShapeArray(3, (age) -> {
         return Block.createColumnShape(16.0, 0.0, (double)(2 + age * 2));
      });
   }
}
