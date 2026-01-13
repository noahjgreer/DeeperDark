/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementRotation;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public record ModelElementRotation.OfEuler(float x, float y, float z) implements ModelElementRotation.RotationValue
{
    @Override
    public Matrix4f getMatrix() {
        return new Matrix4f().rotationZYX(this.z * ((float)Math.PI / 180), this.y * ((float)Math.PI / 180), this.x * ((float)Math.PI / 180));
    }
}
