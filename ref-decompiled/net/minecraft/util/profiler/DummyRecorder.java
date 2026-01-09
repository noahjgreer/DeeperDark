package net.minecraft.util.profiler;

public class DummyRecorder implements Recorder {
   public static final Recorder INSTANCE = new DummyRecorder();

   public void stop() {
   }

   public void forceStop() {
   }

   public void startTick() {
   }

   public boolean isActive() {
      return false;
   }

   public Profiler getProfiler() {
      return DummyProfiler.INSTANCE;
   }

   public void endTick() {
   }
}
