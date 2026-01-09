package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class HopperBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(HopperBlock::new);
   public static final EnumProperty FACING;
   public static final BooleanProperty ENABLED;
   private final Function shapeFunction;
   private final Map shapesByDirection;

   public MapCodec getCodec() {
      return CODEC;
   }

   public HopperBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.DOWN)).with(ENABLED, true));
      VoxelShape voxelShape = Block.createColumnShape(12.0, 11.0, 16.0);
      this.shapeFunction = this.createShapeFunction(voxelShape);
      this.shapesByDirection = ImmutableMap.builderWithExpectedSize(5).putAll(VoxelShapes.createHorizontalFacingShapeMap(VoxelShapes.union(voxelShape, Block.createCuboidZShape(4.0, 8.0, 10.0, 0.0, 4.0)))).put(Direction.DOWN, voxelShape).build();
   }

   private Function createShapeFunction(VoxelShape shape) {
      VoxelShape voxelShape = VoxelShapes.union(Block.createColumnShape(16.0, 10.0, 16.0), Block.createColumnShape(8.0, 4.0, 10.0));
      VoxelShape voxelShape2 = VoxelShapes.combineAndSimplify(voxelShape, shape, BooleanBiFunction.ONLY_FIRST);
      Map map = VoxelShapes.createFacingShapeMap(Block.createCuboidZShape(4.0, 4.0, 8.0, 0.0, 8.0), (new Vec3d(8.0, 6.0, 8.0)).multiply(0.0625));
      return this.createShapeFunction((state) -> {
         return VoxelShapes.union(voxelShape2, VoxelShapes.combineAndSimplify((VoxelShape)map.get(state.get(FACING)), VoxelShapes.fullCube(), BooleanBiFunction.AND));
      }, new Property[]{ENABLED});
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)this.shapeFunction.apply(state);
   }

   protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
      return (VoxelShape)this.shapesByDirection.get(state.get(FACING));
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      Direction direction = ctx.getSide().getOpposite();
      return (BlockState)((BlockState)this.getDefaultState().with(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction)).with(ENABLED, true);
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new HopperBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return world.isClient ? null : validateTicker(type, BlockEntityType.HOPPER, HopperBlockEntity::serverTick);
   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      if (!oldState.isOf(state.getBlock())) {
         this.updateEnabled(world, pos, state);
      }
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!world.isClient) {
         BlockEntity var7 = world.getBlockEntity(pos);
         if (var7 instanceof HopperBlockEntity) {
            HopperBlockEntity hopperBlockEntity = (HopperBlockEntity)var7;
            player.openHandledScreen(hopperBlockEntity);
            player.incrementStat(Stats.INSPECT_HOPPER);
         }
      }

      return ActionResult.SUCCESS;
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      this.updateEnabled(world, pos, state);
   }

   private void updateEnabled(World world, BlockPos pos, BlockState state) {
      boolean bl = !world.isReceivingRedstonePower(pos);
      if (bl != (Boolean)state.get(ENABLED)) {
         world.setBlockState(pos, (BlockState)state.with(ENABLED, bl), 2);
      }

   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      ItemScatterer.onStateReplaced(state, world, pos);
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, ENABLED);
   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof HopperBlockEntity) {
         HopperBlockEntity.onEntityCollided(world, pos, state, entity, (HopperBlockEntity)blockEntity);
      }

   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   static {
      FACING = Properties.HOPPER_FACING;
      ENABLED = Properties.ENABLED;
   }
}
