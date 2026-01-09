package net.minecraft.entity.ai.brain.task;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.WeightedList;

public class CompositeTask implements Task {
   private final Map requiredMemoryState;
   private final Set memoriesToForgetWhenStopped;
   private final Order order;
   private final RunMode runMode;
   private final WeightedList tasks = new WeightedList();
   private MultiTickTask.Status status;

   public CompositeTask(Map requiredMemoryState, Set memoriesToForgetWhenStopped, Order order, RunMode runMode, List tasks) {
      this.status = MultiTickTask.Status.STOPPED;
      this.requiredMemoryState = requiredMemoryState;
      this.memoriesToForgetWhenStopped = memoriesToForgetWhenStopped;
      this.order = order;
      this.runMode = runMode;
      tasks.forEach((task) -> {
         this.tasks.add((Task)task.getFirst(), (Integer)task.getSecond());
      });
   }

   public MultiTickTask.Status getStatus() {
      return this.status;
   }

   private boolean shouldStart(LivingEntity entity) {
      Iterator var2 = this.requiredMemoryState.entrySet().iterator();

      MemoryModuleType memoryModuleType;
      MemoryModuleState memoryModuleState;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         Map.Entry entry = (Map.Entry)var2.next();
         memoryModuleType = (MemoryModuleType)entry.getKey();
         memoryModuleState = (MemoryModuleState)entry.getValue();
      } while(entity.getBrain().isMemoryInState(memoryModuleType, memoryModuleState));

      return false;
   }

   public final boolean tryStarting(ServerWorld world, LivingEntity entity, long time) {
      if (this.shouldStart(entity)) {
         this.status = MultiTickTask.Status.RUNNING;
         this.order.apply(this.tasks);
         this.runMode.run(this.tasks.stream(), world, entity, time);
         return true;
      } else {
         return false;
      }
   }

   public final void tick(ServerWorld world, LivingEntity entity, long time) {
      this.tasks.stream().filter((task) -> {
         return task.getStatus() == MultiTickTask.Status.RUNNING;
      }).forEach((task) -> {
         task.tick(world, entity, time);
      });
      if (this.tasks.stream().noneMatch((task) -> {
         return task.getStatus() == MultiTickTask.Status.RUNNING;
      })) {
         this.stop(world, entity, time);
      }

   }

   public final void stop(ServerWorld world, LivingEntity entity, long time) {
      this.status = MultiTickTask.Status.STOPPED;
      this.tasks.stream().filter((task) -> {
         return task.getStatus() == MultiTickTask.Status.RUNNING;
      }).forEach((task) -> {
         task.stop(world, entity, time);
      });
      Set var10000 = this.memoriesToForgetWhenStopped;
      Brain var10001 = entity.getBrain();
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::forget);
   }

   public String getName() {
      return this.getClass().getSimpleName();
   }

   public String toString() {
      Set set = (Set)this.tasks.stream().filter((task) -> {
         return task.getStatus() == MultiTickTask.Status.RUNNING;
      }).collect(Collectors.toSet());
      String var10000 = this.getClass().getSimpleName();
      return "(" + var10000 + "): " + String.valueOf(set);
   }

   public static enum Order {
      ORDERED((list) -> {
      }),
      SHUFFLED(WeightedList::shuffle);

      private final Consumer listModifier;

      private Order(final Consumer listModifier) {
         this.listModifier = listModifier;
      }

      public void apply(WeightedList list) {
         this.listModifier.accept(list);
      }

      // $FF: synthetic method
      private static Order[] method_36617() {
         return new Order[]{ORDERED, SHUFFLED};
      }
   }

   public static enum RunMode {
      RUN_ONE {
         public void run(Stream tasks, ServerWorld world, LivingEntity entity, long time) {
            tasks.filter((task) -> {
               return task.getStatus() == MultiTickTask.Status.STOPPED;
            }).filter((task) -> {
               return task.tryStarting(world, entity, time);
            }).findFirst();
         }
      },
      TRY_ALL {
         public void run(Stream tasks, ServerWorld world, LivingEntity entity, long time) {
            tasks.filter((task) -> {
               return task.getStatus() == MultiTickTask.Status.STOPPED;
            }).forEach((task) -> {
               task.tryStarting(world, entity, time);
            });
         }
      };

      public abstract void run(Stream tasks, ServerWorld world, LivingEntity entity, long time);

      // $FF: synthetic method
      private static RunMode[] method_36618() {
         return new RunMode[]{RUN_ONE, TRY_ALL};
      }
   }
}
