/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.chase;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Locale;

record ChaseServer.TeleportPos(String dimensionName, double x, double y, double z, float yaw, float pitch) {
    String getTeleportCommand() {
        return String.format(Locale.ROOT, "t %s %.2f %.2f %.2f %.2f %.2f\n", this.dimensionName, this.x, this.y, this.z, Float.valueOf(this.yaw), Float.valueOf(this.pitch));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChaseServer.TeleportPos.class, "dimensionName;x;y;z;yRot;xRot", "dimensionName", "x", "y", "z", "yaw", "pitch"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChaseServer.TeleportPos.class, "dimensionName;x;y;z;yRot;xRot", "dimensionName", "x", "y", "z", "yaw", "pitch"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChaseServer.TeleportPos.class, "dimensionName;x;y;z;yRot;xRot", "dimensionName", "x", "y", "z", "yaw", "pitch"}, this, object);
    }
}
