/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$Error
 *  com.mojang.serialization.DataResult$Success
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;

static class NbtWriteView.NbtListAppender<T>
implements WriteView.ListAppender<T> {
    private final ErrorReporter reporter;
    private final String key;
    private final DynamicOps<NbtElement> ops;
    private final Codec<T> codec;
    private final NbtList list;

    NbtWriteView.NbtListAppender(ErrorReporter reporter, String key, DynamicOps<NbtElement> ops, Codec<T> codec, NbtList list) {
        this.reporter = reporter;
        this.key = key;
        this.ops = ops;
        this.codec = codec;
        this.list = list;
    }

    @Override
    public void add(T value) {
        DataResult dataResult = this.codec.encodeStart(this.ops, value);
        Objects.requireNonNull(dataResult);
        DataResult dataResult2 = dataResult;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{DataResult.Success.class, DataResult.Error.class}, (Object)dataResult2, n)) {
            default: {
                throw new MatchException(null, null);
            }
            case 0: {
                DataResult.Success success = (DataResult.Success)dataResult2;
                this.list.add((NbtElement)success.value());
                break;
            }
            case 1: {
                DataResult.Error error = (DataResult.Error)dataResult2;
                this.reporter.report(new NbtWriteView.AppendToListError(this.key, value, error));
                error.partialValue().ifPresent(this.list::add);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }
}
