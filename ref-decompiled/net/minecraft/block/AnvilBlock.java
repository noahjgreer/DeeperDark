package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AnvilBlock extends FallingBlock {
   public static final MapCodec CODEC = createCodec(AnvilBlock::new);
   public static final EnumProperty FACING;
   private static final Map SHAPES_BY_AXIS;
   private static final Text TITLE;
   private static final float FALLING_BLOCK_ENTITY_DAMAGE_MULTIPLIER = 2.0F;
   private static final int FALLING_BLOCK_ENTITY_MAX_DAMAGE = 40;

   public MapCodec getCodec() {
      return CODEC;
   }

   public AnvilBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return (BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().rotateYClockwise());
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!world.isClient) {
         player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
         player.incrementStat(Stats.INTERACT_WITH_ANVIL);
      }

      return ActionResult.SUCCESS;
   }

   @Nullable
   protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
      return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
         return new AnvilScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
      }, TITLE);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)SHAPES_BY_AXIS.get(((Direction)state.get(FACING)).getAxis());
   }

   protected void configureFallingBlockEntity(FallingBlockEntity entity) {
      entity.setHurtEntities(2.0F, 40);
   }

   public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
      if (!fallingBlockEntity.isSilent()) {
         world.syncWorldEvent(1031, pos, 0);
      }

   }

   public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
      if (!fallingBlockEntity.isSilent()) {
         world.syncWorldEvent(1029, pos, 0);
      }

   }

   public DamageSource getDamageSource(Entity attacker) {
      return attacker.getDamageSources().fallingAnvil(attacker);
   }

   @Nullable
   public static BlockState getLandingState(BlockState fallingState) {
      if (fallingState.isOf(Blocks.ANVIL)) {
         return (BlockState)Blocks.CHIPPED_ANVIL.getDefaultState().with(FACING, (Direction)fallingState.get(FACING));
      } else {
         return fallingState.isOf(Blocks.CHIPPED_ANVIL) ? (BlockState)Blocks.DAMAGED_ANVIL.getDefaultState().with(FACING, (Direction)fallingState.get(FACING)) : null;
      }
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   public int getColor(BlockState state, BlockView world, BlockPos pos) {
      return state.getMapColor(world, pos).color;
   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      SHAPES_BY_AXIS = VoxelShapes.createHorizontalAxisShapeMap(VoxelShapes.union(Block.createColumnShape(12.0, 0.0, 4.0), Block.createColumnShape(8.0, 10.0, 4.0, 5.0), Block.createColumnShape(4.0, 8.0, 5.0, 10.0), Block.createColumnShape(10.0, 16.0, 10.0, 16.0)));
      TITLE = Text.translatable("container.repair");
   }
}
