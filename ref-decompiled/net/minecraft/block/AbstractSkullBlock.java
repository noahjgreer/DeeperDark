/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractSkullBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.SkullBlock$SkullType
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.SkullBlockEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.block.WireOrientation
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public abstract class AbstractSkullBlock
extends BlockWithEntity {
    public static final BooleanProperty POWERED = Properties.POWERED;
    private final SkullBlock.SkullType type;

    public AbstractSkullBlock(SkullBlock.SkullType type, AbstractBlock.Settings settings) {
        super(settings);
        this.type = type;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected abstract MapCodec<? extends AbstractSkullBlock> getCodec();

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SkullBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            boolean bl;
            boolean bl2 = bl = state.isOf(Blocks.DRAGON_HEAD) || state.isOf(Blocks.DRAGON_WALL_HEAD) || state.isOf(Blocks.PIGLIN_HEAD) || state.isOf(Blocks.PIGLIN_WALL_HEAD);
            if (bl) {
                return AbstractSkullBlock.validateTicker(type, (BlockEntityType)BlockEntityType.SKULL, SkullBlockEntity::tick);
            }
        }
        return null;
    }

    public SkullBlock.SkullType getSkullType() {
        return this.type;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED});
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)POWERED, (Comparable)Boolean.valueOf(ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())));
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isClient()) {
            return;
        }
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != (Boolean)state.get((Property)POWERED)) {
            world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(bl)), 2);
        }
    }
}

