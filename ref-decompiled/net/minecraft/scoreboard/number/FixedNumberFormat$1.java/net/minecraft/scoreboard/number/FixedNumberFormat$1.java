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
import net.minecraft.scoreboard.number.FixedNumberFormat;
import net.minecraft.scoreboard.number.NumberFormatType;
import net.minecraft.text.TextCodecs;

class FixedNumberFormat.1
implements NumberFormatType<FixedNumberFormat> {
    private static final MapCodec<FixedNumberFormat> CODEC = TextCodecs.CODEC.fieldOf("value").xmap(FixedNumberFormat::new, FixedNumberFormat::text);
    private static final PacketCodec<RegistryByteBuf, FixedNumberFormat> PACKET_CODEC = PacketCodec.tuple(TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, FixedNumberFormat::text, FixedNumberFormat::new);

    FixedNumberFormat.1() {
    }

    @Override
    public MapCodec<FixedNumberFormat> getCodec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, FixedNumberFormat> getPacketCodec() {
        return PACKET_CODEC;
    }
}
