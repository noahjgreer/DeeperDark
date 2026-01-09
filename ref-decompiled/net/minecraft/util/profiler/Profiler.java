package net.minecraft.util.profiler;

import java.util.function.Supplier;

public interface Profiler {
   String ROOT_NAME = "root";

   void startTick();

   void endTick();

   void push(String location);

   void push(Supplier locationGetter);

   void pop();

   void swap(String location);

   void swap(Supplier locationGetter);

   default void addZoneText(String label) {
   }

   default void addZoneValue(long value) {
   }

   default void setZoneColor(int color) {
   }

   default ScopedProfiler scoped(String name) {
      this.push(name);
      return new ScopedProfiler(this);
   }

   default ScopedProfiler scoped(Supplier nameSupplier) {
      this.push(nameSupplier);
      return new ScopedProfiler(this);
   }

   void markSampleType(SampleType type);

   default void visit(String marker) {
      this.visit((String)marker, 1);
   }

   void visit(String marker, int num);

   default void visit(Supplier markerGetter) {
      this.visit((Supplier)markerGetter, 1);
   }

   void visit(Supplier markerGetter, int num);

   static Profiler union(Profiler first, Profiler second) {
      if (first == DummyProfiler.INSTANCE) {
         return second;
      } else {
         return (Profiler)(second == DummyProfiler.INSTANCE ? first : new UnionProfiler(first, second));
      }
   }

   public static class UnionProfiler implements Profiler {
      private final Profiler first;
      private final Profiler second;

      public UnionProfiler(Profiler first, Profiler second) {
         this.first = first;
         this.second = second;
      }

      public void startTick() {
         this.first.startTick();
         this.second.startTick();
      }

      public void endTick() {
         this.first.endTick();
         this.second.endTick();
      }

      public void push(String location) {
         this.first.push(location);
         this.second.push(location);
      }

      public void push(Supplier locationGetter) {
         this.first.push(locationGetter);
         this.second.push(locationGetter);
      }

      public void markSampleType(SampleType type) {
         this.first.markSampleType(type);
         this.second.markSampleType(type);
      }

      public void pop() {
         this.first.pop();
         this.second.pop();
      }

      public void swap(String location) {
         this.first.swap(location);
         this.second.swap(location);
      }

      public void swap(Supplier locationGetter) {
         this.first.swap(locationGetter);
         this.second.swap(locationGetter);
      }

      public void visit(String marker, int num) {
         this.first.visit(marker, num);
         this.second.visit(marker, num);
      }

      public void visit(Supplier markerGetter, int num) {
         this.first.visit(markerGetter, num);
         this.second.visit(markerGetter, num);
      }

      public void addZoneText(String label) {
         this.first.addZoneText(label);
         this.second.addZoneText(label);
      }

      public void addZoneValue(long value) {
         this.first.addZoneValue(value);
         this.second.addZoneValue(value);
      }

      public void setZoneColor(int color) {
         this.first.setZoneColor(color);
         this.second.setZoneColor(color);
      }
   }
}
