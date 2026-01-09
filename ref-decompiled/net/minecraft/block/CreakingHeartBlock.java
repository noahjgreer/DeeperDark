package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.BiConsumer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CreakingHeartBlockEntity;
import net.minecraft.block.enums.CreakingHeartState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionImpl;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class CreakingHeartBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(CreakingHeartBlock::new);
   public static final EnumProperty AXIS;
   public static final EnumProperty ACTIVE;
   public static final BooleanProperty NATURAL;

   public MapCodec getCodec() {
      return CODEC;
   }

   public CreakingHeartBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.getDefaultState().with(AXIS, Direction.Axis.Y)).with(ACTIVE, CreakingHeartState.UPROOTED)).with(NATURAL, false));
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new CreakingHeartBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      if (world.isClient) {
         return null;
      } else {
         return state.get(ACTIVE) != CreakingHeartState.UPROOTED ? validateTicker(type, BlockEntityType.CREAKING_HEART, CreakingHeartBlockEntity::tick) : null;
      }
   }

   public static boolean isNightAndNatural(World world) {
      return world.isNightAndNatural();
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if (isNightAndNatural(world)) {
         if (state.get(ACTIVE) != CreakingHeartState.UPROOTED) {
            if (random.nextInt(16) == 0 && isSurroundedByPaleOakLogs(world, pos)) {
               world.playSoundClient((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_CREAKING_HEART_IDLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

         }
      }
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      tickView.scheduleBlockTick(pos, this, 1);
      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      BlockState blockState = enableIfValid(state, world, pos);
      if (blockState != state) {
         world.setBlockState(pos, blockState, 3);
      }

   }

   private static BlockState enableIfValid(BlockState state, World world, BlockPos pos) {
      boolean bl = shouldBeEnabled(state, world, pos);
      boolean bl2 = state.get(ACTIVE) == CreakingHeartState.UPROOTED;
      return bl && bl2 ? (BlockState)state.with(ACTIVE, isNightAndNatural(world) ? CreakingHeartState.AWAKE : CreakingHeartState.DORMANT) : state;
   }

   public static boolean shouldBeEnabled(BlockState state, WorldView world, BlockPos pos) {
      Direction.Axis axis = (Direction.Axis)state.get(AXIS);
      Direction[] var4 = axis.getDirections();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction direction = var4[var6];
         BlockState blockState = world.getBlockState(pos.offset(direction));
         if (!blockState.isIn(BlockTags.PALE_OAK_LOGS) || blockState.get(AXIS) != axis) {
            return false;
         }
      }

      return true;
   }

   private static boolean isSurroundedByPaleOakLogs(WorldAccess world, BlockPos pos) {
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction direction = var2[var4];
         BlockPos blockPos = pos.offset(direction);
         BlockState blockState = world.getBlockState(blockPos);
         if (!blockState.isIn(BlockTags.PALE_OAK_LOGS)) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return enableIfValid((BlockState)this.getDefaultState().with(AXIS, ctx.getSide().getAxis()), ctx.getWorld(), ctx.getBlockPos());
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return PillarBlock.changeRotation(state, rotation);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(AXIS, ACTIVE, NATURAL);
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      ItemScatterer.onStateReplaced(state, world, pos);
   }

   protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer stackMerger) {
      BlockEntity var8 = world.getBlockEntity(pos);
      if (var8 instanceof CreakingHeartBlockEntity creakingHeartBlockEntity) {
         if (explosion instanceof ExplosionImpl explosionImpl) {
            if (explosion.getDestructionType().destroysBlocks()) {
               creakingHeartBlockEntity.killPuppet(explosionImpl.getDamageSource());
               LivingEntity var9 = explosion.getCausingEntity();
               if (var9 instanceof PlayerEntity) {
                  PlayerEntity playerEntity = (PlayerEntity)var9;
                  if (explosion.getDestructionType().destroysBlocks()) {
                     this.dropExperienceOnBreak(playerEntity, state, world, pos);
                  }
               }
            }
         }
      }

      super.onExploded(state, world, pos, explosion, stackMerger);
   }

   public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      BlockEntity var6 = world.getBlockEntity(pos);
      if (var6 instanceof CreakingHeartBlockEntity creakingHeartBlockEntity) {
         creakingHeartBlockEntity.killPuppet(player.getDamageSources().playerAttack(player));
         this.dropExperienceOnBreak(player, state, world, pos);
      }

      return super.onBreak(world, pos, state, player);
   }

   private void dropExperienceOnBreak(PlayerEntity player, BlockState state, World world, BlockPos pos) {
      if (!player.shouldSkipBlockDrops() && !player.isSpectator() && (Boolean)state.get(NATURAL) && world instanceof ServerWorld serverWorld) {
         this.dropExperience(serverWorld, pos, world.random.nextBetween(20, 24));
      }

   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      if (state.get(ACTIVE) == CreakingHeartState.UPROOTED) {
         return 0;
      } else {
         BlockEntity var5 = world.getBlockEntity(pos);
         if (var5 instanceof CreakingHeartBlockEntity) {
            CreakingHeartBlockEntity creakingHeartBlockEntity = (CreakingHeartBlockEntity)var5;
            return creakingHeartBlockEntity.getComparatorOutput();
         } else {
            return 0;
         }
      }
   }

   static {
      AXIS = Properties.AXIS;
      ACTIVE = Properties.CREAKING_HEART_STATE;
      NATURAL = Properties.NATURAL;
   }
}
