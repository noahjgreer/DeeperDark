/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.ProjectionType
 *  com.mojang.blaze3d.systems.ProjectionType$Applier
 *  com.mojang.blaze3d.systems.VertexSorter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class ProjectionType
extends Enum<ProjectionType> {
    public static final /* enum */ ProjectionType PERSPECTIVE = new ProjectionType("PERSPECTIVE", 0, VertexSorter.BY_DISTANCE, (matrix, direction) -> matrix.scale(1.0f - direction / 4096.0f));
    public static final /* enum */ ProjectionType ORTHOGRAPHIC = new ProjectionType("ORTHOGRAPHIC", 1, VertexSorter.BY_Z, (matrix, direction) -> matrix.translate(0.0f, 0.0f, direction / 512.0f));
    private final VertexSorter vertexSorter;
    private final Applier applier;
    private static final /* synthetic */ ProjectionType[] field_54957;

    public static ProjectionType[] values() {
        return (ProjectionType[])field_54957.clone();
    }

    public static ProjectionType valueOf(String string) {
        return Enum.valueOf(ProjectionType.class, string);
    }

    private ProjectionType(VertexSorter vertexSorter, Applier applier) {
        this.vertexSorter = vertexSorter;
        this.applier = applier;
    }

    public VertexSorter getVertexSorter() {
        return this.vertexSorter;
    }

    public void apply(Matrix4f matrix, float direction) {
        this.applier.apply(matrix, direction);
    }

    private static /* synthetic */ ProjectionType[] method_65047() {
        return new ProjectionType[]{PERSPECTIVE, ORTHOGRAPHIC};
    }

    static {
        field_54957 = ProjectionType.method_65047();
    }
}

