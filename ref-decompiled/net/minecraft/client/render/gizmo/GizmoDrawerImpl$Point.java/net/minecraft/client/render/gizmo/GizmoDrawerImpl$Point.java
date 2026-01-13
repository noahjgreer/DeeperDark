/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.gizmo;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
static final class GizmoDrawerImpl.Point
extends Record {
    final Vec3d pos;
    private final int color;
    private final float size;

    GizmoDrawerImpl.Point(Vec3d pos, int color, float size) {
        this.pos = pos;
        this.color = color;
        this.size = size;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GizmoDrawerImpl.Point.class, "pos;color;size", "pos", "color", "size"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GizmoDrawerImpl.Point.class, "pos;color;size", "pos", "color", "size"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GizmoDrawerImpl.Point.class, "pos;color;size", "pos", "color", "size"}, this, object);
    }

    public Vec3d pos() {
        return this.pos;
    }

    public int color() {
        return this.color;
    }

    public float size() {
        return this.size;
    }
}
