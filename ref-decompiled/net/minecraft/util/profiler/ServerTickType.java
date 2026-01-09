package net.minecraft.util.profiler;

public enum ServerTickType {
   FULL_TICK,
   TICK_SERVER_METHOD,
   SCHEDULED_TASKS,
   IDLE;

   // $FF: synthetic method
   private static ServerTickType[] method_56536() {
      return new ServerTickType[]{FULL_TICK, TICK_SERVER_METHOD, SCHEDULED_TASKS, IDLE};
   }
}
