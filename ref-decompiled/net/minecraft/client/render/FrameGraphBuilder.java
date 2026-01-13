/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.FrameGraphBuilder
 *  net.minecraft.client.render.FrameGraphBuilder$FramePassImpl
 *  net.minecraft.client.render.FrameGraphBuilder$Handle
 *  net.minecraft.client.render.FrameGraphBuilder$Node
 *  net.minecraft.client.render.FrameGraphBuilder$ObjectNode
 *  net.minecraft.client.render.FrameGraphBuilder$Profiler
 *  net.minecraft.client.render.FrameGraphBuilder$ResourceNode
 *  net.minecraft.client.render.FramePass
 *  net.minecraft.client.util.ClosableFactory
 *  net.minecraft.client.util.Handle
 *  net.minecraft.client.util.ObjectAllocator
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.ObjectAllocator;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class FrameGraphBuilder {
    private final List<ResourceNode<?>> resourceNodes = new ArrayList();
    private final List<ObjectNode<?>> objectNodes = new ArrayList();
    private final List<FramePassImpl> passes = new ArrayList();

    public FramePass createPass(String name) {
        FramePassImpl framePassImpl = new FramePassImpl(this, this.passes.size(), name);
        this.passes.add(framePassImpl);
        return framePassImpl;
    }

    public <T> net.minecraft.client.util.Handle<T> createObjectNode(String name, T object) {
        ObjectNode objectNode = new ObjectNode(name, null, object);
        this.objectNodes.add(objectNode);
        return objectNode.handle;
    }

    public <T> net.minecraft.client.util.Handle<T> createResourceHandle(String name, ClosableFactory<T> factory) {
        return this.createResourceNode((String)name, factory, null).handle;
    }

    <T> ResourceNode<T> createResourceNode(String name, ClosableFactory<T> factory, // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable FramePassImpl stageNode) {
        int i = this.resourceNodes.size();
        ResourceNode resourceNode = new ResourceNode(i, name, stageNode, factory);
        this.resourceNodes.add(resourceNode);
        return resourceNode;
    }

    public void run(ObjectAllocator allocator) {
        this.run(allocator, Profiler.NONE);
    }

    public void run(ObjectAllocator allocator, Profiler profiler) {
        BitSet bitSet = this.collectPassesToVisit();
        ArrayList list = new ArrayList(bitSet.cardinality());
        BitSet bitSet2 = new BitSet(this.passes.size());
        for (FramePassImpl framePassImpl : this.passes) {
            this.visit(framePassImpl, bitSet, bitSet2, list);
        }
        this.checkResources(list);
        for (FramePassImpl framePassImpl : list) {
            for (ResourceNode resourceNode : framePassImpl.resourcesToAcquire) {
                profiler.acquire(resourceNode.name);
                resourceNode.acquire(allocator);
            }
            profiler.push(framePassImpl.name);
            framePassImpl.renderer.run();
            profiler.pop(framePassImpl.name);
            int i = framePassImpl.resourcesToRelease.nextSetBit(0);
            while (i >= 0) {
                ResourceNode resourceNode;
                resourceNode = (ResourceNode)this.resourceNodes.get(i);
                profiler.release(resourceNode.name);
                resourceNode.release(allocator);
                i = framePassImpl.resourcesToRelease.nextSetBit(i + 1);
            }
        }
    }

    private BitSet collectPassesToVisit() {
        ArrayDeque deque = new ArrayDeque(this.passes.size());
        BitSet bitSet = new BitSet(this.passes.size());
        for (Node node : this.objectNodes) {
            FramePassImpl framePassImpl = node.handle.from;
            if (framePassImpl == null) continue;
            this.markForVisit(framePassImpl, bitSet, deque);
        }
        for (FramePassImpl framePassImpl2 : this.passes) {
            if (!framePassImpl2.toBeVisited) continue;
            this.markForVisit(framePassImpl2, bitSet, deque);
        }
        return bitSet;
    }

    private void markForVisit(FramePassImpl pass, BitSet result, Deque<FramePassImpl> deque) {
        deque.add(pass);
        while (!deque.isEmpty()) {
            FramePassImpl framePassImpl = deque.poll();
            if (result.get(framePassImpl.id)) continue;
            result.set(framePassImpl.id);
            int i = framePassImpl.requiredPassIds.nextSetBit(0);
            while (i >= 0) {
                deque.add((FramePassImpl)this.passes.get(i));
                i = framePassImpl.requiredPassIds.nextSetBit(i + 1);
            }
        }
    }

    private void visit(FramePassImpl node, BitSet unvisited, BitSet visiting, List<FramePassImpl> topologicalOrderOut) {
        if (visiting.get(node.id)) {
            String string = visiting.stream().mapToObj(id -> ((FramePassImpl)this.passes.get((int)id)).name).collect(Collectors.joining(", "));
            throw new IllegalStateException("Frame graph cycle detected between " + string);
        }
        if (!unvisited.get(node.id)) {
            return;
        }
        visiting.set(node.id);
        unvisited.clear(node.id);
        int i = node.requiredPassIds.nextSetBit(0);
        while (i >= 0) {
            this.visit((FramePassImpl)this.passes.get(i), unvisited, visiting, topologicalOrderOut);
            i = node.requiredPassIds.nextSetBit(i + 1);
        }
        for (Handle handle : node.transferredHandles) {
            int j = handle.dependents.nextSetBit(0);
            while (j >= 0) {
                if (j != node.id) {
                    this.visit((FramePassImpl)this.passes.get(j), unvisited, visiting, topologicalOrderOut);
                }
                j = handle.dependents.nextSetBit(j + 1);
            }
        }
        topologicalOrderOut.add(node);
        visiting.clear(node.id);
    }

    private void checkResources(Collection<FramePassImpl> passes) {
        // Could not load outer class - annotation placement on inner may be incorrect
        @Nullable FramePassImpl[] framePassImpls = new FramePassImpl[this.resourceNodes.size()];
        for (FramePassImpl framePassImpl : passes) {
            int i = framePassImpl.requiredResourceIds.nextSetBit(0);
            while (i >= 0) {
                ResourceNode resourceNode = (ResourceNode)this.resourceNodes.get(i);
                FramePassImpl framePassImpl2 = framePassImpls[i];
                framePassImpls[i] = framePassImpl;
                if (framePassImpl2 == null) {
                    framePassImpl.resourcesToAcquire.add(resourceNode);
                } else {
                    framePassImpl2.resourcesToRelease.clear(i);
                }
                framePassImpl.resourcesToRelease.set(i);
                i = framePassImpl.requiredResourceIds.nextSetBit(i + 1);
            }
        }
    }
}

