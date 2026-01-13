/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.StringIdentifiable;
import org.jspecify.annotations.Nullable;

public static class StringIdentifiable.EnumCodec<E extends Enum<E>>
extends StringIdentifiable.BasicCodec<E> {
    private final Function<String, @Nullable E> idToIdentifiable;

    public StringIdentifiable.EnumCodec(E[] values, Function<String, E> idToIdentifiable) {
        super(values, idToIdentifiable, enum_ -> ((Enum)enum_).ordinal());
        this.idToIdentifiable = idToIdentifiable;
    }

    public @Nullable E byId(String id) {
        return (E)((Enum)this.idToIdentifiable.apply(id));
    }

    public E byId(String id, E fallback) {
        return (E)((Enum)Objects.requireNonNullElse(this.byId(id), fallback));
    }

    public E byId(String id, Supplier<? extends E> fallbackSupplier) {
        return (E)((Enum)Objects.requireNonNullElseGet(this.byId(id), fallbackSupplier));
    }
}
