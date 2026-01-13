/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

static final class TestManager.State
extends Enum<TestManager.State> {
    public static final /* enum */ TestManager.State IDLE = new TestManager.State();
    public static final /* enum */ TestManager.State RUNNING = new TestManager.State();
    public static final /* enum */ TestManager.State HALTING = new TestManager.State();
    private static final /* synthetic */ TestManager.State[] field_57045;

    public static TestManager.State[] values() {
        return (TestManager.State[])field_57045.clone();
    }

    public static TestManager.State valueOf(String string) {
        return Enum.valueOf(TestManager.State.class, string);
    }

    private static /* synthetic */ TestManager.State[] method_68078() {
        return new TestManager.State[]{IDLE, RUNNING, HALTING};
    }

    static {
        field_57045 = TestManager.State.method_68078();
    }
}
