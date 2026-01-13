/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.SoundListenerTransform
 *  net.minecraft.util.math.Vec3d
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public record SoundListenerTransform(Vec3d position, Vec3d forward, Vec3d up) {
    private final Vec3d position;
    private final Vec3d forward;
    private final Vec3d up;
    public static final SoundListenerTransform DEFAULT = new SoundListenerTransform(Vec3d.ZERO, new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 1.0, 0.0));

    public SoundListenerTransform(Vec3d position, Vec3d forward, Vec3d up) {
        this.position = position;
        this.forward = forward;
        this.up = up;
    }

    public Vec3d right() {
        return this.forward.crossProduct(this.up);
    }

    public Vec3d position() {
        return this.position;
    }

    public Vec3d forward() {
        return this.forward;
    }

    public Vec3d up() {
        return this.up;
    }
}

