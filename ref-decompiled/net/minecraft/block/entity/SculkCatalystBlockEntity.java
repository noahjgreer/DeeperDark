/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.SculkCatalystBlockEntity
 *  net.minecraft.block.entity.SculkCatalystBlockEntity$Listener
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.event.BlockPositionSource
 *  net.minecraft.world.event.PositionSource
 *  net.minecraft.world.event.listener.GameEventListener
 *  net.minecraft.world.event.listener.GameEventListener$Holder
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

public class SculkCatalystBlockEntity
extends BlockEntity
implements GameEventListener.Holder<Listener> {
    private final Listener eventListener;

    public SculkCatalystBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SCULK_CATALYST, pos, state);
        this.eventListener = new Listener(state, (PositionSource)new BlockPositionSource(pos));
    }

    public static void tick(World world, BlockPos pos, BlockState state, SculkCatalystBlockEntity blockEntity) {
        blockEntity.eventListener.getSpreadManager().tick((WorldAccess)world, pos, world.getRandom(), true);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.eventListener.spreadManager.readData(view);
    }

    protected void writeData(WriteView view) {
        this.eventListener.spreadManager.writeData(view);
        super.writeData(view);
    }

    public Listener getEventListener() {
        return this.eventListener;
    }

    public /* synthetic */ GameEventListener getEventListener() {
        return this.getEventListener();
    }
}

