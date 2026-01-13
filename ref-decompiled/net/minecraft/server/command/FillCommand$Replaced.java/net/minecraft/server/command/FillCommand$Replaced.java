/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

static final class FillCommand.Replaced
extends Record {
    final BlockPos pos;
    final BlockState oldState;

    FillCommand.Replaced(BlockPos pos, BlockState oldState) {
        this.pos = pos;
        this.oldState = oldState;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{FillCommand.Replaced.class, "pos;oldState", "pos", "oldState"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FillCommand.Replaced.class, "pos;oldState", "pos", "oldState"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FillCommand.Replaced.class, "pos;oldState", "pos", "oldState"}, this, object);
    }

    public BlockPos pos() {
        return this.pos;
    }

    public BlockState oldState() {
        return this.oldState;
    }
}
