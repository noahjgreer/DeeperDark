/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CommandBlock
 *  net.minecraft.block.FacingBlock
 *  net.minecraft.block.OperatorBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.CommandBlockBlockEntity
 *  net.minecraft.block.entity.CommandBlockBlockEntity$Type
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.StringHelper
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.CommandBlockExecutor
 *  net.minecraft.world.World
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.rule.GameRules
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.OperatorBlock;
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
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
public class CommandBlock
extends BlockWithEntity
implements OperatorBlock {
    public static final MapCodec<CommandBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.fieldOf("automatic").forGetter(block -> block.auto), (App)CommandBlock.createSettingsCodec()).apply((Applicative)instance, CommandBlock::new));
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final EnumProperty<Direction> FACING = FacingBlock.FACING;
    public static final BooleanProperty CONDITIONAL = Properties.CONDITIONAL;
    private final boolean auto;

    public MapCodec<CommandBlock> getCodec() {
        return CODEC;
    }

    public CommandBlock(boolean auto, AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)CONDITIONAL, (Comparable)Boolean.valueOf(false)));
        this.auto = auto;
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        CommandBlockBlockEntity commandBlockBlockEntity = new CommandBlockBlockEntity(pos, state);
        commandBlockBlockEntity.setAuto(this.auto);
        return commandBlockBlockEntity;
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isClient()) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CommandBlockBlockEntity) {
            CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
            this.update(world, pos, commandBlockBlockEntity, world.isReceivingRedstonePower(pos));
        }
    }

    private void update(World world, BlockPos pos, CommandBlockBlockEntity blockEntity, boolean powered) {
        boolean bl = blockEntity.isPowered();
        if (powered == bl) {
            return;
        }
        blockEntity.setPowered(powered);
        if (powered) {
            if (blockEntity.isAuto() || blockEntity.getCommandBlockType() == CommandBlockBlockEntity.Type.SEQUENCE) {
                return;
            }
            blockEntity.updateConditionMet();
            world.scheduleBlockTick(pos, (Block)this, 1);
        }
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CommandBlockBlockEntity) {
            CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
            CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
            boolean bl = !StringHelper.isEmpty((String)commandBlockExecutor.getCommand());
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
                    world.scheduleBlockTick(pos, (Block)this, 1);
                }
            } else if (type == CommandBlockBlockEntity.Type.REDSTONE) {
                if (bl2) {
                    this.execute(state, world, pos, commandBlockExecutor, bl);
                } else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
                    commandBlockExecutor.setSuccessCount(0);
                }
            }
            world.updateComparators(pos, (Block)this);
        }
    }

    private void execute(BlockState state, ServerWorld world, BlockPos pos, CommandBlockExecutor executor, boolean hasCommand) {
        if (hasCommand) {
            executor.execute(world);
        } else {
            executor.setSuccessCount(0);
        }
        CommandBlock.executeCommandChain((ServerWorld)world, (BlockPos)pos, (Direction)((Direction)state.get((Property)FACING)));
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CommandBlockBlockEntity && player.isCreativeLevelTwoOp()) {
            player.openCommandBlockScreen((CommandBlockBlockEntity)blockEntity);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CommandBlockBlockEntity) {
            return ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount();
        }
        return 0;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof CommandBlockBlockEntity)) {
            return;
        }
        CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
        CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if (!itemStack.contains(DataComponentTypes.BLOCK_ENTITY_DATA)) {
                commandBlockExecutor.setTrackOutput(((Boolean)serverWorld.getGameRules().getValue(GameRules.SEND_COMMAND_FEEDBACK)).booleanValue());
                commandBlockBlockEntity.setAuto(this.auto);
            }
            boolean bl = world.isReceivingRedstonePower(pos);
            this.update(world, pos, commandBlockBlockEntity, bl);
        }
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, CONDITIONAL});
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getPlayerLookDirection().getOpposite());
    }

    private static void executeCommandChain(ServerWorld world, BlockPos pos, Direction facing) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        GameRules gameRules = world.getGameRules();
        int i = (Integer)gameRules.getValue(GameRules.MAX_COMMAND_SEQUENCE_LENGTH);
        while (i-- > 0) {
            CommandBlockBlockEntity commandBlockBlockEntity;
            BlockEntity blockEntity;
            mutable.move(facing);
            BlockState blockState = world.getBlockState((BlockPos)mutable);
            Block block = blockState.getBlock();
            if (!blockState.isOf(Blocks.CHAIN_COMMAND_BLOCK) || !((blockEntity = world.getBlockEntity((BlockPos)mutable)) instanceof CommandBlockBlockEntity) || (commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity).getCommandBlockType() != CommandBlockBlockEntity.Type.SEQUENCE) break;
            if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
                CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
                if (commandBlockBlockEntity.updateConditionMet()) {
                    if (!commandBlockExecutor.execute(world)) break;
                    world.updateComparators((BlockPos)mutable, block);
                } else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
                    commandBlockExecutor.setSuccessCount(0);
                }
            }
            facing = (Direction)blockState.get((Property)FACING);
        }
        if (i <= 0) {
            int j = Math.max((Integer)gameRules.getValue(GameRules.MAX_COMMAND_SEQUENCE_LENGTH), 0);
            LOGGER.warn("Command Block chain tried to execute more than {} steps!", (Object)j);
        }
    }
}

