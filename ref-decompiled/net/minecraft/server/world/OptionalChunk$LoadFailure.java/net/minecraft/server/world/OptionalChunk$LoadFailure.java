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

public record OptionalChunk.LoadFailure<T>(Supplier<String> error) implements OptionalChunk<T>
{
    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public @Nullable T orElse(@Nullable T other) {
        return other;
    }

    @Override
    public String getError() {
        return this.error.get();
    }

    @Override
    public OptionalChunk<T> ifPresent(Consumer<T> callback) {
        return this;
    }

    @Override
    public <R> OptionalChunk<R> map(Function<T, R> mapper) {
        return new OptionalChunk.LoadFailure<T>(this.error);
    }

    @Override
    public <E extends Throwable> T orElseThrow(Supplier<E> exceptionSupplier) throws E {
        throw (Throwable)exceptionSupplier.get();
    }
}
