/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static interface FrameGraphBuilder.Profiler {
    public static final FrameGraphBuilder.Profiler NONE = new FrameGraphBuilder.Profiler(){};

    default public void acquire(String name) {
    }

    default public void release(String name) {
    }

    default public void push(String location) {
    }

    default public void pop(String location) {
    }
}
