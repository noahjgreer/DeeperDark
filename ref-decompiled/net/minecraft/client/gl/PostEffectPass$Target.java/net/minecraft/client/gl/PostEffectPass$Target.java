/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GpuSampler;

@Environment(value=EnvType.CLIENT)
static final class PostEffectPass.Target
extends Record {
    private final String samplerName;
    final GpuTextureView view;
    private final GpuSampler sampler;

    PostEffectPass.Target(String samplerName, GpuTextureView view, GpuSampler sampler) {
        this.samplerName = samplerName;
        this.view = view;
        this.sampler = sampler;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PostEffectPass.Target.class, "samplerName;view;sampler", "samplerName", "view", "sampler"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PostEffectPass.Target.class, "samplerName;view;sampler", "samplerName", "view", "sampler"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PostEffectPass.Target.class, "samplerName;view;sampler", "samplerName", "view", "sampler"}, this, object);
    }

    public String samplerName() {
        return this.samplerName;
    }

    public GpuTextureView view() {
        return this.view;
    }

    public GpuSampler sampler() {
        return this.sampler;
    }
}
