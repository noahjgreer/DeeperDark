/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class ModelPart.Vertex
extends Record {
    private final float x;
    private final float y;
    private final float z;
    final float u;
    final float v;
    public static final float SCALE_FACTOR = 16.0f;

    public ModelPart.Vertex(float x, float y, float z, float u, float v) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        this.v = v;
    }

    public ModelPart.Vertex remap(float u, float v) {
        return new ModelPart.Vertex(this.x, this.y, this.z, u, v);
    }

    public float worldX() {
        return this.x / 16.0f;
    }

    public float worldY() {
        return this.y / 16.0f;
    }

    public float worldZ() {
        return this.z / 16.0f;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelPart.Vertex.class, "x;y;z;u;v", "x", "y", "z", "u", "v"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelPart.Vertex.class, "x;y;z;u;v", "x", "y", "z", "u", "v"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelPart.Vertex.class, "x;y;z;u;v", "x", "y", "z", "u", "v"}, this, object);
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public float u() {
        return this.u;
    }

    public float v() {
        return this.v;
    }
}
