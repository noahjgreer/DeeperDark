/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.util.math.Vec3d;

static final class Entity.QueuedCollisionCheck
extends Record {
    final Vec3d from;
    final Vec3d to;
    private final Optional<Vec3d> axisDependentOriginalMovement;

    public Entity.QueuedCollisionCheck(Vec3d vec3d, Vec3d vec3d2, Vec3d vec3d3) {
        this(vec3d, vec3d2, Optional.of(vec3d3));
    }

    public Entity.QueuedCollisionCheck(Vec3d vec3d, Vec3d vec3d2) {
        this(vec3d, vec3d2, Optional.empty());
    }

    private Entity.QueuedCollisionCheck(Vec3d from, Vec3d to, Optional<Vec3d> axisDependentOriginalMovement) {
        this.from = from;
        this.to = to;
        this.axisDependentOriginalMovement = axisDependentOriginalMovement;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entity.QueuedCollisionCheck.class, "from;to;axisDependentOriginalMovement", "from", "to", "axisDependentOriginalMovement"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entity.QueuedCollisionCheck.class, "from;to;axisDependentOriginalMovement", "from", "to", "axisDependentOriginalMovement"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entity.QueuedCollisionCheck.class, "from;to;axisDependentOriginalMovement", "from", "to", "axisDependentOriginalMovement"}, this, object);
    }

    public Vec3d from() {
        return this.from;
    }

    public Vec3d to() {
        return this.to;
    }

    public Optional<Vec3d> axisDependentOriginalMovement() {
        return this.axisDependentOriginalMovement;
    }
}
