/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class BillboardParticleSubmittable.Layer
extends Record {
    final int vertexOffset;
    final int indexCount;

    public BillboardParticleSubmittable.Layer(int vertexOffset, int indexCount) {
        this.vertexOffset = vertexOffset;
        this.indexCount = indexCount;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BillboardParticleSubmittable.Layer.class, "vertexOffset;indexCount", "vertexOffset", "indexCount"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BillboardParticleSubmittable.Layer.class, "vertexOffset;indexCount", "vertexOffset", "indexCount"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BillboardParticleSubmittable.Layer.class, "vertexOffset;indexCount", "vertexOffset", "indexCount"}, this, object);
    }

    public int vertexOffset() {
        return this.vertexOffset;
    }

    public int indexCount() {
        return this.indexCount;
    }
}
