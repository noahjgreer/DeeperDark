/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class NeighborUpdateDebugRenderer.Update
extends Record {
    final int count;
    final int age;
    static final NeighborUpdateDebugRenderer.Update EMPTY = new NeighborUpdateDebugRenderer.Update(0, Integer.MAX_VALUE);

    private NeighborUpdateDebugRenderer.Update(int count, int age) {
        this.count = count;
        this.age = age;
    }

    public NeighborUpdateDebugRenderer.Update withAge(int age) {
        if (age == this.age) {
            return new NeighborUpdateDebugRenderer.Update(this.count + 1, age);
        }
        if (age < this.age) {
            return new NeighborUpdateDebugRenderer.Update(1, age);
        }
        return this;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{NeighborUpdateDebugRenderer.Update.class, "count;age", "count", "age"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NeighborUpdateDebugRenderer.Update.class, "count;age", "count", "age"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NeighborUpdateDebugRenderer.Update.class, "count;age", "count", "age"}, this, object);
    }

    public int count() {
        return this.count;
    }

    public int age() {
        return this.age;
    }
}
