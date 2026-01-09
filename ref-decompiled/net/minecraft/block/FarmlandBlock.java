package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class FarmlandBlock extends Block {
   public static final MapCodec CODEC = createCodec(FarmlandBlock::new);
   public static final IntProperty MOISTURE;
   private static final VoxelShape SHAPE;
   public static final int MAX_MOISTURE = 7;

   public MapCodec getCodec() {
      return CODEC;
   }

   public FarmlandBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(MOISTURE, 0));
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (direction == Direction.UP && !state.canPlaceAt(world, pos)) {
         tickView.scheduleBlockTick(pos, this, 1);
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos.up());
      return !blockState.isSolid() || blockState.getBlock() instanceof FenceGateBlock || blockState.getBlock() instanceof PistonExtensionBlock;
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return !this.getDefaultState().canPlaceAt(ctx.getWorld(), ctx.getBlockPos()) ? Blocks.DIRT.getDefaultState() : super.getPlacementState(ctx);
   }

   protected boolean hasSidedTransparency(BlockState state) {
      return true;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (!state.canPlaceAt(world, pos)) {
         setToDirt((Entity)null, state, world, pos);
      }

   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      int i = (Integer)state.get(MOISTURE);
      if (!isWaterNearby(world, pos) && !world.hasRain(pos.up())) {
         if (i > 0) {
            world.setBlockState(pos, (BlockState)state.with(MOISTURE, i - 1), 2);
         } else if (!hasCrop(world, pos)) {
            setToDirt((Entity)null, state, world, pos);
         }
      } else if (i < 7) {
         world.setBlockState(pos, (BlockState)state.with(MOISTURE, 7), 2);
      }

   }

   public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
      if (world instanceof ServerWorld serverWorld) {
         if ((double)world.random.nextFloat() < fallDistance - 0.5 && entity instanceof LivingEntity && (entity instanceof PlayerEntity || serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) && entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512F) {
            setToDirt(entity, state, world, pos);
         }
      }

      super.onLandedUpon(world, state, pos, entity, fallDistance);
   }

   public static void setToDirt(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
      BlockState blockState = pushEntitiesUpBeforeBlockChange(state, Blocks.DIRT.getDefaultState(), world, pos);
      world.setBlockState(pos, blockState);
      world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(entity, blockState));
   }

   private static boolean hasCrop(BlockView world, BlockPos pos) {
      return world.getBlockState(pos.up()).isIn(BlockTags.MAINTAINS_FARMLAND);
   }

   private static boolean isWaterNearby(WorldView world, BlockPos pos) {
      Iterator var2 = BlockPos.iterate(pos.add(-4, 0, -4), pos.add(4, 1, 4)).iterator();

      BlockPos blockPos;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         blockPos = (BlockPos)var2.next();
      } while(!world.getFluidState(blockPos).isIn(FluidTags.WATER));

      return true;
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(MOISTURE);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   static {
      MOISTURE = Properties.MOISTURE;
      SHAPE = Block.createColumnShape(16.0, 0.0, 15.0);
   }
}
