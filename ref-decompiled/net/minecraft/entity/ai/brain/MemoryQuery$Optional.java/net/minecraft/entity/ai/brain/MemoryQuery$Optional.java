/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.OptionalBox
 *  com.mojang.datafixers.kinds.OptionalBox$Mu
 */
package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.kinds.OptionalBox;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQuery;
import net.minecraft.entity.ai.brain.MemoryQueryResult;

public record MemoryQuery.Optional<Value>(MemoryModuleType<Value> memory) implements MemoryQuery<OptionalBox.Mu, Value>
{
    @Override
    public MemoryModuleState getState() {
        return MemoryModuleState.REGISTERED;
    }

    @Override
    public MemoryQueryResult<OptionalBox.Mu, Value> toQueryResult(Brain<?> brain, Optional<Value> value) {
        return new MemoryQueryResult(brain, this.memory, OptionalBox.create(value));
    }
}
