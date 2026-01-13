/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.predicate;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.math.MathHelper;

public record NumberRange.DoubleRange(NumberRange.Bounds<Double> bounds, NumberRange.Bounds<Double> boundsSqr) implements NumberRange<Double>
{
    public static final NumberRange.DoubleRange ANY = new NumberRange.DoubleRange(NumberRange.Bounds.any());
    public static final Codec<NumberRange.DoubleRange> CODEC = NumberRange.Bounds.createCodec(Codec.DOUBLE).validate(NumberRange.Bounds::validate).xmap(NumberRange.DoubleRange::new, NumberRange.DoubleRange::bounds);
    public static final PacketCodec<ByteBuf, NumberRange.DoubleRange> PACKET_CODEC = NumberRange.Bounds.createPacketCodec(PacketCodecs.DOUBLE).xmap(NumberRange.DoubleRange::new, NumberRange.DoubleRange::bounds);

    private NumberRange.DoubleRange(NumberRange.Bounds<Double> bounds) {
        this(bounds, bounds.map(MathHelper::square));
    }

    public static NumberRange.DoubleRange exactly(double value) {
        return new NumberRange.DoubleRange(NumberRange.Bounds.exactly(value));
    }

    public static NumberRange.DoubleRange between(double min, double max) {
        return new NumberRange.DoubleRange(NumberRange.Bounds.between(min, max));
    }

    public static NumberRange.DoubleRange atLeast(double value) {
        return new NumberRange.DoubleRange(NumberRange.Bounds.atLeast(value));
    }

    public static NumberRange.DoubleRange atMost(double value) {
        return new NumberRange.DoubleRange(NumberRange.Bounds.atMost(value));
    }

    public boolean test(double value) {
        if (this.bounds.min.isPresent() && (Double)this.bounds.min.get() > value) {
            return false;
        }
        return this.bounds.max.isEmpty() || !((Double)this.bounds.max.get() < value);
    }

    public boolean testSqrt(double value) {
        if (this.boundsSqr.min.isPresent() && (Double)this.boundsSqr.min.get() > value) {
            return false;
        }
        return this.boundsSqr.max.isEmpty() || !((Double)this.boundsSqr.max.get() < value);
    }

    public static NumberRange.DoubleRange parse(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        NumberRange.Bounds<Double> bounds = NumberRange.Bounds.parse(reader, Double::parseDouble, () -> ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS).readerInvalidDouble());
        if (bounds.isSwapped()) {
            reader.setCursor(i);
            throw EXCEPTION_SWAPPED.createWithContext((ImmutableStringReader)reader);
        }
        return new NumberRange.DoubleRange(bounds);
    }
}
