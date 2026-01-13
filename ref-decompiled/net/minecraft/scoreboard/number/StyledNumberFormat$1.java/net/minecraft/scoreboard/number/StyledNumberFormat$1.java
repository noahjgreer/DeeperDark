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
import net.minecraft.scoreboard.number.NumberFormatType;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Style;

class StyledNumberFormat.1
implements NumberFormatType<StyledNumberFormat> {
    private static final MapCodec<StyledNumberFormat> CODEC = Style.Codecs.MAP_CODEC.xmap(StyledNumberFormat::new, StyledNumberFormat::style);
    private static final PacketCodec<RegistryByteBuf, StyledNumberFormat> PACKET_CODEC = PacketCodec.tuple(Style.Codecs.PACKET_CODEC, StyledNumberFormat::style, StyledNumberFormat::new);

    StyledNumberFormat.1() {
    }

    @Override
    public MapCodec<StyledNumberFormat> getCodec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, StyledNumberFormat> getPacketCodec() {
        return PACKET_CODEC;
    }
}
