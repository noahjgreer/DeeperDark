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

public record NumberRange.IntRange(NumberRange.Bounds<Integer> bounds, NumberRange.Bounds<Long> boundsSqr) implements NumberRange<Integer>
{
    public static final NumberRange.IntRange ANY = new NumberRange.IntRange(NumberRange.Bounds.any());
    public static final Codec<NumberRange.IntRange> CODEC = NumberRange.Bounds.createCodec(Codec.INT).validate(NumberRange.Bounds::validate).xmap(NumberRange.IntRange::new, NumberRange.IntRange::bounds);
    public static final PacketCodec<ByteBuf, NumberRange.IntRange> PACKET_CODEC = NumberRange.Bounds.createPacketCodec(PacketCodecs.INTEGER).xmap(NumberRange.IntRange::new, NumberRange.IntRange::bounds);

    private NumberRange.IntRange(NumberRange.Bounds<Integer> bounds) {
        this(bounds, bounds.map(i -> MathHelper.square(i.longValue())));
    }

    public static NumberRange.IntRange exactly(int value) {
        return new NumberRange.IntRange(NumberRange.Bounds.exactly(value));
    }

    public static NumberRange.IntRange between(int min, int max) {
        return new NumberRange.IntRange(NumberRange.Bounds.between(min, max));
    }

    public static NumberRange.IntRange atLeast(int value) {
        return new NumberRange.IntRange(NumberRange.Bounds.atLeast(value));
    }

    public static NumberRange.IntRange atMost(int value) {
        return new NumberRange.IntRange(NumberRange.Bounds.atMost(value));
    }

    public boolean test(int value) {
        if (this.bounds.min.isPresent() && (Integer)this.bounds.min.get() > value) {
            return false;
        }
        return this.bounds.max.isEmpty() || (Integer)this.bounds.max.get() >= value;
    }

    public boolean testSqrt(long value) {
        if (this.boundsSqr.min.isPresent() && (Long)this.boundsSqr.min.get() > value) {
            return false;
        }
        return this.boundsSqr.max.isEmpty() || (Long)this.boundsSqr.max.get() >= value;
    }

    public static NumberRange.IntRange parse(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        NumberRange.Bounds<Integer> bounds = NumberRange.Bounds.parse(reader, Integer::parseInt, () -> ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS).readerInvalidInt());
        if (bounds.isSwapped()) {
            reader.setCursor(i);
            throw EXCEPTION_SWAPPED.createWithContext((ImmutableStringReader)reader);
        }
        return new NumberRange.IntRange(bounds);
    }
}
