/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.json.ModelElementRotation
 *  net.minecraft.client.render.model.json.ModelElementRotation$RotationValue
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.MatrixUtil
 *  org.joml.Math
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixUtil;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record ModelElementRotation(Vector3fc origin, RotationValue value, boolean rescale, Matrix4fc transform) {
    private final Vector3fc origin;
    private final RotationValue value;
    private final boolean rescale;
    private final Matrix4fc transform;

    public ModelElementRotation(Vector3fc origin, RotationValue value, boolean rescale) {
        this(origin, value, rescale, (Matrix4fc)ModelElementRotation.transform((RotationValue)value, (boolean)rescale));
    }

    public ModelElementRotation(Vector3fc origin, RotationValue value, boolean rescale, Matrix4fc transform) {
        this.origin = origin;
        this.value = value;
        this.rescale = rescale;
        this.transform = transform;
    }

    private static Matrix4f transform(RotationValue value, boolean rescale) {
        Matrix4f matrix4f = value.getMatrix();
        if (rescale && !MatrixUtil.isIdentity((Matrix4fc)matrix4f)) {
            Vector3fc vector3fc = ModelElementRotation.scale((Matrix4fc)matrix4f);
            matrix4f.scale(vector3fc);
        }
        return matrix4f;
    }

    private static Vector3fc scale(Matrix4fc matrix) {
        Vector3f vector3f = new Vector3f();
        float f = ModelElementRotation.scale((Matrix4fc)matrix, (Direction.Axis)Direction.Axis.X, (Vector3f)vector3f);
        float g = ModelElementRotation.scale((Matrix4fc)matrix, (Direction.Axis)Direction.Axis.Y, (Vector3f)vector3f);
        float h = ModelElementRotation.scale((Matrix4fc)matrix, (Direction.Axis)Direction.Axis.Z, (Vector3f)vector3f);
        return vector3f.set(f, g, h);
    }

    private static float scale(Matrix4fc matrix, Direction.Axis axis, Vector3f vec) {
        Vector3f vector3f = vec.set(axis.getPositiveDirection().getFloatVector());
        Vector3f vector3f2 = matrix.transformDirection(vector3f);
        float f = Math.abs((float)vector3f2.x);
        float g = Math.abs((float)vector3f2.y);
        float h = Math.abs((float)vector3f2.z);
        float i = Math.max((float)Math.max((float)f, (float)g), (float)h);
        return 1.0f / i;
    }

    public Vector3fc origin() {
        return this.origin;
    }

    public RotationValue value() {
        return this.value;
    }

    public boolean rescale() {
        return this.rescale;
    }

    public Matrix4fc transform() {
        return this.transform;
    }
}

