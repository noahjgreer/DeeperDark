package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class SaplingBlock extends PlantBlock implements Fertilizable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(SaplingGenerator.CODEC.fieldOf("tree").forGetter((block) -> {
         return block.generator;
      }), createSettingsCodec()).apply(instance, SaplingBlock::new);
   });
   public static final IntProperty STAGE;
   private static final VoxelShape SHAPE;
   protected final SaplingGenerator generator;

   public MapCodec getCodec() {
      return CODEC;
   }

   public SaplingBlock(SaplingGenerator generator, AbstractBlock.Settings settings) {
      super(settings);
      this.generator = generator;
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(STAGE, 0));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(7) == 0) {
         this.generate(world, pos, state, random);
      }

   }

   public void generate(ServerWorld world, BlockPos pos, BlockState state, Random random) {
      if ((Integer)state.get(STAGE) == 0) {
         world.setBlockState(pos, (BlockState)state.cycle(STAGE), 260);
      } else {
         this.generator.generate(world, world.getChunkManager().getChunkGenerator(), pos, state, random);
      }

   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return true;
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return (double)world.random.nextFloat() < 0.45;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      this.generate(world, pos, state, random);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(STAGE);
   }

   static {
      STAGE = Properties.STAGE;
      SHAPE = Block.createColumnShape(12.0, 0.0, 12.0);
   }
}
