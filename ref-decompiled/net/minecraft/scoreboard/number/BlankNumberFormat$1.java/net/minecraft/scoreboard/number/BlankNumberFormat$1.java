/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.scoreboard.number.BlankNumberFormat;
import net.minecraft.scoreboard.number.NumberFormatType;

class BlankNumberFormat.1
implements NumberFormatType<BlankNumberFormat> {
    private static final MapCodec<BlankNumberFormat> CODEC = MapCodec.unit((Object)INSTANCE);
    private static final PacketCodec<RegistryByteBuf, BlankNumberFormat> PACKET_CODEC = PacketCodec.unit(INSTANCE);

    BlankNumberFormat.1() {
    }

    @Override
    public MapCodec<BlankNumberFormat> getCodec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, BlankNumberFormat> getPacketCodec() {
        return PACKET_CODEC;
    }
}
