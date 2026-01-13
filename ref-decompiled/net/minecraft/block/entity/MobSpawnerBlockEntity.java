/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.MobSpawnerBlockEntity
 *  net.minecraft.block.entity.Spawner
 *  net.minecraft.block.spawner.MobSpawnerLogic
 *  net.minecraft.entity.EntityType
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Spawner;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class MobSpawnerBlockEntity
extends BlockEntity
implements Spawner {
    private final MobSpawnerLogic logic = new /* Unavailable Anonymous Inner Class!! */;

    public MobSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.MOB_SPAWNER, pos, state);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.logic.readData(this.world, this.pos, view);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        this.logic.writeData(view);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, MobSpawnerBlockEntity blockEntity) {
        blockEntity.logic.clientTick(world, pos);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, MobSpawnerBlockEntity blockEntity) {
        blockEntity.logic.serverTick((ServerWorld)world, pos);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbtCompound = this.createComponentlessNbt(registries);
        nbtCompound.remove("SpawnPotentials");
        return nbtCompound;
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (this.logic.handleStatus(this.world, type)) {
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    public void setEntityType(EntityType<?> type, Random random) {
        this.logic.setEntityId(type, this.world, random, this.pos);
        this.markDirty();
    }

    public MobSpawnerLogic getLogic() {
        return this.logic;
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

