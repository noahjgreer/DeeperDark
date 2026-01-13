/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

static final class CrossbowAttackTask.CrossbowState
extends Enum<CrossbowAttackTask.CrossbowState> {
    public static final /* enum */ CrossbowAttackTask.CrossbowState UNCHARGED = new CrossbowAttackTask.CrossbowState();
    public static final /* enum */ CrossbowAttackTask.CrossbowState CHARGING = new CrossbowAttackTask.CrossbowState();
    public static final /* enum */ CrossbowAttackTask.CrossbowState CHARGED = new CrossbowAttackTask.CrossbowState();
    public static final /* enum */ CrossbowAttackTask.CrossbowState READY_TO_ATTACK = new CrossbowAttackTask.CrossbowState();
    private static final /* synthetic */ CrossbowAttackTask.CrossbowState[] field_22299;

    public static CrossbowAttackTask.CrossbowState[] values() {
        return (CrossbowAttackTask.CrossbowState[])field_22299.clone();
    }

    public static CrossbowAttackTask.CrossbowState valueOf(String string) {
        return Enum.valueOf(CrossbowAttackTask.CrossbowState.class, string);
    }

    private static /* synthetic */ CrossbowAttackTask.CrossbowState[] method_36616() {
        return new CrossbowAttackTask.CrossbowState[]{UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK};
    }

    static {
        field_22299 = CrossbowAttackTask.CrossbowState.method_36616();
    }
}
