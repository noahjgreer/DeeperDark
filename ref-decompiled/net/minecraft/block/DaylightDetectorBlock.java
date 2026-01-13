/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.DaylightDetectorBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.DaylightDetectorBlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.LightType
 *  net.minecraft.world.World
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.event.GameEvent;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class DaylightDetectorBlock
extends BlockWithEntity {
    public static final MapCodec<DaylightDetectorBlock> CODEC = DaylightDetectorBlock.createCodec(DaylightDetectorBlock::new);
    public static final IntProperty POWER = Properties.POWER;
    public static final BooleanProperty INVERTED = Properties.INVERTED;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)6.0);

    public MapCodec<DaylightDetectorBlock> getCodec() {
        return CODEC;
    }

    public DaylightDetectorBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)POWER, (Comparable)Integer.valueOf(0))).with((Property)INVERTED, (Comparable)Boolean.valueOf(false)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (Integer)state.get((Property)POWER);
    }

    private static void updateState(BlockState state, World world, BlockPos pos) {
        int i = world.getLightLevel(LightType.SKY, pos) - world.getAmbientDarkness();
        float f = ((Float)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.SUN_ANGLE_VISUAL, pos)).floatValue() * ((float)Math.PI / 180);
        boolean bl = (Boolean)state.get((Property)INVERTED);
        if (bl) {
            i = 15 - i;
        } else if (i > 0) {
            float g = f < (float)Math.PI ? 0.0f : (float)Math.PI * 2;
            f += (g - f) * 0.2f;
            i = Math.round((float)i * MathHelper.cos((double)f));
        }
        i = MathHelper.clamp((int)i, (int)0, (int)15);
        if ((Integer)state.get((Property)POWER) != i) {
            world.setBlockState(pos, (BlockState)state.with((Property)POWER, (Comparable)Integer.valueOf(i)), 3);
        }
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.canModifyBlocks()) {
            return super.onUse(state, world, pos, player, hit);
        }
        if (!world.isClient()) {
            BlockState blockState = (BlockState)state.cycle((Property)INVERTED);
            world.setBlockState(pos, blockState, 2);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((Entity)player, (BlockState)blockState));
            DaylightDetectorBlock.updateState((BlockState)blockState, (World)world, (BlockPos)pos);
        }
        return ActionResult.SUCCESS;
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DaylightDetectorBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient() && world.getDimension().hasSkyLight()) {
            return DaylightDetectorBlock.validateTicker(type, (BlockEntityType)BlockEntityType.DAYLIGHT_DETECTOR, DaylightDetectorBlock::tick);
        }
        return null;
    }

    private static void tick(World world, BlockPos pos, BlockState state, DaylightDetectorBlockEntity blockEntity) {
        if (world.getTime() % 20L == 0L) {
            DaylightDetectorBlock.updateState((BlockState)state, (World)world, (BlockPos)pos);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWER, INVERTED});
    }
}

