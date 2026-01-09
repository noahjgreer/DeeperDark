package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ProfilerSystem implements ReadableProfiler {
   private static final long TIMEOUT_NANOSECONDS = Duration.ofMillis(100L).toNanos();
   private static final Logger LOGGER = LogUtils.getLogger();
   private final List path = Lists.newArrayList();
   private final LongList timeList = new LongArrayList();
   private final Map locationInfos = Maps.newHashMap();
   private final IntSupplier endTickGetter;
   private final LongSupplier timeGetter;
   private final long startTime;
   private final int startTick;
   private String fullPath = "";
   private boolean tickStarted;
   @Nullable
   private LocatedInfo currentInfo;
   private final BooleanSupplier timeoutDisabled;
   private final Set sampleTypes = new ObjectArraySet();

   public ProfilerSystem(LongSupplier timeGetter, IntSupplier tickGetter, BooleanSupplier timeoutDisabled) {
      this.startTime = timeGetter.getAsLong();
      this.timeGetter = timeGetter;
      this.startTick = tickGetter.getAsInt();
      this.endTickGetter = tickGetter;
      this.timeoutDisabled = timeoutDisabled;
   }

   public void startTick() {
      if (this.tickStarted) {
         LOGGER.error("Profiler tick already started - missing endTick()?");
      } else {
         this.tickStarted = true;
         this.fullPath = "";
         this.path.clear();
         this.push("root");
      }
   }

   public void endTick() {
      if (!this.tickStarted) {
         LOGGER.error("Profiler tick already ended - missing startTick()?");
      } else {
         this.pop();
         this.tickStarted = false;
         if (!this.fullPath.isEmpty()) {
            LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", LogUtils.defer(() -> {
               return ProfileResult.getHumanReadableName(this.fullPath);
            }));
         }

      }
   }

   public void push(String location) {
      if (!this.tickStarted) {
         LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", location);
      } else {
         if (!this.fullPath.isEmpty()) {
            this.fullPath = this.fullPath + "\u001e";
         }

         this.fullPath = this.fullPath + location;
         this.path.add(this.fullPath);
         this.timeList.add(Util.getMeasuringTimeNano());
         this.currentInfo = null;
      }
   }

   public void push(Supplier locationGetter) {
      this.push((String)locationGetter.get());
   }

   public void markSampleType(SampleType type) {
      this.sampleTypes.add(Pair.of(this.fullPath, type));
   }

   public void pop() {
      if (!this.tickStarted) {
         LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
      } else if (this.timeList.isEmpty()) {
         LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
      } else {
         long l = Util.getMeasuringTimeNano();
         long m = this.timeList.removeLong(this.timeList.size() - 1);
         this.path.remove(this.path.size() - 1);
         long n = l - m;
         LocatedInfo locatedInfo = this.getCurrentInfo();
         locatedInfo.totalTime += n;
         ++locatedInfo.visits;
         locatedInfo.maxTime = Math.max(locatedInfo.maxTime, n);
         locatedInfo.minTime = Math.min(locatedInfo.minTime, n);
         if (n > TIMEOUT_NANOSECONDS && !this.timeoutDisabled.getAsBoolean()) {
            LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", LogUtils.defer(() -> {
               return ProfileResult.getHumanReadableName(this.fullPath);
            }), LogUtils.defer(() -> {
               return (double)n / 1000000.0;
            }));
         }

         this.fullPath = this.path.isEmpty() ? "" : (String)this.path.get(this.path.size() - 1);
         this.currentInfo = null;
      }
   }

   public void swap(String location) {
      this.pop();
      this.push(location);
   }

   public void swap(Supplier locationGetter) {
      this.pop();
      this.push(locationGetter);
   }

   private LocatedInfo getCurrentInfo() {
      if (this.currentInfo == null) {
         this.currentInfo = (LocatedInfo)this.locationInfos.computeIfAbsent(this.fullPath, (k) -> {
            return new LocatedInfo();
         });
      }

      return this.currentInfo;
   }

   public void visit(String marker, int num) {
      this.getCurrentInfo().counts.addTo(marker, (long)num);
   }

   public void visit(Supplier markerGetter, int num) {
      this.getCurrentInfo().counts.addTo((String)markerGetter.get(), (long)num);
   }

   public ProfileResult getResult() {
      return new ProfileResultImpl(this.locationInfos, this.startTime, this.startTick, this.timeGetter.getAsLong(), this.endTickGetter.getAsInt());
   }

   @Nullable
   public LocatedInfo getInfo(String name) {
      return (LocatedInfo)this.locationInfos.get(name);
   }

   public Set getSampleTargets() {
      return this.sampleTypes;
   }

   public static class LocatedInfo implements ProfileLocationInfo {
      long maxTime = Long.MIN_VALUE;
      long minTime = Long.MAX_VALUE;
      long totalTime;
      long visits;
      final Object2LongOpenHashMap counts = new Object2LongOpenHashMap();

      public long getTotalTime() {
         return this.totalTime;
      }

      public long getMaxTime() {
         return this.maxTime;
      }

      public long getVisitCount() {
         return this.visits;
      }

      public Object2LongMap getCounts() {
         return Object2LongMaps.unmodifiable(this.counts);
      }
   }
}
