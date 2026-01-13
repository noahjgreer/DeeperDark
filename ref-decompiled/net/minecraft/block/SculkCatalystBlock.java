/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.SculkCatalystBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.SculkCatalystBlockEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.intprovider.ConstantIntProvider
 *  net.minecraft.util.math.intprovider.IntProvider
 *  net.minecraft.util.math.random.Random
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
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class SculkCatalystBlock
extends BlockWithEntity {
    public static final MapCodec<SculkCatalystBlock> CODEC = SculkCatalystBlock.createCodec(SculkCatalystBlock::new);
    public static final BooleanProperty BLOOM = Properties.BLOOM;
    private final IntProvider experience = ConstantIntProvider.create((int)5);

    public MapCodec<SculkCatalystBlock> getCodec() {
        return CODEC;
    }

    public SculkCatalystBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)BLOOM, (Comparable)Boolean.valueOf(false)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{BLOOM});
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (((Boolean)state.get((Property)BLOOM)).booleanValue()) {
            world.setBlockState(pos, (BlockState)state.with((Property)BLOOM, (Comparable)Boolean.valueOf(false)), 3);
        }
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SculkCatalystBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return null;
        }
        return SculkCatalystBlock.validateTicker(type, (BlockEntityType)BlockEntityType.SCULK_CATALYST, SculkCatalystBlockEntity::tick);
    }

    protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.onStacksDropped(state, world, pos, tool, dropExperience);
        if (dropExperience) {
            this.dropExperienceWhenMined(world, pos, tool, this.experience);
        }
    }
}

