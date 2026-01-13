/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import java.util.BitSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.util.Handle;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class FrameGraphBuilder.Handle<T>
implements Handle<T> {
    final FrameGraphBuilder.Node<T> parent;
    private final int id;
    final @Nullable FrameGraphBuilder.FramePassImpl from;
    final BitSet dependents = new BitSet();
    private @Nullable FrameGraphBuilder.Handle<T> movedTo;

    FrameGraphBuilder.Handle(FrameGraphBuilder.Node<T> parent, int id, @Nullable FrameGraphBuilder.FramePassImpl from) {
        this.parent = parent;
        this.id = id;
        this.from = from;
    }

    @Override
    public T get() {
        return this.parent.get();
    }

    FrameGraphBuilder.Handle<T> moveTo(FrameGraphBuilder.FramePassImpl pass) {
        if (this.parent.handle != this) {
            throw new IllegalStateException("Handle " + String.valueOf(this) + " is no longer valid, as its contents were moved into " + String.valueOf(this.movedTo));
        }
        FrameGraphBuilder.Handle<T> handle = new FrameGraphBuilder.Handle<T>(this.parent, this.id + 1, pass);
        this.parent.handle = handle;
        this.movedTo = handle;
        return handle;
    }

    public String toString() {
        if (this.from != null) {
            return String.valueOf(this.parent) + "#" + this.id + " (from " + String.valueOf(this.from) + ")";
        }
        return String.valueOf(this.parent) + "#" + this.id;
    }
}
