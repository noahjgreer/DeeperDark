/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FrameGraphBuilder;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static abstract class FrameGraphBuilder.Node<T> {
    public final String name;
    public FrameGraphBuilder.Handle<T> handle;

    public FrameGraphBuilder.Node(String name, @Nullable FrameGraphBuilder.FramePassImpl from) {
        this.name = name;
        this.handle = new FrameGraphBuilder.Handle(this, 0, from);
    }

    public abstract T get();

    public String toString() {
        return this.name;
    }
}
