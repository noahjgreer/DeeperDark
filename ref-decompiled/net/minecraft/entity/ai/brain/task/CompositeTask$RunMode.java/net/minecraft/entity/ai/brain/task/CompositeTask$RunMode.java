/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public static abstract sealed class CompositeTask.RunMode
extends Enum<CompositeTask.RunMode> {
    public static final /* enum */ CompositeTask.RunMode RUN_ONE = new CompositeTask.RunMode(){

        @Override
        public <E extends LivingEntity> void run(Stream<Task<? super E>> tasks, ServerWorld world, E entity, long time) {
            tasks.filter(task -> task.getStatus() == MultiTickTask.Status.STOPPED).filter(task -> task.tryStarting(world, entity, time)).findFirst();
        }
    };
    public static final /* enum */ CompositeTask.RunMode TRY_ALL = new CompositeTask.RunMode(){

        @Override
        public <E extends LivingEntity> void run(Stream<Task<? super E>> tasks, ServerWorld world, E entity, long time) {
            tasks.filter(task -> task.getStatus() == MultiTickTask.Status.STOPPED).forEach(task -> task.tryStarting(world, entity, time));
        }
    };
    private static final /* synthetic */ CompositeTask.RunMode[] field_18857;

    public static CompositeTask.RunMode[] values() {
        return (CompositeTask.RunMode[])field_18857.clone();
    }

    public static CompositeTask.RunMode valueOf(String string) {
        return Enum.valueOf(CompositeTask.RunMode.class, string);
    }

    public abstract <E extends LivingEntity> void run(Stream<Task<? super E>> var1, ServerWorld var2, E var3, long var4);

    private static /* synthetic */ CompositeTask.RunMode[] method_36618() {
        return new CompositeTask.RunMode[]{RUN_ONE, TRY_ALL};
    }

    static {
        field_18857 = CompositeTask.RunMode.method_36618();
    }
}
