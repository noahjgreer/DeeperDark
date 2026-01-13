/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import net.minecraft.util.math.Vec3d;

static class PositionInterpolator.Data {
    protected int step;
    Vec3d pos;
    float yaw;
    float pitch;

    PositionInterpolator.Data(int step, Vec3d pos, float yaw, float pitch) {
        this.step = step;
        this.pos = pos;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void tick() {
        --this.step;
    }

    public void addPos(Vec3d pos) {
        this.pos = this.pos.add(pos);
    }

    public void addRotation(float yaw, float pitch) {
        this.yaw += yaw;
        this.pitch += pitch;
    }
}
