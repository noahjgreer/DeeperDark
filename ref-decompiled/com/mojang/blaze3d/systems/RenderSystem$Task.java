/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuFence;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class RenderSystem.Task
extends Record {
    final Runnable callback;
    final GpuFence fence;

    RenderSystem.Task(Runnable callback, GpuFence fence) {
        this.callback = callback;
        this.fence = fence;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RenderSystem.Task.class, "callback;fence", "callback", "fence"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RenderSystem.Task.class, "callback;fence", "callback", "fence"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RenderSystem.Task.class, "callback;fence", "callback", "fence"}, this, object);
    }

    public Runnable callback() {
        return this.callback;
    }

    public GpuFence fence() {
        return this.fence;
    }
}
