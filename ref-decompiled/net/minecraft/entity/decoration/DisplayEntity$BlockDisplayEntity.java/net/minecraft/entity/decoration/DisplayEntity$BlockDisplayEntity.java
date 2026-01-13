/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.decoration;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public static class DisplayEntity.BlockDisplayEntity
extends DisplayEntity {
    public static final String BLOCK_STATE_NBT_KEY = "block_state";
    private static final TrackedData<BlockState> BLOCK_STATE = DataTracker.registerData(DisplayEntity.BlockDisplayEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
    private @Nullable Data data;

    public DisplayEntity.BlockDisplayEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BLOCK_STATE, Blocks.AIR.getDefaultState());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (data.equals(BLOCK_STATE)) {
            this.renderingDataSet = true;
        }
    }

    public final BlockState getBlockState() {
        return this.dataTracker.get(BLOCK_STATE);
    }

    public final void setBlockState(BlockState state) {
        this.dataTracker.set(BLOCK_STATE, state);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.setBlockState(view.read(BLOCK_STATE_NBT_KEY, BlockState.CODEC).orElse(Blocks.AIR.getDefaultState()));
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.put(BLOCK_STATE_NBT_KEY, BlockState.CODEC, this.getBlockState());
    }

    public @Nullable Data getData() {
        return this.data;
    }

    @Override
    protected void refreshData(boolean shouldLerp, float lerpProgress) {
        this.data = new Data(this.getBlockState());
    }

    public record Data(BlockState blockState) {
    }
}
