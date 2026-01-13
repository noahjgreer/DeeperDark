/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;

class ReadContext.3
implements ReadView {
    ReadContext.3() {
    }

    @Override
    public <T> Optional<T> read(String key, Codec<T> codec) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> read(MapCodec<T> mapCodec) {
        return Optional.empty();
    }

    @Override
    public Optional<ReadView> getOptionalReadView(String key) {
        return Optional.empty();
    }

    @Override
    public ReadView getReadView(String key) {
        return this;
    }

    @Override
    public Optional<ReadView.ListReadView> getOptionalListReadView(String key) {
        return Optional.empty();
    }

    @Override
    public ReadView.ListReadView getListReadView(String key) {
        return ReadContext.this.emptyListReadView;
    }

    @Override
    public <T> Optional<ReadView.TypedListReadView<T>> getOptionalTypedListView(String key, Codec<T> typeCodec) {
        return Optional.empty();
    }

    @Override
    public <T> ReadView.TypedListReadView<T> getTypedListView(String key, Codec<T> typeCodec) {
        return ReadContext.this.getEmptyTypedListReadView();
    }

    @Override
    public boolean getBoolean(String key, boolean fallback) {
        return fallback;
    }

    @Override
    public byte getByte(String key, byte fallback) {
        return fallback;
    }

    @Override
    public int getShort(String key, short fallback) {
        return fallback;
    }

    @Override
    public Optional<Integer> getOptionalInt(String key) {
        return Optional.empty();
    }

    @Override
    public int getInt(String key, int fallback) {
        return fallback;
    }

    @Override
    public long getLong(String key, long fallback) {
        return fallback;
    }

    @Override
    public Optional<Long> getOptionalLong(String key) {
        return Optional.empty();
    }

    @Override
    public float getFloat(String key, float fallback) {
        return fallback;
    }

    @Override
    public double getDouble(String key, double fallback) {
        return fallback;
    }

    @Override
    public Optional<String> getOptionalString(String key) {
        return Optional.empty();
    }

    @Override
    public String getString(String key, String fallback) {
        return fallback;
    }

    @Override
    public RegistryWrapper.WrapperLookup getRegistries() {
        return ReadContext.this.registries;
    }

    @Override
    public Optional<int[]> getOptionalIntArray(String key) {
        return Optional.empty();
    }
}
