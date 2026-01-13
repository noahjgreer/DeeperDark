/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Const
 *  com.mojang.datafixers.kinds.Const$Mu
 *  com.mojang.datafixers.util.Unit
 */
package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.util.Unit;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQuery;
import net.minecraft.entity.ai.brain.MemoryQueryResult;

public record MemoryQuery.Absent<Value>(MemoryModuleType<Value> memory) implements MemoryQuery<Const.Mu<Unit>, Value>
{
    @Override
    public MemoryModuleState getState() {
        return MemoryModuleState.VALUE_ABSENT;
    }

    @Override
    public MemoryQueryResult<Const.Mu<Unit>, Value> toQueryResult(Brain<?> brain, Optional<Value> value) {
        if (value.isPresent()) {
            return null;
        }
        return new MemoryQueryResult(brain, this.memory, Const.create((Object)Unit.INSTANCE));
    }
}
