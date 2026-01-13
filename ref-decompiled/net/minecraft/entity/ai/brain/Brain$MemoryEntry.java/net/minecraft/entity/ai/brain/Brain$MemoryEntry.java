/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.entity.ai.brain;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;

static final class Brain.MemoryEntry<U> {
    private final MemoryModuleType<U> type;
    private final Optional<? extends Memory<U>> data;

    static <U> Brain.MemoryEntry<U> of(MemoryModuleType<U> type, Optional<? extends Memory<?>> data) {
        return new Brain.MemoryEntry<U>(type, data);
    }

    Brain.MemoryEntry(MemoryModuleType<U> type, Optional<? extends Memory<U>> data) {
        this.type = type;
        this.data = data;
    }

    void apply(Brain<?> brain) {
        brain.setMemory(this.type, this.data);
    }

    public <T> void serialize(DynamicOps<T> ops, RecordBuilder<T> builder) {
        this.type.getCodec().ifPresent(codec -> this.data.ifPresent(data -> builder.add(Registries.MEMORY_MODULE_TYPE.getCodec().encodeStart(ops, this.type), codec.encodeStart(ops, data))));
    }
}
