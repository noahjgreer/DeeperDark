package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class LecternBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(LecternBlock::new);
   public static final EnumProperty FACING;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty HAS_BOOK;
   private static final VoxelShape BASE_SHAPE;
   private static final Map OUTLINE_SHAPES_BY_DIRECTION;
   private static final int SCHEDULED_TICK_DELAY = 2;

   public MapCodec getCodec() {
      return CODEC;
   }

   public LecternBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(HAS_BOOK, false));
   }

   protected VoxelShape getCullingShape(BlockState state) {
      return BASE_SHAPE;
   }

   protected boolean hasSidedTransparency(BlockState state) {
      return true;
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      World world = ctx.getWorld();
      ItemStack itemStack = ctx.getStack();
      PlayerEntity playerEntity = ctx.getPlayer();
      boolean bl = false;
      if (!world.isClient && playerEntity != null && playerEntity.isCreativeLevelTwoOp()) {
         NbtComponent nbtComponent = (NbtComponent)itemStack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT);
         if (nbtComponent.contains("Book")) {
            bl = true;
         }
      }

      return (BlockState)((BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())).with(HAS_BOOK, bl);
   }

   protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return BASE_SHAPE;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)OUTLINE_SHAPES_BY_DIRECTION.get(state.get(FACING));
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, POWERED, HAS_BOOK);
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new LecternBlockEntity(pos, state);
   }

   public static boolean putBookIfAbsent(@Nullable LivingEntity user, World world, BlockPos pos, BlockState state, ItemStack stack) {
      if (!(Boolean)state.get(HAS_BOOK)) {
         if (!world.isClient) {
            putBook(user, world, pos, state, stack);
         }

         return true;
      } else {
         return false;
      }
   }

   private static void putBook(@Nullable LivingEntity user, World world, BlockPos pos, BlockState state, ItemStack stack) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof LecternBlockEntity lecternBlockEntity) {
         lecternBlockEntity.setBook(stack.splitUnlessCreative(1, user));
         setHasBook(user, world, pos, state, true);
         world.playSound((Entity)null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

   }

   public static void setHasBook(@Nullable Entity user, World world, BlockPos pos, BlockState state, boolean hasBook) {
      BlockState blockState = (BlockState)((BlockState)state.with(POWERED, false)).with(HAS_BOOK, hasBook);
      world.setBlockState(pos, blockState, 3);
      world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(user, blockState));
      updateNeighborAlways(world, pos, state);
   }

   public static void setPowered(World world, BlockPos pos, BlockState state) {
      setPowered(world, pos, state, true);
      world.scheduleBlockTick(pos, state.getBlock(), 2);
      world.syncWorldEvent(1043, pos, 0);
   }

   private static void setPowered(World world, BlockPos pos, BlockState state, boolean powered) {
      world.setBlockState(pos, (BlockState)state.with(POWERED, powered), 3);
      updateNeighborAlways(world, pos, state);
   }

   private static void updateNeighborAlways(World world, BlockPos pos, BlockState state) {
      WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(world, ((Direction)state.get(FACING)).getOpposite(), Direction.UP);
      world.updateNeighborsAlways(pos.down(), state.getBlock(), wireOrientation);
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      setPowered(world, pos, state, false);
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      if ((Boolean)state.get(POWERED)) {
         updateNeighborAlways(world, pos, state);
      }

   }

   protected boolean emitsRedstonePower(BlockState state) {
      return true;
   }

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Boolean)state.get(POWERED) ? 15 : 0;
   }

   protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return direction == Direction.UP && (Boolean)state.get(POWERED) ? 15 : 0;
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      if ((Boolean)state.get(HAS_BOOK)) {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         if (blockEntity instanceof LecternBlockEntity) {
            return ((LecternBlockEntity)blockEntity).getComparatorOutput();
         }
      }

      return 0;
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      if ((Boolean)state.get(HAS_BOOK)) {
         return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
      } else if (stack.isIn(ItemTags.LECTERN_BOOKS)) {
         return (ActionResult)(putBookIfAbsent(player, world, pos, state, stack) ? ActionResult.SUCCESS : ActionResult.PASS);
      } else {
         return (ActionResult)(stack.isEmpty() && hand == Hand.MAIN_HAND ? ActionResult.PASS : ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION);
      }
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if ((Boolean)state.get(HAS_BOOK)) {
         if (!world.isClient) {
            this.openScreen(world, pos, player);
         }

         return ActionResult.SUCCESS;
      } else {
         return ActionResult.CONSUME;
      }
   }

   @Nullable
   protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
      return !(Boolean)state.get(HAS_BOOK) ? null : super.createScreenHandlerFactory(state, world, pos);
   }

   private void openScreen(World world, BlockPos pos, PlayerEntity player) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof LecternBlockEntity) {
         player.openHandledScreen((LecternBlockEntity)blockEntity);
         player.incrementStat(Stats.INTERACT_WITH_LECTERN);
      }

   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      POWERED = Properties.POWERED;
      HAS_BOOK = Properties.HAS_BOOK;
      BASE_SHAPE = VoxelShapes.union(Block.createColumnShape(16.0, 0.0, 2.0), Block.createColumnShape(8.0, 2.0, 14.0));
      OUTLINE_SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap(VoxelShapes.union(Block.createCuboidZShape(16.0, 10.0, 14.0, 1.0, 5.333333), Block.createCuboidZShape(16.0, 12.0, 16.0, 5.333333, 9.666667), Block.createCuboidZShape(16.0, 14.0, 18.0, 9.666667, 14.0), BASE_SHAPE));
   }
}
