/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.RecordBuilder$AbstractStringBuilder
 */
package net.minecraft.nbt;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtOps;

class NbtOps.MapBuilder
extends RecordBuilder.AbstractStringBuilder<NbtElement, NbtCompound> {
    protected NbtOps.MapBuilder(NbtOps ops) {
        super((DynamicOps)ops);
    }

    protected NbtCompound initBuilder() {
        return new NbtCompound();
    }

    protected NbtCompound append(String string, NbtElement nbtElement, NbtCompound nbtCompound) {
        nbtCompound.put(string, nbtElement);
        return nbtCompound;
    }

    protected DataResult<NbtElement> build(NbtCompound nbtCompound, NbtElement nbtElement) {
        if (nbtElement == null || nbtElement == NbtEnd.INSTANCE) {
            return DataResult.success((Object)nbtCompound);
        }
        if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound2 = (NbtCompound)nbtElement;
            NbtCompound nbtCompound3 = nbtCompound2.shallowCopy();
            for (Map.Entry<String, NbtElement> entry : nbtCompound.entrySet()) {
                nbtCompound3.put(entry.getKey(), entry.getValue());
            }
            return DataResult.success((Object)nbtCompound3);
        }
        return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(nbtElement), (Object)nbtElement);
    }

    protected /* synthetic */ Object append(String key, Object value, Object nbt) {
        return this.append(key, (NbtElement)value, (NbtCompound)nbt);
    }

    protected /* synthetic */ DataResult build(Object nbt, Object mergedValue) {
        return this.build((NbtCompound)nbt, (NbtElement)mergedValue);
    }

    protected /* synthetic */ Object initBuilder() {
        return this.initBuilder();
    }
}
