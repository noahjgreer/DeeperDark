package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class ChestBlock extends AbstractChestBlock implements Waterloggable {
   public static final MapCodec CODEC = createCodec((settings) -> {
      return new ChestBlock(() -> {
         return BlockEntityType.CHEST;
      }, settings);
   });
   public static final EnumProperty FACING;
   public static final EnumProperty CHEST_TYPE;
   public static final BooleanProperty WATERLOGGED;
   public static final int field_31057 = 1;
   private static final VoxelShape SINGLE_SHAPE;
   private static final Map DOUBLE_SHAPES_BY_DIRECTION;
   private static final DoubleBlockProperties.PropertyRetriever INVENTORY_RETRIEVER;
   private static final DoubleBlockProperties.PropertyRetriever NAME_RETRIEVER;

   public MapCodec getCodec() {
      return CODEC;
   }

   public ChestBlock(Supplier blockEntityTypeSupplier, AbstractBlock.Settings settings) {
      super(settings, blockEntityTypeSupplier);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(CHEST_TYPE, ChestType.SINGLE)).with(WATERLOGGED, false));
   }

   public static DoubleBlockProperties.Type getDoubleBlockType(BlockState state) {
      ChestType chestType = (ChestType)state.get(CHEST_TYPE);
      if (chestType == ChestType.SINGLE) {
         return DoubleBlockProperties.Type.SINGLE;
      } else {
         return chestType == ChestType.RIGHT ? DoubleBlockProperties.Type.FIRST : DoubleBlockProperties.Type.SECOND;
      }
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if ((Boolean)state.get(WATERLOGGED)) {
         tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      if (neighborState.isOf(this) && direction.getAxis().isHorizontal()) {
         ChestType chestType = (ChestType)neighborState.get(CHEST_TYPE);
         if (state.get(CHEST_TYPE) == ChestType.SINGLE && chestType != ChestType.SINGLE && state.get(FACING) == neighborState.get(FACING) && getFacing(neighborState) == direction.getOpposite()) {
            return (BlockState)state.with(CHEST_TYPE, chestType.getOpposite());
         }
      } else if (getFacing(state) == direction) {
         return (BlockState)state.with(CHEST_TYPE, ChestType.SINGLE);
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      VoxelShape var10000;
      switch ((ChestType)state.get(CHEST_TYPE)) {
         case SINGLE:
            var10000 = SINGLE_SHAPE;
            break;
         case LEFT:
         case RIGHT:
            var10000 = (VoxelShape)DOUBLE_SHAPES_BY_DIRECTION.get(getFacing(state));
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static Direction getFacing(BlockState state) {
      Direction direction = (Direction)state.get(FACING);
      return state.get(CHEST_TYPE) == ChestType.LEFT ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      ChestType chestType = ChestType.SINGLE;
      Direction direction = ctx.getHorizontalPlayerFacing().getOpposite();
      FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
      boolean bl = ctx.shouldCancelInteraction();
      Direction direction2 = ctx.getSide();
      if (direction2.getAxis().isHorizontal() && bl) {
         Direction direction3 = this.getNeighborChestDirection(ctx, direction2.getOpposite());
         if (direction3 != null && direction3.getAxis() != direction2.getAxis()) {
            direction = direction3;
            chestType = direction3.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (chestType == ChestType.SINGLE && !bl) {
         if (direction == this.getNeighborChestDirection(ctx, direction.rotateYClockwise())) {
            chestType = ChestType.LEFT;
         } else if (direction == this.getNeighborChestDirection(ctx, direction.rotateYCounterclockwise())) {
            chestType = ChestType.RIGHT;
         }
      }

      return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, direction)).with(CHEST_TYPE, chestType)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   @Nullable
   private Direction getNeighborChestDirection(ItemPlacementContext ctx, Direction dir) {
      BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(dir));
      return blockState.isOf(this) && blockState.get(CHEST_TYPE) == ChestType.SINGLE ? (Direction)blockState.get(FACING) : null;
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      ItemScatterer.onStateReplaced(state, world, pos);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (world instanceof ServerWorld serverWorld) {
         NamedScreenHandlerFactory namedScreenHandlerFactory = this.createScreenHandlerFactory(state, world, pos);
         if (namedScreenHandlerFactory != null) {
            player.openHandledScreen(namedScreenHandlerFactory);
            player.incrementStat(this.getOpenStat());
            PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true);
         }
      }

      return ActionResult.SUCCESS;
   }

   protected Stat getOpenStat() {
      return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST);
   }

   public BlockEntityType getExpectedEntityType() {
      return (BlockEntityType)this.entityTypeRetriever.get();
   }

   @Nullable
   public static Inventory getInventory(ChestBlock block, BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
      return (Inventory)((Optional)block.getBlockEntitySource(state, world, pos, ignoreBlocked).apply(INVENTORY_RETRIEVER)).orElse((Object)null);
   }

   public DoubleBlockProperties.PropertySource getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
      BiPredicate biPredicate;
      if (ignoreBlocked) {
         biPredicate = (worldx, posx) -> {
            return false;
         };
      } else {
         biPredicate = ChestBlock::isChestBlocked;
      }

      return DoubleBlockProperties.toPropertySource((BlockEntityType)this.entityTypeRetriever.get(), ChestBlock::getDoubleBlockType, ChestBlock::getFacing, FACING, state, world, pos, biPredicate);
   }

   @Nullable
   protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
      return (NamedScreenHandlerFactory)((Optional)this.getBlockEntitySource(state, world, pos, false).apply(NAME_RETRIEVER)).orElse((Object)null);
   }

   public static DoubleBlockProperties.PropertyRetriever getAnimationProgressRetriever(final LidOpenable progress) {
      return new DoubleBlockProperties.PropertyRetriever() {
         public Float2FloatFunction getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
            return (tickProgress) -> {
               return Math.max(chestBlockEntity.getAnimationProgress(tickProgress), chestBlockEntity2.getAnimationProgress(tickProgress));
            };
         }

         public Float2FloatFunction getFrom(ChestBlockEntity chestBlockEntity) {
            Objects.requireNonNull(chestBlockEntity);
            return chestBlockEntity::getAnimationProgress;
         }

         public Float2FloatFunction getFallback() {
            LidOpenable var10000 = progress;
            Objects.requireNonNull(var10000);
            return var10000::getAnimationProgress;
         }

         // $FF: synthetic method
         public Object getFallback() {
            return this.getFallback();
         }
      };
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new ChestBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return world.isClient ? validateTicker(type, this.getExpectedEntityType(), ChestBlockEntity::clientTick) : null;
   }

   public static boolean isChestBlocked(WorldAccess world, BlockPos pos) {
      return hasBlockOnTop(world, pos) || hasCatOnTop(world, pos);
   }

   private static boolean hasBlockOnTop(BlockView world, BlockPos pos) {
      BlockPos blockPos = pos.up();
      return world.getBlockState(blockPos).isSolidBlock(world, blockPos);
   }

   private static boolean hasCatOnTop(WorldAccess world, BlockPos pos) {
      List list = world.getNonSpectatingEntities(CatEntity.class, new Box((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1)));
      if (!list.isEmpty()) {
         Iterator var3 = list.iterator();

         while(var3.hasNext()) {
            CatEntity catEntity = (CatEntity)var3.next();
            if (catEntity.isInSittingPose()) {
               return true;
            }
         }
      }

      return false;
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      return ScreenHandler.calculateComparatorOutput(getInventory(this, state, world, pos, false));
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, CHEST_TYPE, WATERLOGGED);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof ChestBlockEntity) {
         ((ChestBlockEntity)blockEntity).onScheduledTick();
      }

   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      CHEST_TYPE = Properties.CHEST_TYPE;
      WATERLOGGED = Properties.WATERLOGGED;
      SINGLE_SHAPE = Block.createColumnShape(14.0, 0.0, 14.0);
      DOUBLE_SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap(Block.createCuboidZShape(14.0, 0.0, 14.0, 0.0, 15.0));
      INVENTORY_RETRIEVER = new DoubleBlockProperties.PropertyRetriever() {
         public Optional getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
            return Optional.of(new DoubleInventory(chestBlockEntity, chestBlockEntity2));
         }

         public Optional getFrom(ChestBlockEntity chestBlockEntity) {
            return Optional.of(chestBlockEntity);
         }

         public Optional getFallback() {
            return Optional.empty();
         }

         // $FF: synthetic method
         public Object getFallback() {
            return this.getFallback();
         }
      };
      NAME_RETRIEVER = new DoubleBlockProperties.PropertyRetriever() {
         public Optional getFromBoth(final ChestBlockEntity chestBlockEntity, final ChestBlockEntity chestBlockEntity2) {
            final Inventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
            return Optional.of(new NamedScreenHandlerFactory(this) {
               @Nullable
               public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                  if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
                     chestBlockEntity.generateLoot(playerInventory.player);
                     chestBlockEntity2.generateLoot(playerInventory.player);
                     return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory);
                  } else {
                     return null;
                  }
               }

               public Text getDisplayName() {
                  if (chestBlockEntity.hasCustomName()) {
                     return chestBlockEntity.getDisplayName();
                  } else {
                     return (Text)(chestBlockEntity2.hasCustomName() ? chestBlockEntity2.getDisplayName() : Text.translatable("container.chestDouble"));
                  }
               }
            });
         }

         public Optional getFrom(ChestBlockEntity chestBlockEntity) {
            return Optional.of(chestBlockEntity);
         }

         public Optional getFallback() {
            return Optional.empty();
         }

         // $FF: synthetic method
         public Object getFallback() {
            return this.getFallback();
         }
      };
   }
}
