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
import net.minecraft.world.debug.gizmo.TextGizmo;

@Environment(value=EnvType.CLIENT)
static final class GizmoDrawerImpl.Text
extends Record {
    private final Vec3d pos;
    final String text;
    final TextGizmo.Style style;

    GizmoDrawerImpl.Text(Vec3d pos, String text, TextGizmo.Style style) {
        this.pos = pos;
        this.text = text;
        this.style = style;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GizmoDrawerImpl.Text.class, "pos;text;style", "pos", "text", "style"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GizmoDrawerImpl.Text.class, "pos;text;style", "pos", "text", "style"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GizmoDrawerImpl.Text.class, "pos;text;style", "pos", "text", "style"}, this, object);
    }

    public Vec3d pos() {
        return this.pos;
    }

    public String text() {
        return this.text;
    }

    public TextGizmo.Style style() {
        return this.style;
    }
}
