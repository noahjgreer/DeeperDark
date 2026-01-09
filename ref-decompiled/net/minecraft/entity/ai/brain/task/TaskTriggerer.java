package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Unit;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQuery;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class TaskTriggerer implements App {
   private final TaskFunction function;

   public static TaskTriggerer cast(App app) {
      return (TaskTriggerer)app;
   }

   public static TaskContext newContext() {
      return new TaskContext();
   }

   public static SingleTickTask task(Function creator) {
      final TaskFunction taskFunction = getFunction((App)creator.apply(newContext()));
      return new SingleTickTask() {
         public boolean trigger(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
            TaskRunnable taskRunnable = (TaskRunnable)taskFunction.run(serverWorld, livingEntity, l);
            return taskRunnable == null ? false : taskRunnable.trigger(serverWorld, livingEntity, l);
         }

         public String getName() {
            return "OneShot[" + taskFunction.asString() + "]";
         }

         public String toString() {
            return this.getName();
         }
      };
   }

   public static SingleTickTask runIf(TaskRunnable predicate, TaskRunnable task) {
      return task((context) -> {
         return context.group(context.trigger(predicate)).apply(context, (unit) -> {
            Objects.requireNonNull(task);
            return task::trigger;
         });
      });
   }

   public static SingleTickTask runIf(Predicate predicate, SingleTickTask task) {
      return runIf((TaskRunnable)predicate(predicate), (TaskRunnable)task);
   }

   public static SingleTickTask predicate(Predicate predicate) {
      return task((context) -> {
         return context.point((world, entity, time) -> {
            return predicate.test(entity);
         });
      });
   }

   public static SingleTickTask predicate(BiPredicate predicate) {
      return task((context) -> {
         return context.point((world, entity, time) -> {
            return predicate.test(world, entity);
         });
      });
   }

   static TaskFunction getFunction(App app) {
      return cast(app).function;
   }

   TaskTriggerer(TaskFunction function) {
      this.function = function;
   }

   static TaskTriggerer of(TaskFunction function) {
      return new TaskTriggerer(function);
   }

   public static final class TaskContext implements Applicative {
      public Optional getOptionalValue(MemoryQueryResult result) {
         return OptionalBox.unbox(result.getValue());
      }

      public Object getValue(MemoryQueryResult result) {
         return IdF.get(result.getValue());
      }

      public TaskTriggerer queryMemoryOptional(MemoryModuleType type) {
         return new QueryMemory(new MemoryQuery.Optional(type));
      }

      public TaskTriggerer queryMemoryValue(MemoryModuleType type) {
         return new QueryMemory(new MemoryQuery.Value(type));
      }

      public TaskTriggerer queryMemoryAbsent(MemoryModuleType type) {
         return new QueryMemory(new MemoryQuery.Absent(type));
      }

      public TaskTriggerer trigger(TaskRunnable runnable) {
         return new Trigger(runnable);
      }

      public TaskTriggerer point(Object object) {
         return new Supply(object);
      }

      public TaskTriggerer supply(Supplier nameSupplier, Object value) {
         return new Supply(value, nameSupplier);
      }

      public Function lift1(App app) {
         return (app2) -> {
            final TaskFunction taskFunction = TaskTriggerer.getFunction(app2);
            final TaskFunction taskFunction2 = TaskTriggerer.getFunction(app);
            return TaskTriggerer.of(new TaskFunction(this) {
               public Object run(ServerWorld world, LivingEntity entity, long time) {
                  Object object = taskFunction.run(world, entity, time);
                  if (object == null) {
                     return null;
                  } else {
                     Function function = (Function)taskFunction2.run(world, entity, time);
                     return function == null ? null : function.apply(object);
                  }
               }

               public String asString() {
                  String var10000 = taskFunction2.asString();
                  return var10000 + " * " + taskFunction.asString();
               }

               public String toString() {
                  return this.asString();
               }
            });
         };
      }

      public TaskTriggerer map(final Function function, App app) {
         final TaskFunction taskFunction = TaskTriggerer.getFunction(app);
         return TaskTriggerer.of(new TaskFunction(this) {
            public Object run(ServerWorld world, LivingEntity entity, long time) {
               Object object = taskFunction.run(world, entity, time);
               return object == null ? null : function.apply(object);
            }

            public String asString() {
               String var10000 = taskFunction.asString();
               return var10000 + ".map[" + String.valueOf(function) + "]";
            }

            public String toString() {
               return this.asString();
            }
         });
      }

      public TaskTriggerer ap2(App app, App app2, App app3) {
         final TaskFunction taskFunction = TaskTriggerer.getFunction(app2);
         final TaskFunction taskFunction2 = TaskTriggerer.getFunction(app3);
         final TaskFunction taskFunction3 = TaskTriggerer.getFunction(app);
         return TaskTriggerer.of(new TaskFunction(this) {
            public Object run(ServerWorld world, LivingEntity entity, long time) {
               Object object = taskFunction.run(world, entity, time);
               if (object == null) {
                  return null;
               } else {
                  Object object2 = taskFunction2.run(world, entity, time);
                  if (object2 == null) {
                     return null;
                  } else {
                     BiFunction biFunction = (BiFunction)taskFunction3.run(world, entity, time);
                     return biFunction == null ? null : biFunction.apply(object, object2);
                  }
               }
            }

            public String asString() {
               String var10000 = taskFunction3.asString();
               return var10000 + " * " + taskFunction.asString() + " * " + taskFunction2.asString();
            }

            public String toString() {
               return this.asString();
            }
         });
      }

      public TaskTriggerer ap3(App app, App app2, App app3, App app4) {
         final TaskFunction taskFunction = TaskTriggerer.getFunction(app2);
         final TaskFunction taskFunction2 = TaskTriggerer.getFunction(app3);
         final TaskFunction taskFunction3 = TaskTriggerer.getFunction(app4);
         final TaskFunction taskFunction4 = TaskTriggerer.getFunction(app);
         return TaskTriggerer.of(new TaskFunction(this) {
            public Object run(ServerWorld world, LivingEntity entity, long time) {
               Object object = taskFunction.run(world, entity, time);
               if (object == null) {
                  return null;
               } else {
                  Object object2 = taskFunction2.run(world, entity, time);
                  if (object2 == null) {
                     return null;
                  } else {
                     Object object3 = taskFunction3.run(world, entity, time);
                     if (object3 == null) {
                        return null;
                     } else {
                        Function3 function3 = (Function3)taskFunction4.run(world, entity, time);
                        return function3 == null ? null : function3.apply(object, object2, object3);
                     }
                  }
               }
            }

            public String asString() {
               String var10000 = taskFunction4.asString();
               return var10000 + " * " + taskFunction.asString() + " * " + taskFunction2.asString() + " * " + taskFunction3.asString();
            }

            public String toString() {
               return this.asString();
            }
         });
      }

      public TaskTriggerer ap4(App app, App app2, App app3, App app4, App app5) {
         final TaskFunction taskFunction = TaskTriggerer.getFunction(app2);
         final TaskFunction taskFunction2 = TaskTriggerer.getFunction(app3);
         final TaskFunction taskFunction3 = TaskTriggerer.getFunction(app4);
         final TaskFunction taskFunction4 = TaskTriggerer.getFunction(app5);
         final TaskFunction taskFunction5 = TaskTriggerer.getFunction(app);
         return TaskTriggerer.of(new TaskFunction(this) {
            public Object run(ServerWorld world, LivingEntity entity, long time) {
               Object object = taskFunction.run(world, entity, time);
               if (object == null) {
                  return null;
               } else {
                  Object object2 = taskFunction2.run(world, entity, time);
                  if (object2 == null) {
                     return null;
                  } else {
                     Object object3 = taskFunction3.run(world, entity, time);
                     if (object3 == null) {
                        return null;
                     } else {
                        Object object4 = taskFunction4.run(world, entity, time);
                        if (object4 == null) {
                           return null;
                        } else {
                           Function4 function4 = (Function4)taskFunction5.run(world, entity, time);
                           return function4 == null ? null : function4.apply(object, object2, object3, object4);
                        }
                     }
                  }
               }
            }

            public String asString() {
               String var10000 = taskFunction5.asString();
               return var10000 + " * " + taskFunction.asString() + " * " + taskFunction2.asString() + " * " + taskFunction3.asString() + " * " + taskFunction4.asString();
            }

            public String toString() {
               return this.asString();
            }
         });
      }

      // $FF: synthetic method
      public App ap4(final App app, final App function1, final App function2, final App function3, final App function4) {
         return this.ap4(app, function1, function2, function3, function4);
      }

      // $FF: synthetic method
      public App ap3(final App app, final App function1, final App function2, final App function3) {
         return this.ap3(app, function1, function2, function3);
      }

      // $FF: synthetic method
      public App ap2(final App app, final App function1, final App function2) {
         return this.ap2(app, function1, function2);
      }

      // $FF: synthetic method
      public App point(final Object value) {
         return this.point(value);
      }

      // $FF: synthetic method
      public App map(final Function function, final App app) {
         return this.map(function, app);
      }

      static final class Mu implements Applicative.Mu {
         private Mu() {
         }
      }
   }

   private interface TaskFunction {
      @Nullable
      Object run(ServerWorld world, LivingEntity entity, long time);

      String asString();
   }

   static final class Trigger extends TaskTriggerer {
      Trigger(final TaskRunnable taskRunnable) {
         super(new TaskFunction() {
            @Nullable
            public Unit run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
               return taskRunnable.trigger(serverWorld, livingEntity, l) ? Unit.INSTANCE : null;
            }

            public String asString() {
               return "T[" + String.valueOf(taskRunnable) + "]";
            }

            // $FF: synthetic method
            @Nullable
            public Object run(final ServerWorld world, final LivingEntity entity, final long time) {
               return this.run(world, entity, time);
            }
         });
      }
   }

   private static final class Supply extends TaskTriggerer {
      Supply(Object value) {
         this(value, () -> {
            return "C[" + String.valueOf(value) + "]";
         });
      }

      Supply(final Object value, final Supplier nameSupplier) {
         super(new TaskFunction() {
            public Object run(ServerWorld world, LivingEntity entity, long time) {
               return value;
            }

            public String asString() {
               return (String)nameSupplier.get();
            }

            public String toString() {
               return this.asString();
            }
         });
      }
   }

   private static final class QueryMemory extends TaskTriggerer {
      QueryMemory(final MemoryQuery query) {
         super(new TaskFunction() {
            public MemoryQueryResult run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
               Brain brain = livingEntity.getBrain();
               Optional optional = brain.getOptionalMemory(query.memory());
               return optional == null ? null : query.toQueryResult(brain, optional);
            }

            public String asString() {
               return "M[" + String.valueOf(query) + "]";
            }

            public String toString() {
               return this.asString();
            }

            // $FF: synthetic method
            public Object run(final ServerWorld world, final LivingEntity entity, final long time) {
               return this.run(world, entity, time);
            }
         });
      }
   }

   public static final class K1 implements com.mojang.datafixers.kinds.K1 {
   }
}
