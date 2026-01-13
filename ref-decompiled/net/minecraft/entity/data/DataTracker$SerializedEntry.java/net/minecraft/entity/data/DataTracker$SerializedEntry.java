/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 */
package net.minecraft.entity.data;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.RegistryByteBuf;

public static final class DataTracker.SerializedEntry<T>
extends Record {
    final int id;
    private final TrackedDataHandler<T> handler;
    final T value;

    public DataTracker.SerializedEntry(int id, TrackedDataHandler<T> handler, T value) {
        this.id = id;
        this.handler = handler;
        this.value = value;
    }

    public static <T> DataTracker.SerializedEntry<T> of(TrackedData<T> data, T value) {
        TrackedDataHandler<T> trackedDataHandler = data.dataType();
        return new DataTracker.SerializedEntry<T>(data.id(), trackedDataHandler, trackedDataHandler.copy(value));
    }

    public void write(RegistryByteBuf buf) {
        int i = TrackedDataHandlerRegistry.getId(this.handler);
        if (i < 0) {
            throw new EncoderException("Unknown serializer type " + String.valueOf(this.handler));
        }
        buf.writeByte(this.id);
        buf.writeVarInt(i);
        this.handler.codec().encode(buf, this.value);
    }

    public static DataTracker.SerializedEntry<?> fromBuf(RegistryByteBuf buf, int id) {
        int i = buf.readVarInt();
        TrackedDataHandler<?> trackedDataHandler = TrackedDataHandlerRegistry.get(i);
        if (trackedDataHandler == null) {
            throw new DecoderException("Unknown serializer type " + i);
        }
        return DataTracker.SerializedEntry.fromBuf(buf, id, trackedDataHandler);
    }

    private static <T> DataTracker.SerializedEntry<T> fromBuf(RegistryByteBuf buf, int id, TrackedDataHandler<T> handler) {
        return new DataTracker.SerializedEntry<T>(id, handler, handler.codec().decode(buf));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{DataTracker.SerializedEntry.class, "id;serializer;value", "id", "handler", "value"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DataTracker.SerializedEntry.class, "id;serializer;value", "id", "handler", "value"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DataTracker.SerializedEntry.class, "id;serializer;value", "id", "handler", "value"}, this, object);
    }

    public int id() {
        return this.id;
    }

    public TrackedDataHandler<T> handler() {
        return this.handler;
    }

    public T value() {
        return this.value;
    }
}
