package net.minecraft.util.profiler;

public enum SampleType {
   PATH_FINDING("pathfinding"),
   EVENT_LOOPS("event-loops"),
   CONSECUTIVE_EXECUTORS("consecutive-executors"),
   TICK_LOOP("ticking"),
   JVM("jvm"),
   CHUNK_RENDERING("chunk rendering"),
   CHUNK_RENDERING_DISPATCHING("chunk rendering dispatching"),
   CPU("cpu"),
   GPU("gpu");

   private final String name;

   private SampleType(final String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   // $FF: synthetic method
   private static SampleType[] method_36594() {
      return new SampleType[]{PATH_FINDING, EVENT_LOOPS, CONSECUTIVE_EXECUTORS, TICK_LOOP, JVM, CHUNK_RENDERING, CHUNK_RENDERING_DISPATCHING, CPU, GPU};
   }
}
