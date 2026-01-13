/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.command;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

static final class CloneCommand.BlockInfo
extends Record {
    final BlockPos pos;
    final BlockState state;
    final  @Nullable CloneCommand.BlockEntityInfo blockEntityInfo;
    final BlockState previousStateAtDestination;

    CloneCommand.BlockInfo(BlockPos pos, BlockState state,  @Nullable CloneCommand.BlockEntityInfo blockEntityInfo, BlockState previousStateAtDestination) {
        this.pos = pos;
        this.state = state;
        this.blockEntityInfo = blockEntityInfo;
        this.previousStateAtDestination = previousStateAtDestination;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CloneCommand.BlockInfo.class, "pos;state;blockEntityInfo;previousStateAtDestination", "pos", "state", "blockEntityInfo", "previousStateAtDestination"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CloneCommand.BlockInfo.class, "pos;state;blockEntityInfo;previousStateAtDestination", "pos", "state", "blockEntityInfo", "previousStateAtDestination"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CloneCommand.BlockInfo.class, "pos;state;blockEntityInfo;previousStateAtDestination", "pos", "state", "blockEntityInfo", "previousStateAtDestination"}, this, object);
    }

    public BlockPos pos() {
        return this.pos;
    }

    public BlockState state() {
        return this.state;
    }

    public  @Nullable CloneCommand.BlockEntityInfo blockEntityInfo() {
        return this.blockEntityInfo;
    }

    public BlockState previousStateAtDestination() {
        return this.previousStateAtDestination;
    }
}
