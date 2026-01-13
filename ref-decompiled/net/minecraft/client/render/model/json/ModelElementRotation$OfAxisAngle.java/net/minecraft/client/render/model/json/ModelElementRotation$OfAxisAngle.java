/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementRotation;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record ModelElementRotation.OfAxisAngle(Direction.Axis axis, float angle) implements ModelElementRotation.RotationValue
{
    @Override
    public Matrix4f getMatrix() {
        Matrix4f matrix4f = new Matrix4f();
        if (this.angle == 0.0f) {
            return matrix4f;
        }
        Vector3fc vector3fc = this.axis.getPositiveDirection().getFloatVector();
        matrix4f.rotation(this.angle * ((float)Math.PI / 180), vector3fc);
        return matrix4f;
    }
}
