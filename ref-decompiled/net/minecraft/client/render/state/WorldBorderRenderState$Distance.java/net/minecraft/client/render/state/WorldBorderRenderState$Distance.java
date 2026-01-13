/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.state;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public static final class WorldBorderRenderState.Distance
extends Record {
    private final Direction direction;
    final double value;

    public WorldBorderRenderState.Distance(Direction direction, double value) {
        this.direction = direction;
        this.value = value;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{WorldBorderRenderState.Distance.class, "direction;distance", "direction", "value"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{WorldBorderRenderState.Distance.class, "direction;distance", "direction", "value"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{WorldBorderRenderState.Distance.class, "direction;distance", "direction", "value"}, this, object);
    }

    public Direction direction() {
        return this.direction;
    }

    public double value() {
        return this.value;
    }
}
