package net.minecraft.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CommandBlock extends BlockWithEntity implements OperatorBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.BOOL.fieldOf("automatic").forGetter((block) -> {
         return block.auto;
      }), createSettingsCodec()).apply(instance, CommandBlock::new);
   });
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final EnumProperty FACING;
   public static final BooleanProperty CONDITIONAL;
   private final boolean auto;

   public MapCodec getCodec() {
      return CODEC;
   }

   public CommandBlock(boolean auto, AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(CONDITIONAL, false));
      this.auto = auto;
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      CommandBlockBlockEntity commandBlockBlockEntity = new CommandBlockBlockEntity(pos, state);
      commandBlockBlockEntity.setAuto(this.auto);
      return commandBlockBlockEntity;
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      if (!world.isClient) {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         if (blockEntity instanceof CommandBlockBlockEntity) {
            CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
            this.update(world, pos, commandBlockBlockEntity, world.isReceivingRedstonePower(pos));
         }

      }
   }

   private void update(World world, BlockPos pos, CommandBlockBlockEntity blockEntity, boolean powered) {
      boolean bl = blockEntity.isPowered();
      if (powered != bl) {
         blockEntity.setPowered(powered);
         if (powered) {
            if (blockEntity.isAuto() || blockEntity.getCommandBlockType() == CommandBlockBlockEntity.Type.SEQUENCE) {
               return;
            }

            blockEntity.updateConditionMet();
            world.scheduleBlockTick(pos, this, 1);
         }

      }
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof CommandBlockBlockEntity commandBlockBlockEntity) {
         CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
         boolean bl = !StringHelper.isEmpty(commandBlockExecutor.getCommand());
         CommandBlockBlockEntity.Type type = commandBlockBlockEntity.getCommandBlockType();
         boolean bl2 = commandBlockBlockEntity.isConditionMet();
         if (type == CommandBlockBlockEntity.Type.AUTO) {
            commandBlockBlockEntity.updateConditionMet();
            if (bl2) {
               this.execute(state, world, pos, commandBlockExecutor, bl);
            } else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
               commandBlockExecutor.setSuccessCount(0);
            }

            if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
               world.scheduleBlockTick(pos, this, 1);
            }
         } else if (type == CommandBlockBlockEntity.Type.REDSTONE) {
            if (bl2) {
               this.execute(state, world, pos, commandBlockExecutor, bl);
            } else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
               commandBlockExecutor.setSuccessCount(0);
            }
         }

         world.updateComparators(pos, this);
      }

   }

   private void execute(BlockState state, ServerWorld world, BlockPos pos, CommandBlockExecutor executor, boolean hasCommand) {
      if (hasCommand) {
         executor.execute(world);
      } else {
         executor.setSuccessCount(0);
      }

      executeCommandChain(world, pos, (Direction)state.get(FACING));
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof CommandBlockBlockEntity && player.isCreativeLevelTwoOp()) {
         player.openCommandBlockScreen((CommandBlockBlockEntity)blockEntity);
         return ActionResult.SUCCESS;
      } else {
         return ActionResult.PASS;
      }
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      return blockEntity instanceof CommandBlockBlockEntity ? ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount() : 0;
   }

   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof CommandBlockBlockEntity commandBlockBlockEntity) {
         CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
         if (world instanceof ServerWorld serverWorld) {
            if (!itemStack.contains(DataComponentTypes.BLOCK_ENTITY_DATA)) {
               commandBlockExecutor.setTrackOutput(serverWorld.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK));
               commandBlockBlockEntity.setAuto(this.auto);
            }

            boolean bl = world.isReceivingRedstonePower(pos);
            this.update(world, pos, commandBlockBlockEntity, bl);
         }

      }
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, CONDITIONAL);
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
   }

   private static void executeCommandChain(ServerWorld world, BlockPos pos, Direction facing) {
      BlockPos.Mutable mutable = pos.mutableCopy();
      GameRules gameRules = world.getGameRules();

      int i;
      BlockState blockState;
      for(i = gameRules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH); i-- > 0; facing = (Direction)blockState.get(FACING)) {
         mutable.move(facing);
         blockState = world.getBlockState(mutable);
         Block block = blockState.getBlock();
         if (!blockState.isOf(Blocks.CHAIN_COMMAND_BLOCK)) {
            break;
         }

         BlockEntity blockEntity = world.getBlockEntity(mutable);
         if (!(blockEntity instanceof CommandBlockBlockEntity)) {
            break;
         }

         CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
         if (commandBlockBlockEntity.getCommandBlockType() != CommandBlockBlockEntity.Type.SEQUENCE) {
            break;
         }

         if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
            CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
            if (commandBlockBlockEntity.updateConditionMet()) {
               if (!commandBlockExecutor.execute(world)) {
                  break;
               }

               world.updateComparators(mutable, block);
            } else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
               commandBlockExecutor.setSuccessCount(0);
            }
         }
      }

      if (i <= 0) {
         int j = Math.max(gameRules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH), 0);
         LOGGER.warn("Command Block chain tried to execute more than {} steps!", j);
      }

   }

   static {
      FACING = FacingBlock.FACING;
      CONDITIONAL = Properties.CONDITIONAL;
   }
}
