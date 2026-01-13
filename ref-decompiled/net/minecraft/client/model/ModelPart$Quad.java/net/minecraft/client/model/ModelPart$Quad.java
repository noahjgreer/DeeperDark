/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 */
package net.minecraft.client.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Direction;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public static final class ModelPart.Quad
extends Record {
    final ModelPart.Vertex[] vertices;
    final Vector3fc direction;

    public ModelPart.Quad(ModelPart.Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean flip, Direction direction) {
        this(vertices, (flip ? ModelPart.Quad.getMirrorDirection(direction) : direction).getFloatVector());
        float f = 0.0f / squishU;
        float g = 0.0f / squishV;
        vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
        vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
        vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
        vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
        if (flip) {
            int i = vertices.length;
            for (int j = 0; j < i / 2; ++j) {
                ModelPart.Vertex vertex = vertices[j];
                vertices[j] = vertices[i - 1 - j];
                vertices[i - 1 - j] = vertex;
            }
        }
    }

    public ModelPart.Quad(ModelPart.Vertex[] vertices, Vector3fc direction) {
        this.vertices = vertices;
        this.direction = direction;
    }

    private static Direction getMirrorDirection(Direction direction) {
        return direction.getAxis() == Direction.Axis.X ? direction.getOpposite() : direction;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelPart.Quad.class, "vertices;normal", "vertices", "direction"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelPart.Quad.class, "vertices;normal", "vertices", "direction"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelPart.Quad.class, "vertices;normal", "vertices", "direction"}, this, object);
    }

    public ModelPart.Vertex[] vertices() {
        return this.vertices;
    }

    public Vector3fc direction() {
        return this.direction;
    }
}
