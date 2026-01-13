/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.serialization.DynamicOps
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.VaultBlockEntity
 *  net.minecraft.block.vault.VaultClientData
 *  net.minecraft.block.vault.VaultConfig
 *  net.minecraft.block.vault.VaultServerData
 *  net.minecraft.block.vault.VaultSharedData
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.network.listener.ClientPlayPacketListener
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.vault.VaultClientData;
import net.minecraft.block.vault.VaultConfig;
import net.minecraft.block.vault.VaultServerData;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class VaultBlockEntity
extends BlockEntity {
    private final VaultServerData serverData = new VaultServerData();
    private final VaultSharedData sharedData = new VaultSharedData();
    private final VaultClientData clientData = new VaultClientData();
    private VaultConfig config = VaultConfig.DEFAULT;

    public VaultBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.VAULT, pos, state);
    }

    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return (NbtCompound)Util.make((Object)new NbtCompound(), nbt -> nbt.put("shared_data", VaultSharedData.codec, (DynamicOps)registries.getOps((DynamicOps)NbtOps.INSTANCE), (Object)this.sharedData));
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put("config", VaultConfig.codec, (Object)this.config);
        view.put("shared_data", VaultSharedData.codec, (Object)this.sharedData);
        view.put("server_data", VaultServerData.codec, (Object)this.serverData);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        view.read("server_data", VaultServerData.codec).ifPresent(arg_0 -> ((VaultServerData)this.serverData).copyFrom(arg_0));
        this.config = view.read("config", VaultConfig.codec).orElse(VaultConfig.DEFAULT);
        view.read("shared_data", VaultSharedData.codec).ifPresent(arg_0 -> ((VaultSharedData)this.sharedData).copyFrom(arg_0));
    }

    public @Nullable VaultServerData getServerData() {
        return this.world == null || this.world.isClient() ? null : this.serverData;
    }

    public VaultSharedData getSharedData() {
        return this.sharedData;
    }

    public VaultClientData getClientData() {
        return this.clientData;
    }

    public VaultConfig getConfig() {
        return this.config;
    }

    @VisibleForTesting
    public void setConfig(VaultConfig config) {
        this.config = config;
    }

    static /* synthetic */ void method_56732(World world, BlockPos blockPos, BlockState blockState) {
        VaultBlockEntity.markDirty((World)world, (BlockPos)blockPos, (BlockState)blockState);
    }
}

