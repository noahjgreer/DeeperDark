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
static class FrameGraphBuilder.ObjectNode<T>
extends FrameGraphBuilder.Node<T> {
    private final T value;

    public FrameGraphBuilder.ObjectNode(String name, @Nullable FrameGraphBuilder.FramePassImpl parent, T value) {
        super(name, parent);
        this.value = value;
    }

    @Override
    public T get() {
        return this.value;
    }
}
