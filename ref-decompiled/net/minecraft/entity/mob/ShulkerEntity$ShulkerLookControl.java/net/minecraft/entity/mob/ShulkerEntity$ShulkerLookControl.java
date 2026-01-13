/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.entity.mob;

import java.util.Optional;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;
import org.joml.Vector3fc;

class ShulkerEntity.ShulkerLookControl
extends LookControl {
    public ShulkerEntity.ShulkerLookControl(MobEntity entity) {
        super(entity);
    }

    @Override
    protected void clampHeadYaw() {
    }

    @Override
    protected Optional<Float> getTargetYaw() {
        Direction direction = ShulkerEntity.this.getAttachedFace().getOpposite();
        Vector3f vector3f = direction.getRotationQuaternion().transform(new Vector3f((Vector3fc)SOUTH_VECTOR));
        Vec3i vec3i = direction.getVector();
        Vector3f vector3f2 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
        vector3f2.cross((Vector3fc)vector3f);
        double d = this.x - this.entity.getX();
        double e = this.y - this.entity.getEyeY();
        double f = this.z - this.entity.getZ();
        Vector3f vector3f3 = new Vector3f((float)d, (float)e, (float)f);
        float g = vector3f2.dot((Vector3fc)vector3f3);
        float h = vector3f.dot((Vector3fc)vector3f3);
        return Math.abs(g) > 1.0E-5f || Math.abs(h) > 1.0E-5f ? Optional.of(Float.valueOf((float)(MathHelper.atan2(-g, h) * 57.2957763671875))) : Optional.empty();
    }

    @Override
    protected Optional<Float> getTargetPitch() {
        return Optional.of(Float.valueOf(0.0f));
    }
}
