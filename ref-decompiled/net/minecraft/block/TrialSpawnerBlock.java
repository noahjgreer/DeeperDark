/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.TrialSpawnerBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.TrialSpawnerBlockEntity
 *  net.minecraft.block.enums.TrialSpawnerState
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
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
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class TrialSpawnerBlock
extends BlockWithEntity {
    public static final MapCodec<TrialSpawnerBlock> CODEC = TrialSpawnerBlock.createCodec(TrialSpawnerBlock::new);
    public static final EnumProperty<TrialSpawnerState> TRIAL_SPAWNER_STATE = Properties.TRIAL_SPAWNER_STATE;
    public static final BooleanProperty OMINOUS = Properties.OMINOUS;

    public MapCodec<TrialSpawnerBlock> getCodec() {
        return CODEC;
    }

    public TrialSpawnerBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)TRIAL_SPAWNER_STATE, (Comparable)TrialSpawnerState.INACTIVE)).with((Property)OMINOUS, (Comparable)Boolean.valueOf(false)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{TRIAL_SPAWNER_STATE, OMINOUS});
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TrialSpawnerBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world2, BlockState state2, BlockEntityType<T> type) {
        BlockEntityTicker blockEntityTicker;
        if (world2 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world2;
            blockEntityTicker = TrialSpawnerBlock.validateTicker(type, (BlockEntityType)BlockEntityType.TRIAL_SPAWNER, (world, pos, state, blockEntity) -> blockEntity.getSpawner().tickServer(serverWorld, pos, state.getOrEmpty((Property)Properties.OMINOUS).orElse(false).booleanValue()));
        } else {
            blockEntityTicker = TrialSpawnerBlock.validateTicker(type, (BlockEntityType)BlockEntityType.TRIAL_SPAWNER, (world, pos, state, blockEntity) -> blockEntity.getSpawner().tickClient(world, pos, state.getOrEmpty((Property)Properties.OMINOUS).orElse(false).booleanValue()));
        }
        return blockEntityTicker;
    }
}

