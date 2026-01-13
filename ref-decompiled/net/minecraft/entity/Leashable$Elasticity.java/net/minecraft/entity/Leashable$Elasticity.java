/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.util.List;
import net.minecraft.util.math.Vec3d;

public record Leashable.Elasticity(Vec3d force, double torque) {
    static Leashable.Elasticity ZERO = new Leashable.Elasticity(Vec3d.ZERO, 0.0);

    static double calculateTorque(Vec3d force, Vec3d force2) {
        return force.z * force2.x - force.x * force2.z;
    }

    static Leashable.Elasticity sumOf(List<Leashable.Elasticity> elasticities) {
        if (elasticities.isEmpty()) {
            return ZERO;
        }
        double d = 0.0;
        double e = 0.0;
        double f = 0.0;
        double g = 0.0;
        for (Leashable.Elasticity elasticity : elasticities) {
            Vec3d vec3d = elasticity.force;
            d += vec3d.x;
            e += vec3d.y;
            f += vec3d.z;
            g += elasticity.torque;
        }
        return new Leashable.Elasticity(new Vec3d(d, e, f), g);
    }

    public Leashable.Elasticity multiply(double value) {
        return new Leashable.Elasticity(this.force.multiply(value), this.torque * value);
    }
}
