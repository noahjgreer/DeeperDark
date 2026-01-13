/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.predicate;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.NumberRange;

public record NumberRange.AngleRange(NumberRange.Bounds<Float> bounds) implements NumberRange<Float>
{
    public static final NumberRange.AngleRange ANY = new NumberRange.AngleRange(NumberRange.Bounds.any());
    public static final Codec<NumberRange.AngleRange> CODEC = NumberRange.Bounds.createCodec(Codec.FLOAT).xmap(NumberRange.AngleRange::new, NumberRange.AngleRange::bounds);
    public static final PacketCodec<ByteBuf, NumberRange.AngleRange> PACKET_CODEC = NumberRange.Bounds.createPacketCodec(PacketCodecs.FLOAT).xmap(NumberRange.AngleRange::new, NumberRange.AngleRange::bounds);

    public static NumberRange.AngleRange parse(StringReader reader) throws CommandSyntaxException {
        NumberRange.Bounds<Float> bounds = NumberRange.Bounds.parse(reader, Float::parseFloat, () -> ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS).readerInvalidFloat());
        return new NumberRange.AngleRange(bounds);
    }
}
