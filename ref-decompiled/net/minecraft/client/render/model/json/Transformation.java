/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.json.Transformation
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record Transformation(Vector3fc rotation, Vector3fc translation, Vector3fc scale) {
    private final Vector3fc rotation;
    private final Vector3fc translation;
    private final Vector3fc scale;
    public static final Transformation IDENTITY = new Transformation((Vector3fc)new Vector3f(), (Vector3fc)new Vector3f(), (Vector3fc)new Vector3f(1.0f, 1.0f, 1.0f));

    public Transformation(Vector3fc rotation, Vector3fc translation, Vector3fc scale) {
        this.rotation = rotation;
        this.translation = translation;
        this.scale = scale;
    }

    public void apply(boolean leftHanded, MatrixStack.Entry entry) {
        float h;
        float g;
        float f;
        if (this == IDENTITY) {
            entry.translate(-0.5f, -0.5f, -0.5f);
            return;
        }
        if (leftHanded) {
            f = -this.translation.x();
            g = -this.rotation.y();
            h = -this.rotation.z();
        } else {
            f = this.translation.x();
            g = this.rotation.y();
            h = this.rotation.z();
        }
        entry.translate(f, this.translation.y(), this.translation.z());
        entry.rotate((Quaternionfc)new Quaternionf().rotationXYZ(this.rotation.x() * ((float)Math.PI / 180), g * ((float)Math.PI / 180), h * ((float)Math.PI / 180)));
        entry.scale(this.scale.x(), this.scale.y(), this.scale.z());
        entry.translate(-0.5f, -0.5f, -0.5f);
    }

    public Vector3fc rotation() {
        return this.rotation;
    }

    public Vector3fc translation() {
        return this.translation;
    }

    public Vector3fc scale() {
        return this.scale;
    }
}

