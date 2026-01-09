package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class TurtleEggBlock extends Block {
   public static final MapCodec CODEC = createCodec(TurtleEggBlock::new);
   public static final IntProperty HATCH;
   public static final IntProperty EGGS;
   public static final int field_31272 = 2;
   public static final int field_31273 = 1;
   public static final int field_31274 = 4;
   private static final VoxelShape SINGLE_SHAPE;
   private static final VoxelShape MULTIPLE_SHAPE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public TurtleEggBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HATCH, 0)).with(EGGS, 1));
   }

   public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
      if (!entity.bypassesSteppingEffects()) {
         this.tryBreakEgg(world, state, pos, entity, 100);
      }

      super.onSteppedOn(world, pos, state, entity);
   }

   public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
      if (!(entity instanceof ZombieEntity)) {
         this.tryBreakEgg(world, state, pos, entity, 3);
      }

      super.onLandedUpon(world, state, pos, entity, fallDistance);
   }

   private void tryBreakEgg(World world, BlockState state, BlockPos pos, Entity entity, int inverseChance) {
      if (state.isOf(Blocks.TURTLE_EGG) && world instanceof ServerWorld serverWorld) {
         if (this.breaksEgg(serverWorld, entity) && world.random.nextInt(inverseChance) == 0) {
            this.breakEgg(serverWorld, pos, state);
         }
      }

   }

   private void breakEgg(World world, BlockPos pos, BlockState state) {
      world.playSound((Entity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
      int i = (Integer)state.get(EGGS);
      if (i <= 1) {
         world.breakBlock(pos, false);
      } else {
         world.setBlockState(pos, (BlockState)state.with(EGGS, i - 1), 2);
         world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(state));
         world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
      }

   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (this.shouldHatchProgress(world) && isSandBelow(world, pos)) {
         int i = (Integer)state.get(HATCH);
         if (i < 2) {
            world.playSound((Entity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            world.setBlockState(pos, (BlockState)state.with(HATCH, i + 1), 2);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
         } else {
            world.playSound((Entity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            world.removeBlock(pos, false);
            world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(state));

            for(int j = 0; j < (Integer)state.get(EGGS); ++j) {
               world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
               TurtleEntity turtleEntity = (TurtleEntity)EntityType.TURTLE.create(world, SpawnReason.BREEDING);
               if (turtleEntity != null) {
                  turtleEntity.setBreedingAge(-24000);
                  turtleEntity.setHomePos(pos);
                  turtleEntity.refreshPositionAndAngles((double)pos.getX() + 0.3 + (double)j * 0.2, (double)pos.getY(), (double)pos.getZ() + 0.3, 0.0F, 0.0F);
                  world.spawnEntity(turtleEntity);
               }
            }
         }
      }

   }

   public static boolean isSandBelow(BlockView world, BlockPos pos) {
      return isSand(world, pos.down());
   }

   public static boolean isSand(BlockView world, BlockPos pos) {
      return world.getBlockState(pos).isIn(BlockTags.SAND);
   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      if (isSandBelow(world, pos) && !world.isClient) {
         world.syncWorldEvent(2012, pos, 15);
      }

   }

   private boolean shouldHatchProgress(World world) {
      float f = world.getSkyAngle(1.0F);
      if ((double)f < 0.69 && (double)f > 0.65) {
         return true;
      } else {
         return world.random.nextInt(500) == 0;
      }
   }

   public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
      super.afterBreak(world, player, pos, state, blockEntity, tool);
      this.breakEgg(world, pos, state);
   }

   protected boolean canReplace(BlockState state, ItemPlacementContext context) {
      return !context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && (Integer)state.get(EGGS) < 4 ? true : super.canReplace(state, context);
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
      return blockState.isOf(this) ? (BlockState)blockState.with(EGGS, Math.min(4, (Integer)blockState.get(EGGS) + 1)) : super.getPlacementState(ctx);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (Integer)state.get(EGGS) == 1 ? SINGLE_SHAPE : MULTIPLE_SHAPE;
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(HATCH, EGGS);
   }

   private boolean breaksEgg(ServerWorld world, Entity entity) {
      if (!(entity instanceof TurtleEntity) && !(entity instanceof BatEntity)) {
         if (!(entity instanceof LivingEntity)) {
            return false;
         } else {
            return entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
         }
      } else {
         return false;
      }
   }

   static {
      HATCH = Properties.HATCH;
      EGGS = Properties.EGGS;
      SINGLE_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
      MULTIPLE_SHAPE = Block.createColumnShape(14.0, 0.0, 7.0);
   }
}
