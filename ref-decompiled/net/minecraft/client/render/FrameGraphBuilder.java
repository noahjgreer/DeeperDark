package net.minecraft.client.render;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.ObjectAllocator;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class FrameGraphBuilder {
   private final List resourceNodes = new ArrayList();
   private final List objectNodes = new ArrayList();
   private final List passes = new ArrayList();

   public FramePass createPass(String name) {
      FramePassImpl framePassImpl = new FramePassImpl(this.passes.size(), name);
      this.passes.add(framePassImpl);
      return framePassImpl;
   }

   public net.minecraft.client.util.Handle createObjectNode(String name, Object object) {
      ObjectNode objectNode = new ObjectNode(name, (FramePassImpl)null, object);
      this.objectNodes.add(objectNode);
      return objectNode.handle;
   }

   public net.minecraft.client.util.Handle createResourceHandle(String name, ClosableFactory factory) {
      return this.createResourceNode(name, factory, (FramePassImpl)null).handle;
   }

   ResourceNode createResourceNode(String name, ClosableFactory factory, @Nullable FramePassImpl stageNode) {
      int i = this.resourceNodes.size();
      ResourceNode resourceNode = new ResourceNode(i, name, stageNode, factory);
      this.resourceNodes.add(resourceNode);
      return resourceNode;
   }

   public void run(ObjectAllocator allocator) {
      this.run(allocator, FrameGraphBuilder.Profiler.NONE);
   }

   public void run(ObjectAllocator allocator, Profiler profiler) {
      BitSet bitSet = this.collectPassesToVisit();
      List list = new ArrayList(bitSet.cardinality());
      BitSet bitSet2 = new BitSet(this.passes.size());
      Iterator var6 = this.passes.iterator();

      FramePassImpl framePassImpl;
      while(var6.hasNext()) {
         framePassImpl = (FramePassImpl)var6.next();
         this.visit(framePassImpl, bitSet, bitSet2, list);
      }

      this.checkResources(list);
      var6 = list.iterator();

      while(var6.hasNext()) {
         framePassImpl = (FramePassImpl)var6.next();
         Iterator var8 = framePassImpl.resourcesToAcquire.iterator();

         ResourceNode resourceNode;
         while(var8.hasNext()) {
            resourceNode = (ResourceNode)var8.next();
            profiler.acquire(resourceNode.name);
            resourceNode.acquire(allocator);
         }

         profiler.push(framePassImpl.name);
         framePassImpl.renderer.run();
         profiler.pop(framePassImpl.name);

         for(int i = framePassImpl.resourcesToRelease.nextSetBit(0); i >= 0; i = framePassImpl.resourcesToRelease.nextSetBit(i + 1)) {
            resourceNode = (ResourceNode)this.resourceNodes.get(i);
            profiler.release(resourceNode.name);
            resourceNode.release(allocator);
         }
      }

   }

   private BitSet collectPassesToVisit() {
      Deque deque = new ArrayDeque(this.passes.size());
      BitSet bitSet = new BitSet(this.passes.size());
      Iterator var3 = this.objectNodes.iterator();

      while(var3.hasNext()) {
         Node node = (Node)var3.next();
         FramePassImpl framePassImpl = node.handle.from;
         if (framePassImpl != null) {
            this.markForVisit(framePassImpl, bitSet, deque);
         }
      }

      var3 = this.passes.iterator();

      while(var3.hasNext()) {
         FramePassImpl framePassImpl2 = (FramePassImpl)var3.next();
         if (framePassImpl2.toBeVisited) {
            this.markForVisit(framePassImpl2, bitSet, deque);
         }
      }

      return bitSet;
   }

   private void markForVisit(FramePassImpl pass, BitSet result, Deque deque) {
      deque.add(pass);

      while(true) {
         FramePassImpl framePassImpl;
         do {
            if (deque.isEmpty()) {
               return;
            }

            framePassImpl = (FramePassImpl)deque.poll();
         } while(result.get(framePassImpl.id));

         result.set(framePassImpl.id);

         for(int i = framePassImpl.requiredPassIds.nextSetBit(0); i >= 0; i = framePassImpl.requiredPassIds.nextSetBit(i + 1)) {
            deque.add((FramePassImpl)this.passes.get(i));
         }
      }
   }

   private void visit(FramePassImpl node, BitSet unvisited, BitSet visiting, List topologicalOrderOut) {
      if (visiting.get(node.id)) {
         String string = (String)visiting.stream().mapToObj((id) -> {
            return ((FramePassImpl)this.passes.get(id)).name;
         }).collect(Collectors.joining(", "));
         throw new IllegalStateException("Frame graph cycle detected between " + string);
      } else if (unvisited.get(node.id)) {
         visiting.set(node.id);
         unvisited.clear(node.id);

         for(int i = node.requiredPassIds.nextSetBit(0); i >= 0; i = node.requiredPassIds.nextSetBit(i + 1)) {
            this.visit((FramePassImpl)this.passes.get(i), unvisited, visiting, topologicalOrderOut);
         }

         Iterator var8 = node.transferredHandles.iterator();

         while(var8.hasNext()) {
            Handle handle = (Handle)var8.next();

            for(int j = handle.dependents.nextSetBit(0); j >= 0; j = handle.dependents.nextSetBit(j + 1)) {
               if (j != node.id) {
                  this.visit((FramePassImpl)this.passes.get(j), unvisited, visiting, topologicalOrderOut);
               }
            }
         }

         topologicalOrderOut.add(node);
         visiting.clear(node.id);
      }
   }

   private void checkResources(Collection passes) {
      FramePassImpl[] framePassImpls = new FramePassImpl[this.resourceNodes.size()];
      Iterator var3 = passes.iterator();

      while(var3.hasNext()) {
         FramePassImpl framePassImpl = (FramePassImpl)var3.next();

         for(int i = framePassImpl.requiredResourceIds.nextSetBit(0); i >= 0; i = framePassImpl.requiredResourceIds.nextSetBit(i + 1)) {
            ResourceNode resourceNode = (ResourceNode)this.resourceNodes.get(i);
            FramePassImpl framePassImpl2 = framePassImpls[i];
            framePassImpls[i] = framePassImpl;
            if (framePassImpl2 == null) {
               framePassImpl.resourcesToAcquire.add(resourceNode);
            } else {
               framePassImpl2.resourcesToRelease.clear(i);
            }

            framePassImpl.resourcesToRelease.set(i);
         }
      }

   }

   @Environment(EnvType.CLIENT)
   private class FramePassImpl implements FramePass {
      final int id;
      final String name;
      final List transferredHandles = new ArrayList();
      final BitSet requiredResourceIds = new BitSet();
      final BitSet requiredPassIds = new BitSet();
      Runnable renderer = () -> {
      };
      final List resourcesToAcquire = new ArrayList();
      final BitSet resourcesToRelease = new BitSet();
      boolean toBeVisited;

      public FramePassImpl(final int id, final String name) {
         this.id = id;
         this.name = name;
      }

      private void addRequired(Handle handle) {
         Node var3 = handle.parent;
         if (var3 instanceof ResourceNode resourceNode) {
            this.requiredResourceIds.set(resourceNode.id);
         }

      }

      private void addRequired(FramePassImpl child) {
         this.requiredPassIds.set(child.id);
      }

      public net.minecraft.client.util.Handle addRequiredResource(String name, ClosableFactory factory) {
         ResourceNode resourceNode = FrameGraphBuilder.this.createResourceNode(name, factory, this);
         this.requiredResourceIds.set(resourceNode.id);
         return resourceNode.handle;
      }

      public void dependsOn(net.minecraft.client.util.Handle handle) {
         this.dependsOn((Handle)handle);
      }

      private void dependsOn(Handle handle) {
         this.addRequired(handle);
         if (handle.from != null) {
            this.addRequired(handle.from);
         }

         handle.dependents.set(this.id);
      }

      public net.minecraft.client.util.Handle transfer(net.minecraft.client.util.Handle handle) {
         return this.transfer((Handle)handle);
      }

      public void addRequired(FramePass pass) {
         this.requiredPassIds.set(((FramePassImpl)pass).id);
      }

      public void markToBeVisited() {
         this.toBeVisited = true;
      }

      private Handle transfer(Handle handle) {
         this.transferredHandles.add(handle);
         this.dependsOn(handle);
         return handle.moveTo(this);
      }

      public void setRenderer(Runnable renderer) {
         this.renderer = renderer;
      }

      public String toString() {
         return this.name;
      }
   }

   @Environment(EnvType.CLIENT)
   private static class ObjectNode extends Node {
      private final Object value;

      public ObjectNode(String name, @Nullable FramePassImpl parent, Object value) {
         super(name, parent);
         this.value = value;
      }

      public Object get() {
         return this.value;
      }
   }

   @Environment(EnvType.CLIENT)
   private static class Handle implements net.minecraft.client.util.Handle {
      final Node parent;
      private final int id;
      @Nullable
      final FramePassImpl from;
      final BitSet dependents = new BitSet();
      @Nullable
      private Handle movedTo;

      Handle(Node parent, int id, @Nullable FramePassImpl from) {
         this.parent = parent;
         this.id = id;
         this.from = from;
      }

      public Object get() {
         return this.parent.get();
      }

      Handle moveTo(FramePassImpl pass) {
         if (this.parent.handle != this) {
            String var10002 = String.valueOf(this);
            throw new IllegalStateException("Handle " + var10002 + " is no longer valid, as its contents were moved into " + String.valueOf(this.movedTo));
         } else {
            Handle handle = new Handle(this.parent, this.id + 1, pass);
            this.parent.handle = handle;
            this.movedTo = handle;
            return handle;
         }
      }

      public String toString() {
         String var10000;
         if (this.from != null) {
            var10000 = String.valueOf(this.parent);
            return var10000 + "#" + this.id + " (from " + String.valueOf(this.from) + ")";
         } else {
            var10000 = String.valueOf(this.parent);
            return var10000 + "#" + this.id;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   static class ResourceNode extends Node {
      final int id;
      private final ClosableFactory factory;
      @Nullable
      private Object resource;

      public ResourceNode(int id, String name, @Nullable FramePassImpl from, ClosableFactory factory) {
         super(name, from);
         this.id = id;
         this.factory = factory;
      }

      public Object get() {
         return Objects.requireNonNull(this.resource, "Resource is not currently available");
      }

      public void acquire(ObjectAllocator allocator) {
         if (this.resource != null) {
            throw new IllegalStateException("Tried to acquire physical resource, but it was already assigned");
         } else {
            this.resource = allocator.acquire(this.factory);
         }
      }

      public void release(ObjectAllocator allocator) {
         if (this.resource == null) {
            throw new IllegalStateException("Tried to release physical resource that was not allocated");
         } else {
            allocator.release(this.factory, this.resource);
            this.resource = null;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public interface Profiler {
      Profiler NONE = new Profiler() {
      };

      default void acquire(String name) {
      }

      default void release(String name) {
      }

      default void push(String location) {
      }

      default void pop(String location) {
      }
   }

   @Environment(EnvType.CLIENT)
   abstract static class Node {
      public final String name;
      public Handle handle;

      public Node(String name, @Nullable FramePassImpl from) {
         this.name = name;
         this.handle = new Handle(this, 0, from);
      }

      public abstract Object get();

      public String toString() {
         return this.name;
      }
   }
}
