package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.text.Text;

public class TimedTaskRunner {
   final GameTestState test;
   private final List tasks = Lists.newArrayList();
   private int tick;

   TimedTaskRunner(GameTestState gameTest) {
      this.test = gameTest;
      this.tick = gameTest.getTick();
   }

   public TimedTaskRunner createAndAdd(Runnable task) {
      this.tasks.add(TimedTask.create(task));
      return this;
   }

   public TimedTaskRunner createAndAdd(long duration, Runnable task) {
      this.tasks.add(TimedTask.create(duration, task));
      return this;
   }

   public TimedTaskRunner expectMinDuration(int minDuration) {
      return this.expectMinDurationAndRun(minDuration, () -> {
      });
   }

   public TimedTaskRunner createAndAddReported(Runnable task) {
      this.tasks.add(TimedTask.create(() -> {
         this.tryRun(task);
      }));
      return this;
   }

   public TimedTaskRunner expectMinDurationAndRun(int minDuration, Runnable task) {
      this.tasks.add(TimedTask.create(() -> {
         if (this.test.getTick() < this.tick + minDuration) {
            throw new GameTestException(Text.translatable("test.error.sequence.not_completed"), this.test.getTick());
         } else {
            this.tryRun(task);
         }
      }));
      return this;
   }

   public TimedTaskRunner expectMinDurationOrRun(int minDuration, Runnable task) {
      this.tasks.add(TimedTask.create(() -> {
         if (this.test.getTick() < this.tick + minDuration) {
            this.tryRun(task);
            throw new GameTestException(Text.translatable("test.error.sequence.not_completed"), this.test.getTick());
         }
      }));
      return this;
   }

   public void completeIfSuccessful() {
      List var10000 = this.tasks;
      GameTestState var10001 = this.test;
      Objects.requireNonNull(var10001);
      var10000.add(TimedTask.create(var10001::completeIfSuccessful));
   }

   public void fail(Supplier exceptionSupplier) {
      this.tasks.add(TimedTask.create(() -> {
         this.test.fail((TestException)exceptionSupplier.get());
      }));
   }

   public Trigger createAndAddTrigger() {
      Trigger trigger = new Trigger();
      this.tasks.add(TimedTask.create(() -> {
         trigger.trigger(this.test.getTick());
      }));
      return trigger;
   }

   public void runSilently(int tick) {
      try {
         this.runTasks(tick);
      } catch (GameTestException var3) {
      }

   }

   public void runReported(int tick) {
      try {
         this.runTasks(tick);
      } catch (GameTestException var3) {
         this.test.fail((TestException)var3);
      }

   }

   private void tryRun(Runnable task) {
      try {
         task.run();
      } catch (GameTestException var3) {
         this.test.fail((TestException)var3);
      }

   }

   private void runTasks(int tick) {
      Iterator iterator = this.tasks.iterator();

      while(iterator.hasNext()) {
         TimedTask timedTask = (TimedTask)iterator.next();
         timedTask.task.run();
         iterator.remove();
         int i = tick - this.tick;
         int j = this.tick;
         this.tick = tick;
         if (timedTask.duration != null && timedTask.duration != (long)i) {
            this.test.fail((TestException)(new GameTestException(Text.translatable("test.error.sequence.invalid_tick", (long)j + timedTask.duration), tick)));
            break;
         }
      }

   }

   public class Trigger {
      private static final int UNTRIGGERED_TICK = -1;
      private int triggeredTick = -1;

      void trigger(int tick) {
         if (this.triggeredTick != -1) {
            throw new IllegalStateException("Condition already triggered at " + this.triggeredTick);
         } else {
            this.triggeredTick = tick;
         }
      }

      public void checkTrigger() {
         int i = TimedTaskRunner.this.test.getTick();
         if (this.triggeredTick != i) {
            if (this.triggeredTick == -1) {
               throw new GameTestException(Text.translatable("test.error.sequence.condition_not_triggered"), i);
            } else {
               throw new GameTestException(Text.translatable("test.error.sequence.condition_already_triggered", this.triggeredTick), i);
            }
         }
      }
   }
}
