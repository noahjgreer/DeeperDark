package net.minecraft.util.profiler;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class DummyProfiler implements ReadableProfiler {
   public static final DummyProfiler INSTANCE = new DummyProfiler();

   private DummyProfiler() {
   }

   public void startTick() {
   }

   public void endTick() {
   }

   public void push(String location) {
   }

   public void push(Supplier locationGetter) {
   }

   public void markSampleType(SampleType type) {
   }

   public void pop() {
   }

   public void swap(String location) {
   }

   public void swap(Supplier locationGetter) {
   }

   public ScopedProfiler scoped(String name) {
      return ScopedProfiler.DUMMY;
   }

   public ScopedProfiler scoped(Supplier nameSupplier) {
      return ScopedProfiler.DUMMY;
   }

   public void visit(String marker, int num) {
   }

   public void visit(Supplier markerGetter, int num) {
   }

   public ProfileResult getResult() {
      return EmptyProfileResult.INSTANCE;
   }

   @Nullable
   public ProfilerSystem.LocatedInfo getInfo(String name) {
      return null;
   }

   public Set getSampleTargets() {
      return ImmutableSet.of();
   }
}
