/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.util.math.BlockPos;

public record LongJumpTask.Target(BlockPos pos, int weight) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{LongJumpTask.Target.class, "targetPos;weight", "pos", "weight"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LongJumpTask.Target.class, "targetPos;weight", "pos", "weight"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LongJumpTask.Target.class, "targetPos;weight", "pos", "weight"}, this, object);
    }
}
