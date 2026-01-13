/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;

@Environment(value=EnvType.CLIENT)
public static final class BillboardParticleSubmittable.Buffers
extends Record {
    final int indexCount;
    final GpuBufferSlice dynamicTransforms;
    final Map<BillboardParticle.RenderType, BillboardParticleSubmittable.Layer> layers;

    public BillboardParticleSubmittable.Buffers(int indexCount, GpuBufferSlice dynamicTransforms, Map<BillboardParticle.RenderType, BillboardParticleSubmittable.Layer> layers) {
        this.indexCount = indexCount;
        this.dynamicTransforms = dynamicTransforms;
        this.layers = layers;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BillboardParticleSubmittable.Buffers.class, "indexCount;dynamicTransforms;layers", "indexCount", "dynamicTransforms", "layers"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BillboardParticleSubmittable.Buffers.class, "indexCount;dynamicTransforms;layers", "indexCount", "dynamicTransforms", "layers"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BillboardParticleSubmittable.Buffers.class, "indexCount;dynamicTransforms;layers", "indexCount", "dynamicTransforms", "layers"}, this, object);
    }

    public int indexCount() {
        return this.indexCount;
    }

    public GpuBufferSlice dynamicTransforms() {
        return this.dynamicTransforms;
    }

    public Map<BillboardParticle.RenderType, BillboardParticleSubmittable.Layer> layers() {
        return this.layers;
    }
}
