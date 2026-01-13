/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

public static final class MultiTickTask.Status
extends Enum<MultiTickTask.Status> {
    public static final /* enum */ MultiTickTask.Status STOPPED = new MultiTickTask.Status();
    public static final /* enum */ MultiTickTask.Status RUNNING = new MultiTickTask.Status();
    private static final /* synthetic */ MultiTickTask.Status[] field_18339;

    public static MultiTickTask.Status[] values() {
        return (MultiTickTask.Status[])field_18339.clone();
    }

    public static MultiTickTask.Status valueOf(String string) {
        return Enum.valueOf(MultiTickTask.Status.class, string);
    }

    private static /* synthetic */ MultiTickTask.Status[] method_36615() {
        return new MultiTickTask.Status[]{STOPPED, RUNNING};
    }

    static {
        field_18339 = MultiTickTask.Status.method_36615();
    }
}
