/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

static final class FrogEatEntityTask.Phase
extends Enum<FrogEatEntityTask.Phase> {
    public static final /* enum */ FrogEatEntityTask.Phase MOVE_TO_TARGET = new FrogEatEntityTask.Phase();
    public static final /* enum */ FrogEatEntityTask.Phase CATCH_ANIMATION = new FrogEatEntityTask.Phase();
    public static final /* enum */ FrogEatEntityTask.Phase EAT_ANIMATION = new FrogEatEntityTask.Phase();
    public static final /* enum */ FrogEatEntityTask.Phase DONE = new FrogEatEntityTask.Phase();
    private static final /* synthetic */ FrogEatEntityTask.Phase[] field_37495;

    public static FrogEatEntityTask.Phase[] values() {
        return (FrogEatEntityTask.Phase[])field_37495.clone();
    }

    public static FrogEatEntityTask.Phase valueOf(String string) {
        return Enum.valueOf(FrogEatEntityTask.Phase.class, string);
    }

    private static /* synthetic */ FrogEatEntityTask.Phase[] method_41390() {
        return new FrogEatEntityTask.Phase[]{MOVE_TO_TARGET, CATCH_ANIMATION, EAT_ANIMATION, DONE};
    }

    static {
        field_37495 = FrogEatEntityTask.Phase.method_41390();
    }
}
