/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
record ModelRotation.UVModel(ModelRotation parent) implements ModelBakeSettings
{
    @Override
    public AffineTransformation getRotation() {
        return this.parent.rotation;
    }

    @Override
    public Matrix4fc forward(Direction facing) {
        return this.parent.faces.getOrDefault(facing, TRANSFORM_NONE);
    }

    @Override
    public Matrix4fc reverse(Direction facing) {
        return this.parent.invertedFaces.getOrDefault(facing, TRANSFORM_NONE);
    }

    @Override
    public String toString() {
        return "uvLocked[" + this.parent.transformation.asString() + "]";
    }
}
