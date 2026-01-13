/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.CameraOverride
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record CameraOverride(Vector3fc forwardVector) {
    private final Vector3fc forwardVector;

    public CameraOverride(Vector3fc forwardVector) {
        this.forwardVector = forwardVector;
    }

    public Vector3fc forwardVector() {
        return this.forwardVector;
    }
}

