/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.IdF
 *  com.mojang.datafixers.kinds.IdF$Mu
 */
package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.kinds.IdF;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQuery;
import net.minecraft.entity.ai.brain.MemoryQueryResult;

public record MemoryQuery.Value<Value>(MemoryModuleType<Value> memory) implements MemoryQuery<IdF.Mu, Value>
{
    @Override
    public MemoryModuleState getState() {
        return MemoryModuleState.VALUE_PRESENT;
    }

    @Override
    public MemoryQueryResult<IdF.Mu, Value> toQueryResult(Brain<?> brain, Optional<Value> value) {
        if (value.isEmpty()) {
            return null;
        }
        return new MemoryQueryResult(brain, this.memory, IdF.create(value.get()));
    }
}
