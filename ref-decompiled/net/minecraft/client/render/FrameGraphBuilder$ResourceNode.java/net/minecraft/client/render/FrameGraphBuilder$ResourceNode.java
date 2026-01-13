/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.ObjectAllocator;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class FrameGraphBuilder.ResourceNode<T>
extends FrameGraphBuilder.Node<T> {
    final int id;
    private final ClosableFactory<T> factory;
    private @Nullable T resource;

    public FrameGraphBuilder.ResourceNode(int id, String name, @Nullable FrameGraphBuilder.FramePassImpl from, ClosableFactory<T> factory) {
        super(name, from);
        this.id = id;
        this.factory = factory;
    }

    @Override
    public T get() {
        return Objects.requireNonNull(this.resource, "Resource is not currently available");
    }

    public void acquire(ObjectAllocator allocator) {
        if (this.resource != null) {
            throw new IllegalStateException("Tried to acquire physical resource, but it was already assigned");
        }
        this.resource = allocator.acquire(this.factory);
    }

    public void release(ObjectAllocator allocator) {
        if (this.resource == null) {
            throw new IllegalStateException("Tried to release physical resource that was not allocated");
        }
        allocator.release(this.factory, this.resource);
        this.resource = null;
    }
}
