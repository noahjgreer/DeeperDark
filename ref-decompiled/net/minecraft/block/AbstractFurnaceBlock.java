package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFurnaceBlock extends BlockWithEntity {
   public static final EnumProperty FACING;
   public static final BooleanProperty LIT;

   protected AbstractFurnaceBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(LIT, false));
   }

   protected abstract MapCodec getCodec();

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!world.isClient) {
         this.openScreen(world, pos, player);
      }

      return ActionResult.SUCCESS;
   }

   protected abstract void openScreen(World world, BlockPos pos, PlayerEntity player);

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return (BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
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
      builder.add(FACING, LIT);
   }

   @Nullable
   protected static BlockEntityTicker validateTicker(World world, BlockEntityType givenType, BlockEntityType expectedType) {
      BlockEntityTicker var10000;
      if (world instanceof ServerWorld serverWorld) {
         var10000 = validateTicker(givenType, expectedType, (worldx, pos, state, blockEntity) -> {
            AbstractFurnaceBlockEntity.tick(serverWorld, pos, state, blockEntity);
         });
      } else {
         var10000 = null;
      }

      return var10000;
   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      LIT = Properties.LIT;
   }
}
