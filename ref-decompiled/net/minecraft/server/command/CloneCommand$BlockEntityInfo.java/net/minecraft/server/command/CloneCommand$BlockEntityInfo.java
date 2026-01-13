/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;

static final class CloneCommand.BlockEntityInfo
extends Record {
    final NbtCompound nbt;
    final ComponentMap components;

    CloneCommand.BlockEntityInfo(NbtCompound nbt, ComponentMap components) {
        this.nbt = nbt;
        this.components = components;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CloneCommand.BlockEntityInfo.class, "tag;components", "nbt", "components"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CloneCommand.BlockEntityInfo.class, "tag;components", "nbt", "components"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CloneCommand.BlockEntityInfo.class, "tag;components", "nbt", "components"}, this, object);
    }

    public NbtCompound nbt() {
        return this.nbt;
    }

    public ComponentMap components() {
        return this.components;
    }
}
