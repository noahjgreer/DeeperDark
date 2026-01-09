package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record ServerTickTimeSample(Instant time, Duration averageTickMs) {
   public ServerTickTimeSample(Instant instant, Duration duration) {
      this.time = instant;
      this.averageTickMs = duration;
   }

   public static ServerTickTimeSample fromEvent(RecordedEvent event) {
      return new ServerTickTimeSample(event.getStartTime(), event.getDuration("averageTickDuration"));
   }

   public Instant time() {
      return this.time;
   }

   public Duration averageTickMs() {
      return this.averageTickMs;
   }
}
