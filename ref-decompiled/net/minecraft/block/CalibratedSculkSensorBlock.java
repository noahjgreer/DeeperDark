/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.CalibratedSculkSensorBlock
 *  net.minecraft.block.SculkSensorBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.CalibratedSculkSensorBlockEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.event.Vibrations$Callback
 *  net.minecraft.world.event.Vibrations$ListenerData
 *  net.minecraft.world.event.Vibrations$Ticker
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.Vibrations;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class CalibratedSculkSensorBlock
extends SculkSensorBlock {
    public static final MapCodec<CalibratedSculkSensorBlock> CODEC = CalibratedSculkSensorBlock.createCodec(CalibratedSculkSensorBlock::new);
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public MapCodec<CalibratedSculkSensorBlock> getCodec() {
        return CODEC;
    }

    public CalibratedSculkSensorBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)Direction.NORTH));
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CalibratedSculkSensorBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient()) {
            return CalibratedSculkSensorBlock.validateTicker(type, (BlockEntityType)BlockEntityType.CALIBRATED_SCULK_SENSOR, (worldx, pos, statex, blockEntity) -> Vibrations.Ticker.tick((World)worldx, (Vibrations.ListenerData)blockEntity.getVibrationListenerData(), (Vibrations.Callback)blockEntity.getVibrationCallback()));
        }
        return null;
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)super.getPlacementState(ctx).with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing());
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (direction != state.get((Property)FACING)) {
            return super.getWeakRedstonePower(state, world, pos, direction);
        }
        return 0;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(new Property[]{FACING});
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    public int getCooldownTime() {
        return 10;
    }
}

