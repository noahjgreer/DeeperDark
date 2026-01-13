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
static final class GameTestDebugRenderer.Marker
extends Record {
    private final int color;
    final String message;
    final long removalTime;

    GameTestDebugRenderer.Marker(int color, String message, long removalTime) {
        this.color = color;
        this.message = message;
        this.removalTime = removalTime;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GameTestDebugRenderer.Marker.class, "color;text;removeAtTime", "color", "message", "removalTime"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GameTestDebugRenderer.Marker.class, "color;text;removeAtTime", "color", "message", "removalTime"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GameTestDebugRenderer.Marker.class, "color;text;removeAtTime", "color", "message", "removalTime"}, this, object);
    }

    public int color() {
        return this.color;
    }

    public String message() {
        return this.message;
    }

    public long removalTime() {
        return this.removalTime;
    }
}
