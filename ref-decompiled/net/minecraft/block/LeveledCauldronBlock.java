package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.CollisionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

public class LeveledCauldronBlock extends AbstractCauldronBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter((block) -> {
         return block.precipitation;
      }), CauldronBehavior.CODEC.fieldOf("interactions").forGetter((block) -> {
         return block.behaviorMap;
      }), createSettingsCodec()).apply(instance, LeveledCauldronBlock::new);
   });
   public static final int MIN_LEVEL = 1;
   public static final int MAX_LEVEL = 3;
   public static final IntProperty LEVEL;
   private static final int BASE_FLUID_HEIGHT = 6;
   private static final double FLUID_HEIGHT_PER_LEVEL = 3.0;
   private static final VoxelShape[] INSIDE_COLLISION_SHAPE_BY_LEVEL;
   private final Biome.Precipitation precipitation;

   public MapCodec getCodec() {
      return CODEC;
   }

   public LeveledCauldronBlock(Biome.Precipitation precipitation, CauldronBehavior.CauldronBehaviorMap behaviorMap, AbstractBlock.Settings settings) {
      super(settings, behaviorMap);
      this.precipitation = precipitation;
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LEVEL, 1));
   }

   public boolean isFull(BlockState state) {
      return (Integer)state.get(LEVEL) == 3;
   }

   protected boolean canBeFilledByDripstone(Fluid fluid) {
      return fluid == Fluids.WATER && this.precipitation == Biome.Precipitation.RAIN;
   }

   protected double getFluidHeight(BlockState state) {
      return getFluidHeight((Integer)state.get(LEVEL)) / 16.0;
   }

   private static double getFluidHeight(int level) {
      return 6.0 + (double)level * 3.0;
   }

   protected VoxelShape getInsideCollisionShape(BlockState state, BlockView world, BlockPos pos, Entity entity) {
      return INSIDE_COLLISION_SHAPE_BY_LEVEL[(Integer)state.get(LEVEL) - 1];
   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      if (world instanceof ServerWorld serverWorld) {
         BlockPos blockPos = pos.toImmutable();
         handler.addPreCallback(CollisionEvent.EXTINGUISH, (collidedEntity) -> {
            if (collidedEntity.isOnFire() && collidedEntity.canModifyAt(serverWorld, blockPos)) {
               this.onFireCollision(state, world, blockPos);
            }

         });
      }

      handler.addEvent(CollisionEvent.EXTINGUISH);
   }

   private void onFireCollision(BlockState state, World world, BlockPos pos) {
      if (this.precipitation == Biome.Precipitation.SNOW) {
         decrementFluidLevel((BlockState)Blocks.WATER_CAULDRON.getDefaultState().with(LEVEL, (Integer)state.get(LEVEL)), world, pos);
      } else {
         decrementFluidLevel(state, world, pos);
      }

   }

   public static void decrementFluidLevel(BlockState state, World world, BlockPos pos) {
      int i = (Integer)state.get(LEVEL) - 1;
      BlockState blockState = i == 0 ? Blocks.CAULDRON.getDefaultState() : (BlockState)state.with(LEVEL, i);
      world.setBlockState(pos, blockState);
      world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
   }

   public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
      if (CauldronBlock.canFillWithPrecipitation(world, precipitation) && (Integer)state.get(LEVEL) != 3 && precipitation == this.precipitation) {
         BlockState blockState = (BlockState)state.cycle(LEVEL);
         world.setBlockState(pos, blockState);
         world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
      }
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      return (Integer)state.get(LEVEL);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(LEVEL);
   }

   protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
      if (!this.isFull(state)) {
         BlockState blockState = (BlockState)state.with(LEVEL, (Integer)state.get(LEVEL) + 1);
         world.setBlockState(pos, blockState);
         world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
         world.syncWorldEvent(1047, pos, 0);
      }
   }

   static {
      LEVEL = Properties.LEVEL_3;
      INSIDE_COLLISION_SHAPE_BY_LEVEL = (VoxelShape[])Util.make(() -> {
         return Block.createShapeArray(2, (level) -> {
            return VoxelShapes.union(AbstractCauldronBlock.OUTLINE_SHAPE, Block.createColumnShape(12.0, 4.0, getFluidHeight(level + 1)));
         });
      });
   }
}
