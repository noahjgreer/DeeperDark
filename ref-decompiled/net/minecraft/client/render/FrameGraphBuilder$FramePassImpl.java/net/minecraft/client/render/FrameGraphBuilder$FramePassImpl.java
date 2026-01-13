/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.Handle;

@Environment(value=EnvType.CLIENT)
class FrameGraphBuilder.FramePassImpl
implements FramePass {
    final int id;
    final String name;
    final List<FrameGraphBuilder.Handle<?>> transferredHandles = new ArrayList();
    final BitSet requiredResourceIds = new BitSet();
    final BitSet requiredPassIds = new BitSet();
    Runnable renderer = () -> {};
    final List<FrameGraphBuilder.ResourceNode<?>> resourcesToAcquire = new ArrayList();
    final BitSet resourcesToRelease = new BitSet();
    boolean toBeVisited;

    public FrameGraphBuilder.FramePassImpl(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private <T> void addRequired(FrameGraphBuilder.Handle<T> handle) {
        FrameGraphBuilder.Node node = handle.parent;
        if (node instanceof FrameGraphBuilder.ResourceNode) {
            FrameGraphBuilder.ResourceNode resourceNode = (FrameGraphBuilder.ResourceNode)node;
            this.requiredResourceIds.set(resourceNode.id);
        }
    }

    private void addRequired(FrameGraphBuilder.FramePassImpl child) {
        this.requiredPassIds.set(child.id);
    }

    @Override
    public <T> Handle<T> addRequiredResource(String name, ClosableFactory<T> factory) {
        FrameGraphBuilder.ResourceNode<T> resourceNode = FrameGraphBuilder.this.createResourceNode(name, factory, this);
        this.requiredResourceIds.set(resourceNode.id);
        return resourceNode.handle;
    }

    @Override
    public <T> void dependsOn(Handle<T> handle) {
        this.dependsOn((FrameGraphBuilder.Handle)handle);
    }

    private <T> void dependsOn(FrameGraphBuilder.Handle<T> handle) {
        this.addRequired(handle);
        if (handle.from != null) {
            this.addRequired(handle.from);
        }
        handle.dependents.set(this.id);
    }

    @Override
    public <T> Handle<T> transfer(Handle<T> handle) {
        return this.transfer((FrameGraphBuilder.Handle)handle);
    }

    @Override
    public void addRequired(FramePass pass) {
        this.requiredPassIds.set(((FrameGraphBuilder.FramePassImpl)pass).id);
    }

    @Override
    public void markToBeVisited() {
        this.toBeVisited = true;
    }

    private <T> FrameGraphBuilder.Handle<T> transfer(FrameGraphBuilder.Handle<T> handle) {
        this.transferredHandles.add(handle);
        this.dependsOn(handle);
        return handle.moveTo(this);
    }

    @Override
    public void setRenderer(Runnable renderer) {
        this.renderer = renderer;
    }

    public String toString() {
        return this.name;
    }
}
