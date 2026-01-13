/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.world;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.server.world.OptionalChunk;
import org.jspecify.annotations.Nullable;

public record OptionalChunk.ActualChunk<T>(T value) implements OptionalChunk<T>
{
    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public T orElse(@Nullable T other) {
        return this.value;
    }

    @Override
    public @Nullable String getError() {
        return null;
    }

    @Override
    public OptionalChunk<T> ifPresent(Consumer<T> callback) {
        callback.accept(this.value);
        return this;
    }

    @Override
    public <R> OptionalChunk<R> map(Function<T, R> mapper) {
        return new OptionalChunk.ActualChunk<R>(mapper.apply(this.value));
    }

    @Override
    public <E extends Throwable> T orElseThrow(Supplier<E> exceptionSupplier) throws E {
        return this.value;
    }
}
