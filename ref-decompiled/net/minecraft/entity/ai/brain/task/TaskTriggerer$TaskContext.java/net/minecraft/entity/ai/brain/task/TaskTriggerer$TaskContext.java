/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.kinds.Applicative$Mu
 *  com.mojang.datafixers.kinds.Const$Mu
 *  com.mojang.datafixers.kinds.IdF
 *  com.mojang.datafixers.kinds.IdF$Mu
 *  com.mojang.datafixers.kinds.OptionalBox
 *  com.mojang.datafixers.kinds.OptionalBox$Mu
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.datafixers.util.Function4
 *  com.mojang.datafixers.util.Unit
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Unit;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQuery;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.TaskRunnable;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;

public static final class TaskTriggerer.TaskContext<E extends LivingEntity>
implements Applicative<TaskTriggerer.K1<E>, Mu<E>> {
    public <Value> Optional<Value> getOptionalValue(MemoryQueryResult<OptionalBox.Mu, Value> result) {
        return OptionalBox.unbox(result.getValue());
    }

    public <Value> Value getValue(MemoryQueryResult<IdF.Mu, Value> result) {
        return (Value)IdF.get(result.getValue());
    }

    public <Value> TaskTriggerer<E, MemoryQueryResult<OptionalBox.Mu, Value>> queryMemoryOptional(MemoryModuleType<Value> type) {
        return new TaskTriggerer.QueryMemory(new MemoryQuery.Optional<Value>(type));
    }

    public <Value> TaskTriggerer<E, MemoryQueryResult<IdF.Mu, Value>> queryMemoryValue(MemoryModuleType<Value> type) {
        return new TaskTriggerer.QueryMemory(new MemoryQuery.Value<Value>(type));
    }

    public <Value> TaskTriggerer<E, MemoryQueryResult<Const.Mu<Unit>, Value>> queryMemoryAbsent(MemoryModuleType<Value> type) {
        return new TaskTriggerer.QueryMemory(new MemoryQuery.Absent<Value>(type));
    }

    public TaskTriggerer<E, Unit> trigger(TaskRunnable<? super E> runnable) {
        return new TaskTriggerer.Trigger<E>(runnable);
    }

    public <A> TaskTriggerer<E, A> point(A object) {
        return new TaskTriggerer.Supply(object);
    }

    public <A> TaskTriggerer<E, A> supply(Supplier<String> nameSupplier, A value) {
        return new TaskTriggerer.Supply(value, nameSupplier);
    }

    public <A, R> Function<App<TaskTriggerer.K1<E>, A>, App<TaskTriggerer.K1<E>, R>> lift1(App<TaskTriggerer.K1<E>, Function<A, R>> app) {
        return app2 -> {
            final TaskTriggerer.TaskFunction taskFunction = TaskTriggerer.getFunction(app2);
            final TaskTriggerer.TaskFunction taskFunction2 = TaskTriggerer.getFunction(app);
            return TaskTriggerer.of(new TaskTriggerer.TaskFunction<E, R>(this){

                @Override
                public R run(ServerWorld world, E entity, long time) {
                    Object object = taskFunction.run(world, entity, time);
                    if (object == null) {
                        return null;
                    }
                    Function function = (Function)taskFunction2.run(world, entity, time);
                    if (function == null) {
                        return null;
                    }
                    return function.apply(object);
                }

                @Override
                public String asString() {
                    return taskFunction2.asString() + " * " + taskFunction.asString();
                }

                public String toString() {
                    return this.asString();
                }
            });
        };
    }

    public <T, R> TaskTriggerer<E, R> map(final Function<? super T, ? extends R> function, App<TaskTriggerer.K1<E>, T> app) {
        final TaskTriggerer.TaskFunction<E, T> taskFunction = TaskTriggerer.getFunction(app);
        return TaskTriggerer.of(new TaskTriggerer.TaskFunction<E, R>(this){

            @Override
            public R run(ServerWorld world, E entity, long time) {
                Object object = taskFunction.run(world, entity, time);
                if (object == null) {
                    return null;
                }
                return function.apply(object);
            }

            @Override
            public String asString() {
                return taskFunction.asString() + ".map[" + String.valueOf(function) + "]";
            }

            public String toString() {
                return this.asString();
            }
        });
    }

    public <A, B, R> TaskTriggerer<E, R> ap2(App<TaskTriggerer.K1<E>, BiFunction<A, B, R>> app, App<TaskTriggerer.K1<E>, A> app2, App<TaskTriggerer.K1<E>, B> app3) {
        final TaskTriggerer.TaskFunction<E, A> taskFunction = TaskTriggerer.getFunction(app2);
        final TaskTriggerer.TaskFunction<E, B> taskFunction2 = TaskTriggerer.getFunction(app3);
        final TaskTriggerer.TaskFunction<E, BiFunction<A, B, R>> taskFunction3 = TaskTriggerer.getFunction(app);
        return TaskTriggerer.of(new TaskTriggerer.TaskFunction<E, R>(this){

            @Override
            public R run(ServerWorld world, E entity, long time) {
                Object object = taskFunction.run(world, entity, time);
                if (object == null) {
                    return null;
                }
                Object object2 = taskFunction2.run(world, entity, time);
                if (object2 == null) {
                    return null;
                }
                BiFunction biFunction = (BiFunction)taskFunction3.run(world, entity, time);
                if (biFunction == null) {
                    return null;
                }
                return biFunction.apply(object, object2);
            }

            @Override
            public String asString() {
                return taskFunction3.asString() + " * " + taskFunction.asString() + " * " + taskFunction2.asString();
            }

            public String toString() {
                return this.asString();
            }
        });
    }

    public <T1, T2, T3, R> TaskTriggerer<E, R> ap3(App<TaskTriggerer.K1<E>, Function3<T1, T2, T3, R>> app, App<TaskTriggerer.K1<E>, T1> app2, App<TaskTriggerer.K1<E>, T2> app3, App<TaskTriggerer.K1<E>, T3> app4) {
        final TaskTriggerer.TaskFunction<E, T1> taskFunction = TaskTriggerer.getFunction(app2);
        final TaskTriggerer.TaskFunction<E, T2> taskFunction2 = TaskTriggerer.getFunction(app3);
        final TaskTriggerer.TaskFunction<E, T3> taskFunction3 = TaskTriggerer.getFunction(app4);
        final TaskTriggerer.TaskFunction<E, Function3<T1, T2, T3, R>> taskFunction4 = TaskTriggerer.getFunction(app);
        return TaskTriggerer.of(new TaskTriggerer.TaskFunction<E, R>(this){

            @Override
            public R run(ServerWorld world, E entity, long time) {
                Object object = taskFunction.run(world, entity, time);
                if (object == null) {
                    return null;
                }
                Object object2 = taskFunction2.run(world, entity, time);
                if (object2 == null) {
                    return null;
                }
                Object object3 = taskFunction3.run(world, entity, time);
                if (object3 == null) {
                    return null;
                }
                Function3 function3 = (Function3)taskFunction4.run(world, entity, time);
                if (function3 == null) {
                    return null;
                }
                return function3.apply(object, object2, object3);
            }

            @Override
            public String asString() {
                return taskFunction4.asString() + " * " + taskFunction.asString() + " * " + taskFunction2.asString() + " * " + taskFunction3.asString();
            }

            public String toString() {
                return this.asString();
            }
        });
    }

    public <T1, T2, T3, T4, R> TaskTriggerer<E, R> ap4(App<TaskTriggerer.K1<E>, Function4<T1, T2, T3, T4, R>> app, App<TaskTriggerer.K1<E>, T1> app2, App<TaskTriggerer.K1<E>, T2> app3, App<TaskTriggerer.K1<E>, T3> app4, App<TaskTriggerer.K1<E>, T4> app5) {
        final TaskTriggerer.TaskFunction<E, T1> taskFunction = TaskTriggerer.getFunction(app2);
        final TaskTriggerer.TaskFunction<E, T2> taskFunction2 = TaskTriggerer.getFunction(app3);
        final TaskTriggerer.TaskFunction<E, T3> taskFunction3 = TaskTriggerer.getFunction(app4);
        final TaskTriggerer.TaskFunction<E, T4> taskFunction4 = TaskTriggerer.getFunction(app5);
        final TaskTriggerer.TaskFunction<E, Function4<T1, T2, T3, T4, R>> taskFunction5 = TaskTriggerer.getFunction(app);
        return TaskTriggerer.of(new TaskTriggerer.TaskFunction<E, R>(this){

            @Override
            public R run(ServerWorld world, E entity, long time) {
                Object object = taskFunction.run(world, entity, time);
                if (object == null) {
                    return null;
                }
                Object object2 = taskFunction2.run(world, entity, time);
                if (object2 == null) {
                    return null;
                }
                Object object3 = taskFunction3.run(world, entity, time);
                if (object3 == null) {
                    return null;
                }
                Object object4 = taskFunction4.run(world, entity, time);
                if (object4 == null) {
                    return null;
                }
                Function4 function4 = (Function4)taskFunction5.run(world, entity, time);
                if (function4 == null) {
                    return null;
                }
                return function4.apply(object, object2, object3, object4);
            }

            @Override
            public String asString() {
                return taskFunction5.asString() + " * " + taskFunction.asString() + " * " + taskFunction2.asString() + " * " + taskFunction3.asString() + " * " + taskFunction4.asString();
            }

            public String toString() {
                return this.asString();
            }
        });
    }

    public /* synthetic */ App ap4(App app, App function1, App function2, App function3, App function4) {
        return this.ap4(app, function1, function2, function3, function4);
    }

    public /* synthetic */ App ap3(App app, App function1, App function2, App function3) {
        return this.ap3(app, function1, function2, function3);
    }

    public /* synthetic */ App ap2(App app, App function1, App function2) {
        return this.ap2(app, function1, function2);
    }

    public /* synthetic */ App point(Object value) {
        return this.point(value);
    }

    public /* synthetic */ App map(Function function, App app) {
        return this.map(function, app);
    }

    static final class Mu<E extends LivingEntity>
    implements Applicative.Mu {
        private Mu() {
        }
    }
}
