package net.minecraft.util.thread;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class SimpleConsecutiveExecutor extends ConsecutiveExecutor {
   public SimpleConsecutiveExecutor(Executor executor, String name) {
      super(new TaskQueue.Simple(new ConcurrentLinkedQueue()), executor, name);
   }

   public Runnable createTask(Runnable runnable) {
      return runnable;
   }
}
