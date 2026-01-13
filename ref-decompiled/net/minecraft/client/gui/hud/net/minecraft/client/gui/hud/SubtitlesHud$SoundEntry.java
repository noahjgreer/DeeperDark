/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
static final class SubtitlesHud.SoundEntry
extends Record {
    final Vec3d location;
    final long time;

    SubtitlesHud.SoundEntry(Vec3d location, long time) {
        this.location = location;
        this.time = time;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SubtitlesHud.SoundEntry.class, "location;time", "location", "time"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SubtitlesHud.SoundEntry.class, "location;time", "location", "time"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SubtitlesHud.SoundEntry.class, "location;time", "location", "time"}, this, object);
    }

    public Vec3d location() {
        return this.location;
    }

    public long time() {
        return this.time;
    }
}
