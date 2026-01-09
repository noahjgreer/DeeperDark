package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.HangingSignItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class HangingSignBlock extends AbstractSignBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(WoodType.CODEC.fieldOf("wood_type").forGetter(AbstractSignBlock::getWoodType), createSettingsCodec()).apply(instance, HangingSignBlock::new);
   });
   public static final IntProperty ROTATION;
   public static final BooleanProperty ATTACHED;
   private static final VoxelShape DEFAULT_SHAPE;
   private static final Map SHAPES_BY_ROTATION;

   public MapCodec getCodec() {
      return CODEC;
   }

   public HangingSignBlock(WoodType woodType, AbstractBlock.Settings settings) {
      super(woodType, settings.sounds(woodType.hangingSignSoundType()));
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(ROTATION, 0)).with(ATTACHED, false)).with(WATERLOGGED, false));
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      BlockEntity var9 = world.getBlockEntity(pos);
      if (var9 instanceof SignBlockEntity signBlockEntity) {
         if (this.shouldTryAttaching(player, hit, signBlockEntity, stack)) {
            return ActionResult.PASS;
         }
      }

      return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
   }

   private boolean shouldTryAttaching(PlayerEntity player, BlockHitResult hitResult, SignBlockEntity sign, ItemStack stack) {
      return !sign.canRunCommandClickEvent(sign.isPlayerFacingFront(player), player) && stack.getItem() instanceof HangingSignItem && hitResult.getSide().equals(Direction.DOWN);
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return world.getBlockState(pos.up()).isSideSolid(world, pos.up(), Direction.DOWN, SideShapeType.CENTER);
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      World world = ctx.getWorld();
      FluidState fluidState = world.getFluidState(ctx.getBlockPos());
      BlockPos blockPos = ctx.getBlockPos().up();
      BlockState blockState = world.getBlockState(blockPos);
      boolean bl = blockState.isIn(BlockTags.ALL_HANGING_SIGNS);
      Direction direction = Direction.fromHorizontalDegrees((double)ctx.getPlayerYaw());
      boolean bl2 = !Block.isFaceFullSquare(blockState.getCollisionShape(world, blockPos), Direction.DOWN) || ctx.shouldCancelInteraction();
      if (bl && !ctx.shouldCancelInteraction()) {
         if (blockState.contains(WallHangingSignBlock.FACING)) {
            Direction direction2 = (Direction)blockState.get(WallHangingSignBlock.FACING);
            if (direction2.getAxis().test(direction)) {
               bl2 = false;
            }
         } else if (blockState.contains(ROTATION)) {
            Optional optional = RotationPropertyHelper.toDirection((Integer)blockState.get(ROTATION));
            if (optional.isPresent() && ((Direction)optional.get()).getAxis().test(direction)) {
               bl2 = false;
            }
         }
      }

      int i = !bl2 ? RotationPropertyHelper.fromDirection(direction.getOpposite()) : RotationPropertyHelper.fromYaw(ctx.getPlayerYaw() + 180.0F);
      return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(ATTACHED, bl2)).with(ROTATION, i)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)SHAPES_BY_ROTATION.getOrDefault(state.get(ROTATION), DEFAULT_SHAPE);
   }

   protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
      return this.getOutlineShape(state, world, pos, ShapeContext.absent());
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return direction == Direction.UP && !this.canPlaceAt(state, world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   public float getRotationDegrees(BlockState state) {
      return RotationPropertyHelper.toDegrees((Integer)state.get(ROTATION));
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(ROTATION, rotation.rotate((Integer)state.get(ROTATION), 16));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return (BlockState)state.with(ROTATION, mirror.mirror((Integer)state.get(ROTATION), 16));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(ROTATION, ATTACHED, WATERLOGGED);
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new HangingSignBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return validateTicker(type, BlockEntityType.HANGING_SIGN, SignBlockEntity::tick);
   }

   static {
      ROTATION = Properties.ROTATION;
      ATTACHED = Properties.ATTACHED;
      DEFAULT_SHAPE = Block.createColumnShape(10.0, 0.0, 16.0);
      SHAPES_BY_ROTATION = (Map)VoxelShapes.createHorizontalFacingShapeMap(Block.createColumnShape(14.0, 2.0, 0.0, 10.0)).entrySet().stream().collect(Collectors.toMap((entry) -> {
         return RotationPropertyHelper.fromDirection((Direction)entry.getKey());
      }, Map.Entry::getValue));
   }
}
