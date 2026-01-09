package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignChangingItem;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.PlainTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSignBlock extends BlockWithEntity implements Waterloggable {
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape SHAPE;
   private final WoodType type;

   protected AbstractSignBlock(WoodType type, AbstractBlock.Settings settings) {
      super(settings);
      this.type = type;
   }

   protected abstract MapCodec getCodec();

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if ((Boolean)state.get(WATERLOGGED)) {
         tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   public boolean canMobSpawnInside(BlockState state) {
      return true;
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new SignBlockEntity(pos, state);
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      BlockEntity var9 = world.getBlockEntity(pos);
      if (var9 instanceof SignBlockEntity signBlockEntity) {
         Item var11 = stack.getItem();
         SignChangingItem var10000;
         if (var11 instanceof SignChangingItem signChangingItem) {
            var10000 = signChangingItem;
         } else {
            var10000 = null;
         }

         SignChangingItem signChangingItem2 = var10000;
         boolean bl = signChangingItem2 != null && player.canModifyBlocks();
         if (world instanceof ServerWorld serverWorld) {
            if (bl && !signBlockEntity.isWaxed() && !this.isOtherPlayerEditing(player, signBlockEntity)) {
               boolean bl2 = signBlockEntity.isPlayerFacingFront(player);
               if (signChangingItem2.canUseOnSignText(signBlockEntity.getText(bl2), player) && signChangingItem2.useOnSign(serverWorld, signBlockEntity, bl2, player)) {
                  signBlockEntity.runCommandClickEvent(serverWorld, player, pos, bl2);
                  player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                  serverWorld.emitGameEvent(GameEvent.BLOCK_CHANGE, signBlockEntity.getPos(), GameEvent.Emitter.of(player, signBlockEntity.getCachedState()));
                  stack.decrementUnlessCreative(1, player);
                  return ActionResult.SUCCESS;
               } else {
                  return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
               }
            } else {
               return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
         } else {
            return !bl && !signBlockEntity.isWaxed() ? ActionResult.CONSUME : ActionResult.SUCCESS;
         }
      } else {
         return ActionResult.PASS;
      }
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      BlockEntity var7 = world.getBlockEntity(pos);
      if (var7 instanceof SignBlockEntity signBlockEntity) {
         if (world instanceof ServerWorld serverWorld) {
            boolean bl = signBlockEntity.isPlayerFacingFront(player);
            boolean bl2 = signBlockEntity.runCommandClickEvent(serverWorld, player, pos, bl);
            if (signBlockEntity.isWaxed()) {
               serverWorld.playSound((Entity)null, signBlockEntity.getPos(), signBlockEntity.getInteractionFailSound(), SoundCategory.BLOCKS);
               return ActionResult.SUCCESS_SERVER;
            } else if (bl2) {
               return ActionResult.SUCCESS_SERVER;
            } else if (!this.isOtherPlayerEditing(player, signBlockEntity) && player.canModifyBlocks() && this.isTextLiteralOrEmpty(player, signBlockEntity, bl)) {
               this.openEditScreen(player, signBlockEntity, bl);
               return ActionResult.SUCCESS_SERVER;
            } else {
               return ActionResult.PASS;
            }
         } else {
            Util.getFatalOrPause(new IllegalStateException("Expected to only call this on server"));
            return ActionResult.CONSUME;
         }
      } else {
         return ActionResult.PASS;
      }
   }

   private boolean isTextLiteralOrEmpty(PlayerEntity player, SignBlockEntity blockEntity, boolean front) {
      SignText signText = blockEntity.getText(front);
      return Arrays.stream(signText.getMessages(player.shouldFilterText())).allMatch((message) -> {
         return message.equals(ScreenTexts.EMPTY) || message.getContent() instanceof PlainTextContent;
      });
   }

   public abstract float getRotationDegrees(BlockState state);

   public Vec3d getCenter(BlockState state) {
      return new Vec3d(0.5, 0.5, 0.5);
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   public WoodType getWoodType() {
      return this.type;
   }

   public static WoodType getWoodType(Block block) {
      WoodType woodType;
      if (block instanceof AbstractSignBlock) {
         woodType = ((AbstractSignBlock)block).getWoodType();
      } else {
         woodType = WoodType.OAK;
      }

      return woodType;
   }

   public void openEditScreen(PlayerEntity player, SignBlockEntity blockEntity, boolean front) {
      blockEntity.setEditor(player.getUuid());
      player.openEditSignScreen(blockEntity, front);
   }

   private boolean isOtherPlayerEditing(PlayerEntity player, SignBlockEntity blockEntity) {
      UUID uUID = blockEntity.getEditor();
      return uUID != null && !uUID.equals(player.getUuid());
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return validateTicker(type, BlockEntityType.SIGN, SignBlockEntity::tick);
   }

   static {
      WATERLOGGED = Properties.WATERLOGGED;
      SHAPE = Block.createColumnShape(8.0, 0.0, 16.0);
   }
}
