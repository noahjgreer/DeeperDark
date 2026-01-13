/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.JukeboxBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.JukeboxBlockEntity
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.JukeboxPlayableComponent
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.TypedEntityData
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class JukeboxBlock
extends BlockWithEntity {
    public static final MapCodec<JukeboxBlock> CODEC = JukeboxBlock.createCodec(JukeboxBlock::new);
    public static final BooleanProperty HAS_RECORD = Properties.HAS_RECORD;

    public MapCodec<JukeboxBlock> getCodec() {
        return CODEC;
    }

    public JukeboxBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)HAS_RECORD, (Comparable)Boolean.valueOf(false)));
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        TypedEntityData typedEntityData = (TypedEntityData)itemStack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        if (typedEntityData != null && typedEntityData.contains("RecordItem")) {
            world.setBlockState(pos, (BlockState)state.with((Property)HAS_RECORD, (Comparable)Boolean.valueOf(true)), 2);
        }
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity;
        if (((Boolean)state.get((Property)HAS_RECORD)).booleanValue() && (blockEntity = world.getBlockEntity(pos)) instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity)blockEntity;
            jukeboxBlockEntity.dropRecord();
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (((Boolean)state.get((Property)HAS_RECORD)).booleanValue()) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        ItemStack itemStack = player.getStackInHand(hand);
        ActionResult actionResult = JukeboxPlayableComponent.tryPlayStack((World)world, (BlockPos)pos, (ItemStack)itemStack, (PlayerEntity)player);
        if (!actionResult.isAccepted()) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        return actionResult;
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced((BlockState)state, (World)world, (BlockPos)pos);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new JukeboxBlockEntity(pos, state);
    }

    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        JukeboxBlockEntity jukeboxBlockEntity;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof JukeboxBlockEntity && (jukeboxBlockEntity = (JukeboxBlockEntity)blockEntity).getManager().isPlaying()) {
            return 15;
        }
        return 0;
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity)blockEntity;
            return jukeboxBlockEntity.getComparatorOutput();
        }
        return 0;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HAS_RECORD});
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (((Boolean)state.get((Property)HAS_RECORD)).booleanValue()) {
            return JukeboxBlock.validateTicker(type, (BlockEntityType)BlockEntityType.JUKEBOX, JukeboxBlockEntity::tick);
        }
        return null;
    }
}

