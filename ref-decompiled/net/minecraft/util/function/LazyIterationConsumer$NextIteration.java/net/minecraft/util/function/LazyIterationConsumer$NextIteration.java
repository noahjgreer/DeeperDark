/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.function;

public static final class LazyIterationConsumer.NextIteration
extends Enum<LazyIterationConsumer.NextIteration> {
    public static final /* enum */ LazyIterationConsumer.NextIteration CONTINUE = new LazyIterationConsumer.NextIteration();
    public static final /* enum */ LazyIterationConsumer.NextIteration ABORT = new LazyIterationConsumer.NextIteration();
    private static final /* synthetic */ LazyIterationConsumer.NextIteration[] field_41285;

    public static LazyIterationConsumer.NextIteration[] values() {
        return (LazyIterationConsumer.NextIteration[])field_41285.clone();
    }

    public static LazyIterationConsumer.NextIteration valueOf(String string) {
        return Enum.valueOf(LazyIterationConsumer.NextIteration.class, string);
    }

    public boolean shouldAbort() {
        return this == ABORT;
    }

    private static /* synthetic */ LazyIterationConsumer.NextIteration[] method_47544() {
        return new LazyIterationConsumer.NextIteration[]{CONTINUE, ABORT};
    }

    static {
        field_41285 = LazyIterationConsumer.NextIteration.method_47544();
    }
}
