/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockEntityProvider
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.event.listener.GameEventListener
 *  net.minecraft.world.event.listener.GameEventListener$Holder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.jspecify.annotations.Nullable;

public interface BlockEntityProvider {
    public @Nullable BlockEntity createBlockEntity(BlockPos var1, BlockState var2);

    default public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    default public <T extends BlockEntity> @Nullable GameEventListener getGameEventListener(ServerWorld world, T blockEntity) {
        if (blockEntity instanceof GameEventListener.Holder) {
            GameEventListener.Holder holder = (GameEventListener.Holder)blockEntity;
            return holder.getEventListener();
        }
        return null;
    }
}

