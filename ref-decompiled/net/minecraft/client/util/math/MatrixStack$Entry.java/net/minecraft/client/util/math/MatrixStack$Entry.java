/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MatrixUtil;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public static final class MatrixStack.Entry {
    private final Matrix4f positionMatrix = new Matrix4f();
    private final Matrix3f normalMatrix = new Matrix3f();
    private boolean canSkipNormalization = true;

    private void computeNormal() {
        this.normalMatrix.set((Matrix4fc)this.positionMatrix).invert().transpose();
        this.canSkipNormalization = false;
    }

    public void copy(MatrixStack.Entry entry) {
        this.positionMatrix.set((Matrix4fc)entry.positionMatrix);
        this.normalMatrix.set((Matrix3fc)entry.normalMatrix);
        this.canSkipNormalization = entry.canSkipNormalization;
    }

    public Matrix4f getPositionMatrix() {
        return this.positionMatrix;
    }

    public Matrix3f getNormalMatrix() {
        return this.normalMatrix;
    }

    public Vector3f transformNormal(Vector3fc vec, Vector3f dest) {
        return this.transformNormal(vec.x(), vec.y(), vec.z(), dest);
    }

    public Vector3f transformNormal(float x, float y, float z, Vector3f dest) {
        Vector3f vector3f = this.normalMatrix.transform(x, y, z, dest);
        return this.canSkipNormalization ? vector3f : vector3f.normalize();
    }

    public Matrix4f translate(float x, float y, float z) {
        return this.positionMatrix.translate(x, y, z);
    }

    public void scale(float x, float y, float z) {
        this.positionMatrix.scale(x, y, z);
        if (Math.abs(x) == Math.abs(y) && Math.abs(y) == Math.abs(z)) {
            if (x < 0.0f || y < 0.0f || z < 0.0f) {
                this.normalMatrix.scale(Math.signum(x), Math.signum(y), Math.signum(z));
            }
            return;
        }
        this.normalMatrix.scale(1.0f / x, 1.0f / y, 1.0f / z);
        this.canSkipNormalization = false;
    }

    public void rotate(Quaternionfc quaternion) {
        this.positionMatrix.rotate(quaternion);
        this.normalMatrix.rotate(quaternion);
    }

    public void rotateAround(Quaternionfc quaternion, float originX, float originY, float originZ) {
        this.positionMatrix.rotateAround(quaternion, originX, originY, originZ);
        this.normalMatrix.rotate(quaternion);
    }

    public void loadIdentity() {
        this.positionMatrix.identity();
        this.normalMatrix.identity();
        this.canSkipNormalization = true;
    }

    public void multiplyPositionMatrix(Matrix4fc matrix) {
        this.positionMatrix.mul(matrix);
        if (!MatrixUtil.isTranslation(matrix)) {
            if (MatrixUtil.isOrthonormal(matrix)) {
                this.normalMatrix.mul((Matrix3fc)new Matrix3f(matrix));
            } else {
                this.computeNormal();
            }
        }
    }

    public MatrixStack.Entry copy() {
        MatrixStack.Entry entry = new MatrixStack.Entry();
        entry.copy(this);
        return entry;
    }
}
