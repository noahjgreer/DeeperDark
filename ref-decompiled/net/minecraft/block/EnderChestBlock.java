package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class EnderChestBlock extends AbstractChestBlock implements Waterloggable {
   public static final MapCodec CODEC = createCodec(EnderChestBlock::new);
   public static final EnumProperty FACING;
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape SHAPE;
   private static final Text CONTAINER_NAME;

   public MapCodec getCodec() {
      return CODEC;
   }

   public EnderChestBlock(AbstractBlock.Settings settings) {
      super(settings, () -> {
         return BlockEntityType.ENDER_CHEST;
      });
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, false));
   }

   public DoubleBlockProperties.PropertySource getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
      return DoubleBlockProperties.PropertyRetriever::getFallback;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
      return (BlockState)((BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      EnderChestInventory enderChestInventory = player.getEnderChestInventory();
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (enderChestInventory != null && blockEntity instanceof EnderChestBlockEntity enderChestBlockEntity) {
         BlockPos blockPos = pos.up();
         if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
            return ActionResult.SUCCESS;
         } else {
            if (world instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)world;
               enderChestInventory.setActiveBlockEntity(enderChestBlockEntity);
               player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerx) -> {
                  return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, enderChestInventory);
               }, CONTAINER_NAME));
               player.incrementStat(Stats.OPEN_ENDERCHEST);
               PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true);
            }

            return ActionResult.SUCCESS;
         }
      } else {
         return ActionResult.SUCCESS;
      }
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new EnderChestBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return world.isClient ? validateTicker(type, BlockEntityType.ENDER_CHEST, EnderChestBlockEntity::clientTick) : null;
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      for(int i = 0; i < 3; ++i) {
         int j = random.nextInt(2) * 2 - 1;
         int k = random.nextInt(2) * 2 - 1;
         double d = (double)pos.getX() + 0.5 + 0.25 * (double)j;
         double e = (double)((float)pos.getY() + random.nextFloat());
         double f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
         double g = (double)(random.nextFloat() * (float)j);
         double h = ((double)random.nextFloat() - 0.5) * 0.125;
         double l = (double)(random.nextFloat() * (float)k);
         world.addParticleClient(ParticleTypes.PORTAL, d, e, f, g, h, l);
      }

   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, WATERLOGGED);
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if ((Boolean)state.get(WATERLOGGED)) {
         tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof EnderChestBlockEntity) {
         ((EnderChestBlockEntity)blockEntity).onScheduledTick();
      }

   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      WATERLOGGED = Properties.WATERLOGGED;
      SHAPE = Block.createColumnShape(14.0, 0.0, 14.0);
      CONTAINER_NAME = Text.translatable("container.enderchest");
   }
}
