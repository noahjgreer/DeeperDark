/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.chase;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

static final class ChaseClient.TeleportPos
extends Record {
    final RegistryKey<World> dimension;
    final Vec3d pos;
    final Vec2f rot;

    ChaseClient.TeleportPos(RegistryKey<World> dimension, Vec3d pos, Vec2f rot) {
        this.dimension = dimension;
        this.pos = pos;
        this.rot = rot;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChaseClient.TeleportPos.class, "level;pos;rot", "dimension", "pos", "rot"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChaseClient.TeleportPos.class, "level;pos;rot", "dimension", "pos", "rot"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChaseClient.TeleportPos.class, "level;pos;rot", "dimension", "pos", "rot"}, this, object);
    }

    public RegistryKey<World> dimension() {
        return this.dimension;
    }

    public Vec3d pos() {
        return this.pos;
    }

    public Vec2f rot() {
        return this.rot;
    }
}
