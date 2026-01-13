/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.ModelBakeSettings
 *  net.minecraft.util.math.AffineTransformation
 *  net.minecraft.util.math.Direction
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public interface ModelBakeSettings {
    public static final Matrix4fc TRANSFORM_NONE = new Matrix4f();

    default public AffineTransformation getRotation() {
        return AffineTransformation.identity();
    }

    default public Matrix4fc forward(Direction facing) {
        return TRANSFORM_NONE;
    }

    default public Matrix4fc reverse(Direction facing) {
        return TRANSFORM_NONE;
    }
}

