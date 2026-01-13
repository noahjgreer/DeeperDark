/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.CubeFace;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record CubeFace.Corner(CubeFace.CornerCoord xSide, CubeFace.CornerCoord ySide, CubeFace.CornerCoord zSide) {
    public Vector3f get(Vector3fc from, Vector3fc to) {
        return new Vector3f(this.xSide.get(from, to), this.ySide.get(from, to), this.zSide.get(from, to));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CubeFace.Corner.class, "xFace;yFace;zFace", "xSide", "ySide", "zSide"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CubeFace.Corner.class, "xFace;yFace;zFace", "xSide", "ySide", "zSide"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CubeFace.Corner.class, "xFace;yFace;zFace", "xSide", "ySide", "zSide"}, this, object);
    }
}
