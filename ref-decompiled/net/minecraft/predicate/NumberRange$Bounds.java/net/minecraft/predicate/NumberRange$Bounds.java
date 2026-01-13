/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.predicate;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.codec.PacketCodec;

public static final class NumberRange.Bounds<T extends Number>
extends Record {
    final Optional<T> min;
    final Optional<T> max;

    public NumberRange.Bounds(Optional<T> min, Optional<T> max) {
        this.min = min;
        this.max = max;
    }

    public boolean isAny() {
        return this.min().isEmpty() && this.max().isEmpty();
    }

    public DataResult<NumberRange.Bounds<T>> validate() {
        if (this.isSwapped()) {
            return DataResult.error(() -> "Swapped bounds in range: " + String.valueOf(this.min()) + " is higher than " + String.valueOf(this.max()));
        }
        return DataResult.success((Object)this);
    }

    public boolean isSwapped() {
        return this.min.isPresent() && this.max.isPresent() && ((Comparable)((Object)((Number)this.min.get()))).compareTo((Number)this.max.get()) > 0;
    }

    public Optional<T> getPoint() {
        Optional<T> optional2;
        Optional<T> optional = this.min();
        return optional.equals(optional2 = this.max()) ? optional : Optional.empty();
    }

    public static <T extends Number> NumberRange.Bounds<T> any() {
        return new NumberRange.Bounds(Optional.empty(), Optional.empty());
    }

    public static <T extends Number> NumberRange.Bounds<T> exactly(T value) {
        Optional<T> optional = Optional.of(value);
        return new NumberRange.Bounds<T>(optional, optional);
    }

    public static <T extends Number> NumberRange.Bounds<T> between(T min, T max) {
        return new NumberRange.Bounds<T>(Optional.of(min), Optional.of(max));
    }

    public static <T extends Number> NumberRange.Bounds<T> atLeast(T value) {
        return new NumberRange.Bounds<T>(Optional.of(value), Optional.empty());
    }

    public static <T extends Number> NumberRange.Bounds<T> atMost(T value) {
        return new NumberRange.Bounds(Optional.empty(), Optional.of(value));
    }

    public <U extends Number> NumberRange.Bounds<U> map(Function<T, U> mappingFunction) {
        return new NumberRange.Bounds<U>(this.min.map(mappingFunction), this.max.map(mappingFunction));
    }

    static <T extends Number> Codec<NumberRange.Bounds<T>> createCodec(Codec<T> valueCodec) {
        Codec codec = RecordCodecBuilder.create(instance -> instance.group((App)valueCodec.optionalFieldOf("min").forGetter(NumberRange.Bounds::min), (App)valueCodec.optionalFieldOf("max").forGetter(NumberRange.Bounds::max)).apply((Applicative)instance, NumberRange.Bounds::new));
        return Codec.either((Codec)codec, valueCodec).xmap(either -> (NumberRange.Bounds)either.map(bounds -> bounds, value -> NumberRange.Bounds.exactly((Number)value)), bounds -> {
            Optional optional = bounds.getPoint();
            return optional.isPresent() ? Either.right((Object)((Number)optional.get())) : Either.left((Object)bounds);
        });
    }

    static <B extends ByteBuf, T extends Number> PacketCodec<B, NumberRange.Bounds<T>> createPacketCodec(final PacketCodec<B, T> valuePacketCodec) {
        return new PacketCodec<B, NumberRange.Bounds<T>>(){
            private static final int MIN_PRESENT_FLAG = 1;
            private static final int MAX_PRESENT_FLAG = 2;

            @Override
            public NumberRange.Bounds<T> decode(B byteBuf) {
                byte b = byteBuf.readByte();
                Optional optional = (b & 1) != 0 ? Optional.of((Number)valuePacketCodec.decode(byteBuf)) : Optional.empty();
                Optional optional2 = (b & 2) != 0 ? Optional.of((Number)valuePacketCodec.decode(byteBuf)) : Optional.empty();
                return new NumberRange.Bounds(optional, optional2);
            }

            @Override
            public void encode(B byteBuf, NumberRange.Bounds<T> bounds) {
                Optional<Number> optional = bounds.min();
                Optional<Number> optional2 = bounds.max();
                byteBuf.writeByte((optional.isPresent() ? 1 : 0) | (optional2.isPresent() ? 2 : 0));
                optional.ifPresent(min -> valuePacketCodec.encode(byteBuf, min));
                optional2.ifPresent(max -> valuePacketCodec.encode(byteBuf, max));
            }

            @Override
            public /* synthetic */ void encode(Object object, Object object2) {
                this.encode((Object)((ByteBuf)object), (NumberRange.Bounds)object2);
            }

            @Override
            public /* synthetic */ Object decode(Object object) {
                return this.decode((B)((ByteBuf)object));
            }
        };
    }

    public static <T extends Number> NumberRange.Bounds<T> parse(StringReader reader, Function<String, T> parsingFunction, Supplier<DynamicCommandExceptionType> exceptionSupplier) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)reader);
        }
        int i = reader.getCursor();
        try {
            Optional<T> optional2;
            Optional<T> optional = NumberRange.Bounds.parseNumber(reader, parsingFunction, exceptionSupplier);
            if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
                reader.skip();
                reader.skip();
                optional2 = NumberRange.Bounds.parseNumber(reader, parsingFunction, exceptionSupplier);
            } else {
                optional2 = optional;
            }
            if (optional.isEmpty() && optional2.isEmpty()) {
                throw EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)reader);
            }
            return new NumberRange.Bounds<T>(optional, optional2);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            reader.setCursor(i);
            throw new CommandSyntaxException(commandSyntaxException.getType(), commandSyntaxException.getRawMessage(), commandSyntaxException.getInput(), i);
        }
    }

    private static <T extends Number> Optional<T> parseNumber(StringReader reader, Function<String, T> parsingFunction, Supplier<DynamicCommandExceptionType> exceptionSupplier) throws CommandSyntaxException {
        int i = reader.getCursor();
        while (reader.canRead() && NumberRange.Bounds.shouldSkip(reader)) {
            reader.skip();
        }
        String string = reader.getString().substring(i, reader.getCursor());
        if (string.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of((Number)parsingFunction.apply(string));
        }
        catch (NumberFormatException numberFormatException) {
            throw exceptionSupplier.get().createWithContext((ImmutableStringReader)reader, (Object)string);
        }
    }

    private static boolean shouldSkip(StringReader reader) {
        char c = reader.peek();
        if (c >= '0' && c <= '9' || c == '-') {
            return true;
        }
        if (c == '.') {
            return !reader.canRead(2) || reader.peek(1) != '.';
        }
        return false;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{NumberRange.Bounds.class, "min;max", "min", "max"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NumberRange.Bounds.class, "min;max", "min", "max"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NumberRange.Bounds.class, "min;max", "min", "max"}, this, object);
    }

    public Optional<T> min() {
        return this.min;
    }

    public Optional<T> max() {
        return this.max;
    }
}
