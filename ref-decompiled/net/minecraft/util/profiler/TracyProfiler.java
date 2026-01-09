package net.minecraft.util.profiler;

import com.mojang.jtracy.Plot;
import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import com.mojang.logging.LogUtils;
import java.lang.StackWalker.Option;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import org.slf4j.Logger;

public class TracyProfiler implements Profiler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final StackWalker STACK_WALKER;
   private final List zones = new ArrayList();
   private final Map markers = new HashMap();
   private final String threadName = Thread.currentThread().getName();

   public void startTick() {
   }

   public void endTick() {
      Iterator var1 = this.markers.values().iterator();

      while(var1.hasNext()) {
         Marker marker = (Marker)var1.next();
         marker.setCount(0);
      }

   }

   public void push(String location) {
      String string = "";
      String string2 = "";
      int i = 0;
      if (SharedConstants.isDevelopment) {
         Optional optional = (Optional)STACK_WALKER.walk((stream) -> {
            return stream.filter((frame) -> {
               return frame.getDeclaringClass() != TracyProfiler.class && frame.getDeclaringClass() != Profiler.UnionProfiler.class;
            }).findFirst();
         });
         if (optional.isPresent()) {
            StackWalker.StackFrame stackFrame = (StackWalker.StackFrame)optional.get();
            string = stackFrame.getMethodName();
            string2 = stackFrame.getFileName();
            i = stackFrame.getLineNumber();
         }
      }

      Zone zone = TracyClient.beginZone(location, string, string2, i);
      this.zones.add(zone);
   }

   public void push(Supplier locationGetter) {
      this.push((String)locationGetter.get());
   }

   public void pop() {
      if (this.zones.isEmpty()) {
         LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
      } else {
         Zone zone = (Zone)this.zones.removeLast();
         zone.close();
      }
   }

   public void swap(String location) {
      this.pop();
      this.push(location);
   }

   public void swap(Supplier locationGetter) {
      this.pop();
      this.push((String)locationGetter.get());
   }

   public void markSampleType(SampleType type) {
   }

   public void visit(String marker, int num) {
      ((Marker)this.markers.computeIfAbsent(marker, (markerName) -> {
         return new Marker(this.threadName + " " + marker);
      })).increment(num);
   }

   public void visit(Supplier markerGetter, int num) {
      this.visit((String)markerGetter.get(), num);
   }

   private Zone getCurrentZone() {
      return (Zone)this.zones.getLast();
   }

   public void addZoneText(String label) {
      this.getCurrentZone().addText(label);
   }

   public void addZoneValue(long value) {
      this.getCurrentZone().addValue(value);
   }

   public void setZoneColor(int color) {
      this.getCurrentZone().setColor(color);
   }

   static {
      STACK_WALKER = StackWalker.getInstance(Set.of(Option.RETAIN_CLASS_REFERENCE), 5);
   }

   private static final class Marker {
      private final Plot plot;
      private int count;

      Marker(String name) {
         this.plot = TracyClient.createPlot(name);
         this.count = 0;
      }

      void setCount(int count) {
         this.count = count;
         this.plot.setValue((double)count);
      }

      void increment(int count) {
         this.setCount(this.count + count);
      }
   }
}
