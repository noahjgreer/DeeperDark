/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.DataQueryHandler
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket
 *  net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket
 *  net.minecraft.util.math.BlockPos
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DataQueryHandler {
    private final ClientPlayNetworkHandler networkHandler;
    private int expectedTransactionId = -1;
    private @Nullable Consumer<NbtCompound> callback;

    public DataQueryHandler(ClientPlayNetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    public boolean handleQueryResponse(int transactionId, @Nullable NbtCompound nbt) {
        if (this.expectedTransactionId == transactionId && this.callback != null) {
            this.callback.accept(nbt);
            this.callback = null;
            return true;
        }
        return false;
    }

    private int nextQuery(Consumer<NbtCompound> callback) {
        this.callback = callback;
        return ++this.expectedTransactionId;
    }

    public void queryEntityNbt(int entityNetworkId, Consumer<NbtCompound> callback) {
        int i = this.nextQuery(callback);
        this.networkHandler.sendPacket((Packet)new QueryEntityNbtC2SPacket(i, entityNetworkId));
    }

    public void queryBlockNbt(BlockPos pos, Consumer<NbtCompound> callback) {
        int i = this.nextQuery(callback);
        this.networkHandler.sendPacket((Packet)new QueryBlockNbtC2SPacket(i, pos));
    }
}

