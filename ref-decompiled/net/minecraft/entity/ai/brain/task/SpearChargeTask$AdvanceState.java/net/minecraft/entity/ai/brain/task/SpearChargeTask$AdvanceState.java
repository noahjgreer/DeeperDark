/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

public static final class SpearChargeTask.AdvanceState
extends Enum<SpearChargeTask.AdvanceState> {
    public static final /* enum */ SpearChargeTask.AdvanceState APPROACH = new SpearChargeTask.AdvanceState();
    public static final /* enum */ SpearChargeTask.AdvanceState CHARGING = new SpearChargeTask.AdvanceState();
    public static final /* enum */ SpearChargeTask.AdvanceState RETREAT = new SpearChargeTask.AdvanceState();
    private static final /* synthetic */ SpearChargeTask.AdvanceState[] field_64632;

    public static SpearChargeTask.AdvanceState[] values() {
        return (SpearChargeTask.AdvanceState[])field_64632.clone();
    }

    public static SpearChargeTask.AdvanceState valueOf(String string) {
        return Enum.valueOf(SpearChargeTask.AdvanceState.class, string);
    }

    private static /* synthetic */ SpearChargeTask.AdvanceState[] method_76713() {
        return new SpearChargeTask.AdvanceState[]{APPROACH, CHARGING, RETREAT};
    }

    static {
        field_64632 = SpearChargeTask.AdvanceState.method_76713();
    }
}
