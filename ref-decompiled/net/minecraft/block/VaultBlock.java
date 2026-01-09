package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.enums.VaultState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VaultBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(VaultBlock::new);
   public static final Property VAULT_STATE;
   public static final EnumProperty FACING;
   public static final BooleanProperty OMINOUS;

   public MapCodec getCodec() {
      return CODEC;
   }

   public VaultBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(VAULT_STATE, VaultState.INACTIVE)).with(OMINOUS, false));
   }

   public ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      if (!stack.isEmpty() && state.get(VAULT_STATE) == VaultState.ACTIVE) {
         if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            BlockEntity var10 = serverWorld.getBlockEntity(pos);
            if (!(var10 instanceof VaultBlockEntity)) {
               return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }

            VaultBlockEntity vaultBlockEntity = (VaultBlockEntity)var10;
            VaultBlockEntity.Server.tryUnlock(serverWorld, pos, state, vaultBlockEntity.getConfig(), vaultBlockEntity.getServerData(), vaultBlockEntity.getSharedData(), player, stack);
         }

         return ActionResult.SUCCESS_SERVER;
      } else {
         return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
      }
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new VaultBlockEntity(pos, state);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, VAULT_STATE, OMINOUS);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      BlockEntityTicker var10000;
      if (world instanceof ServerWorld serverWorld) {
         var10000 = validateTicker(type, BlockEntityType.VAULT, (worldx, pos, statex, blockEntity) -> {
            VaultBlockEntity.Server.tick(serverWorld, pos, statex, blockEntity.getConfig(), blockEntity.getServerData(), blockEntity.getSharedData());
         });
      } else {
         var10000 = validateTicker(type, BlockEntityType.VAULT, (worldx, pos, statex, blockEntity) -> {
            VaultBlockEntity.Client.tick(worldx, pos, statex, blockEntity.getClientData(), blockEntity.getSharedData());
         });
      }

      return var10000;
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return (BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
   }

   public BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   public BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   static {
      VAULT_STATE = Properties.VAULT_STATE;
      FACING = HorizontalFacingBlock.FACING;
      OMINOUS = Properties.OMINOUS;
   }
}
