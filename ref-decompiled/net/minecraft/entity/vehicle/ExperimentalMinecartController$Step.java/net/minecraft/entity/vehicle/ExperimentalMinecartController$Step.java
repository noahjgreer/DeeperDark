/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.entity.vehicle;

import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.Vec3d;

public static final class ExperimentalMinecartController.Step
extends Record {
    final Vec3d position;
    final Vec3d movement;
    final float yRot;
    final float xRot;
    final float weight;
    public static final PacketCodec<ByteBuf, ExperimentalMinecartController.Step> PACKET_CODEC = PacketCodec.tuple(Vec3d.PACKET_CODEC, ExperimentalMinecartController.Step::position, Vec3d.PACKET_CODEC, ExperimentalMinecartController.Step::movement, PacketCodecs.DEGREES, ExperimentalMinecartController.Step::yRot, PacketCodecs.DEGREES, ExperimentalMinecartController.Step::xRot, PacketCodecs.FLOAT, ExperimentalMinecartController.Step::weight, ExperimentalMinecartController.Step::new);
    public static ExperimentalMinecartController.Step ZERO = new ExperimentalMinecartController.Step(Vec3d.ZERO, Vec3d.ZERO, 0.0f, 0.0f, 0.0f);

    public ExperimentalMinecartController.Step(Vec3d position, Vec3d movement, float yRot, float xRot, float weight) {
        this.position = position;
        this.movement = movement;
        this.yRot = yRot;
        this.xRot = xRot;
        this.weight = weight;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ExperimentalMinecartController.Step.class, "position;movement;yRot;xRot;weight", "position", "movement", "yRot", "xRot", "weight"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ExperimentalMinecartController.Step.class, "position;movement;yRot;xRot;weight", "position", "movement", "yRot", "xRot", "weight"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ExperimentalMinecartController.Step.class, "position;movement;yRot;xRot;weight", "position", "movement", "yRot", "xRot", "weight"}, this, object);
    }

    public Vec3d position() {
        return this.position;
    }

    public Vec3d movement() {
        return this.movement;
    }

    public float yRot() {
        return this.yRot;
    }

    public float xRot() {
        return this.xRot;
    }

    public float weight() {
        return this.weight;
    }
}
