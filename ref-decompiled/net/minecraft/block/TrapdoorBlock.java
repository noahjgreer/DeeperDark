package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class TrapdoorBlock extends HorizontalFacingBlock implements Waterloggable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter((block) -> {
         return block.blockSetType;
      }), createSettingsCodec()).apply(instance, TrapdoorBlock::new);
   });
   public static final BooleanProperty OPEN;
   public static final EnumProperty HALF;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty WATERLOGGED;
   private static final Map shapeByDirection;
   private final BlockSetType blockSetType;

   public MapCodec getCodec() {
      return CODEC;
   }

   public TrapdoorBlock(BlockSetType type, AbstractBlock.Settings settings) {
      super(settings.sounds(type.soundType()));
      this.blockSetType = type;
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(OPEN, false)).with(HALF, BlockHalf.BOTTOM)).with(POWERED, false)).with(WATERLOGGED, false));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)shapeByDirection.get((Boolean)state.get(OPEN) ? state.get(FACING) : (state.get(HALF) == BlockHalf.TOP ? Direction.DOWN : Direction.UP));
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      switch (type) {
         case LAND:
            return (Boolean)state.get(OPEN);
         case WATER:
            return (Boolean)state.get(WATERLOGGED);
         case AIR:
            return (Boolean)state.get(OPEN);
         default:
            return false;
      }
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!this.blockSetType.canOpenByHand()) {
         return ActionResult.PASS;
      } else {
         this.flip(state, world, pos, player);
         return ActionResult.SUCCESS;
      }
   }

   protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer stackMerger) {
      if (explosion.canTriggerBlocks() && this.blockSetType.canOpenByWindCharge() && !(Boolean)state.get(POWERED)) {
         this.flip(state, world, pos, (PlayerEntity)null);
      }

      super.onExploded(state, world, pos, explosion, stackMerger);
   }

   private void flip(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
      BlockState blockState = (BlockState)state.cycle(OPEN);
      world.setBlockState(pos, blockState, 2);
      if ((Boolean)blockState.get(WATERLOGGED)) {
         world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      this.playToggleSound(player, world, pos, (Boolean)blockState.get(OPEN));
   }

   protected void playToggleSound(@Nullable PlayerEntity player, World world, BlockPos pos, boolean open) {
      world.playSound(player, pos, open ? this.blockSetType.trapdoorOpen() : this.blockSetType.trapdoorClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
      world.emitGameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      if (!world.isClient) {
         boolean bl = world.isReceivingRedstonePower(pos);
         if (bl != (Boolean)state.get(POWERED)) {
            if ((Boolean)state.get(OPEN) != bl) {
               state = (BlockState)state.with(OPEN, bl);
               this.playToggleSound((PlayerEntity)null, world, pos, bl);
            }

            world.setBlockState(pos, (BlockState)state.with(POWERED, bl), 2);
            if ((Boolean)state.get(WATERLOGGED)) {
               world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            }
         }

      }
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState blockState = this.getDefaultState();
      FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
      Direction direction = ctx.getSide();
      if (!ctx.canReplaceExisting() && direction.getAxis().isHorizontal()) {
         blockState = (BlockState)((BlockState)blockState.with(FACING, direction)).with(HALF, ctx.getHitPos().y - (double)ctx.getBlockPos().getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM);
      } else {
         blockState = (BlockState)((BlockState)blockState.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())).with(HALF, direction == Direction.UP ? BlockHalf.BOTTOM : BlockHalf.TOP);
      }

      if (ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())) {
         blockState = (BlockState)((BlockState)blockState.with(OPEN, true)).with(POWERED, true);
      }

      return (BlockState)blockState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
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

   protected BlockSetType getBlockSetType() {
      return this.blockSetType;
   }

   static {
      OPEN = Properties.OPEN;
      HALF = Properties.BLOCK_HALF;
      POWERED = Properties.POWERED;
      WATERLOGGED = Properties.WATERLOGGED;
      shapeByDirection = VoxelShapes.createFacingShapeMap(Block.createCuboidZShape(16.0, 13.0, 16.0));
   }
}
