/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.slf4j.Logger
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

static class Brain.1
extends MapCodec<Brain<E>> {
    final /* synthetic */ Collection field_24658;
    final /* synthetic */ Collection field_24659;
    final /* synthetic */ MutableObject field_24660;

    Brain.1(Collection collection, Collection collection2, MutableObject mutableObject) {
        this.field_24658 = collection;
        this.field_24659 = collection2;
        this.field_24660 = mutableObject;
    }

    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return this.field_24658.stream().flatMap(memoryType -> memoryType.getCodec().map(codec -> Registries.MEMORY_MODULE_TYPE.getId((MemoryModuleType<?>)memoryType)).stream()).map(id -> ops.createString(id.toString()));
    }

    public <T> DataResult<Brain<E>> decode(DynamicOps<T> ops, MapLike<T> map) {
        MutableObject mutableObject = new MutableObject((Object)DataResult.success((Object)ImmutableList.builder()));
        map.entries().forEach(pair -> {
            DataResult dataResult = Registries.MEMORY_MODULE_TYPE.getCodec().parse(ops, pair.getFirst());
            DataResult dataResult2 = dataResult.flatMap(memoryType -> this.parse((MemoryModuleType)memoryType, ops, (Object)pair.getSecond()));
            mutableObject.setValue((Object)((DataResult)mutableObject.get()).apply2(ImmutableList.Builder::add, dataResult2));
        });
        ImmutableList immutableList = ((DataResult)mutableObject.get()).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).map(ImmutableList.Builder::build).orElseGet(ImmutableList::of);
        return DataResult.success(new Brain(this.field_24658, this.field_24659, (ImmutableList<Brain.MemoryEntry<?>>)immutableList, this.field_24660));
    }

    private <T, U> DataResult<Brain.MemoryEntry<U>> parse(MemoryModuleType<U> memoryType, DynamicOps<T> ops, T value) {
        return memoryType.getCodec().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "No codec for memory: " + String.valueOf(memoryType))).flatMap(codec -> codec.parse(ops, value)).map(data -> new Brain.MemoryEntry(memoryType, Optional.of(data)));
    }

    public <T> RecordBuilder<T> encode(Brain<E> brain, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
        brain.streamMemories().forEach(entry -> entry.serialize(dynamicOps, recordBuilder));
        return recordBuilder;
    }

    public /* synthetic */ RecordBuilder encode(Object brain, DynamicOps ops, RecordBuilder recordBuilder) {
        return this.encode((Brain)brain, ops, recordBuilder);
    }
}
