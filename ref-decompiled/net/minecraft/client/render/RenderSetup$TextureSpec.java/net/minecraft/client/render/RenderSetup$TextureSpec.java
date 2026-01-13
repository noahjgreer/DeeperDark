/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static final class RenderSetup.TextureSpec
extends Record {
    final Identifier location;
    private final Supplier<@Nullable GpuSampler> sampler;

    RenderSetup.TextureSpec(Identifier location, Supplier<@Nullable GpuSampler> sampler) {
        this.location = location;
        this.sampler = sampler;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RenderSetup.TextureSpec.class, "location;sampler", "location", "sampler"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RenderSetup.TextureSpec.class, "location;sampler", "location", "sampler"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RenderSetup.TextureSpec.class, "location;sampler", "location", "sampler"}, this, object);
    }

    public Identifier location() {
        return this.location;
    }

    public Supplier<@Nullable GpuSampler> sampler() {
        return this.sampler;
    }
}
