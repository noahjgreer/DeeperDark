package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CropBlock extends PlantBlock implements Fertilizable {
   public static final MapCodec CODEC = createCodec(CropBlock::new);
   public static final int MAX_AGE = 7;
   public static final IntProperty AGE;
   private static final VoxelShape[] SHAPES_BY_AGE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public CropBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(this.getAgeProperty(), 0));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES_BY_AGE[this.getAge(state)];
   }

   protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
      return floor.isOf(Blocks.FARMLAND);
   }

   protected IntProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 7;
   }

   public int getAge(BlockState state) {
      return (Integer)state.get(this.getAgeProperty());
   }

   public BlockState withAge(int age) {
      return (BlockState)this.getDefaultState().with(this.getAgeProperty(), age);
   }

   public final boolean isMature(BlockState state) {
      return this.getAge(state) >= this.getMaxAge();
   }

   protected boolean hasRandomTicks(BlockState state) {
      return !this.isMature(state);
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (world.getBaseLightLevel(pos, 0) >= 9) {
         int i = this.getAge(state);
         if (i < this.getMaxAge()) {
            float f = getAvailableMoisture(this, world, pos);
            if (random.nextInt((int)(25.0F / f) + 1) == 0) {
               world.setBlockState(pos, this.withAge(i + 1), 2);
            }
         }
      }

   }

   public void applyGrowth(World world, BlockPos pos, BlockState state) {
      int i = Math.min(this.getMaxAge(), this.getAge(state) + this.getGrowthAmount(world));
      world.setBlockState(pos, this.withAge(i), 2);
   }

   protected int getGrowthAmount(World world) {
      return MathHelper.nextInt(world.random, 2, 5);
   }

   protected static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
      float f = 1.0F;
      BlockPos blockPos = pos.down();

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            float g = 0.0F;
            BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
            if (blockState.isOf(Blocks.FARMLAND)) {
               g = 1.0F;
               if ((Integer)blockState.get(FarmlandBlock.MOISTURE) > 0) {
                  g = 3.0F;
               }
            }

            if (i != 0 || j != 0) {
               g /= 4.0F;
            }

            f += g;
         }
      }

      BlockPos blockPos2 = pos.north();
      BlockPos blockPos3 = pos.south();
      BlockPos blockPos4 = pos.west();
      BlockPos blockPos5 = pos.east();
      boolean bl = world.getBlockState(blockPos4).isOf(block) || world.getBlockState(blockPos5).isOf(block);
      boolean bl2 = world.getBlockState(blockPos2).isOf(block) || world.getBlockState(blockPos3).isOf(block);
      if (bl && bl2) {
         f /= 2.0F;
      } else {
         boolean bl3 = world.getBlockState(blockPos4.north()).isOf(block) || world.getBlockState(blockPos5.north()).isOf(block) || world.getBlockState(blockPos5.south()).isOf(block) || world.getBlockState(blockPos4.south()).isOf(block);
         if (bl3) {
            f /= 2.0F;
         }
      }

      return f;
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return hasEnoughLightAt(world, pos) && super.canPlaceAt(state, world, pos);
   }

   protected static boolean hasEnoughLightAt(WorldView world, BlockPos pos) {
      return world.getBaseLightLevel(pos, 0) >= 8;
   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      if (world instanceof ServerWorld serverWorld) {
         if (entity instanceof RavagerEntity && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            serverWorld.breakBlock(pos, true, entity);
         }
      }

      super.onEntityCollision(state, world, pos, entity, handler);
   }

   protected ItemConvertible getSeedsItem() {
      return Items.WHEAT_SEEDS;
   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return new ItemStack(this.getSeedsItem());
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return !this.isMature(state);
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      this.applyGrowth(world, pos, state);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(AGE);
   }

   static {
      AGE = Properties.AGE_7;
      SHAPES_BY_AGE = Block.createShapeArray(7, (age) -> {
         return Block.createColumnShape(16.0, 0.0, (double)(2 + age * 2));
      });
   }
}
