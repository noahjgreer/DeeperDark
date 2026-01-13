/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static final class BellBlockModel.BellModelState
extends Record {
    final float ticks;
    final @Nullable Direction shakeDirection;

    public BellBlockModel.BellModelState(float ticks, @Nullable Direction shakeDirection) {
        this.ticks = ticks;
        this.shakeDirection = shakeDirection;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BellBlockModel.BellModelState.class, "ticks;shakeDirection", "ticks", "shakeDirection"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BellBlockModel.BellModelState.class, "ticks;shakeDirection", "ticks", "shakeDirection"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BellBlockModel.BellModelState.class, "ticks;shakeDirection", "ticks", "shakeDirection"}, this, object);
    }

    public float ticks() {
        return this.ticks;
    }

    public @Nullable Direction shakeDirection() {
        return this.shakeDirection;
    }
}
