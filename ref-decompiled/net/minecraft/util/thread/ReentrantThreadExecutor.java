package net.minecraft.util.thread;

public abstract class ReentrantThreadExecutor extends ThreadExecutor {
   private int runningTasks;

   public ReentrantThreadExecutor(String string) {
      super(string);
   }

   public boolean shouldExecuteAsync() {
      return this.hasRunningTasks() || super.shouldExecuteAsync();
   }

   protected boolean hasRunningTasks() {
      return this.runningTasks != 0;
   }

   public void executeTask(Runnable task) {
      ++this.runningTasks;

      try {
         super.executeTask(task);
      } finally {
         --this.runningTasks;
      }

   }
}
