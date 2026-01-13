/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

static final class CrossbowAttackGoal.Stage
extends Enum<CrossbowAttackGoal.Stage> {
    public static final /* enum */ CrossbowAttackGoal.Stage UNCHARGED = new CrossbowAttackGoal.Stage();
    public static final /* enum */ CrossbowAttackGoal.Stage CHARGING = new CrossbowAttackGoal.Stage();
    public static final /* enum */ CrossbowAttackGoal.Stage CHARGED = new CrossbowAttackGoal.Stage();
    public static final /* enum */ CrossbowAttackGoal.Stage READY_TO_ATTACK = new CrossbowAttackGoal.Stage();
    private static final /* synthetic */ CrossbowAttackGoal.Stage[] field_16531;

    public static CrossbowAttackGoal.Stage[] values() {
        return (CrossbowAttackGoal.Stage[])field_16531.clone();
    }

    public static CrossbowAttackGoal.Stage valueOf(String string) {
        return Enum.valueOf(CrossbowAttackGoal.Stage.class, string);
    }

    private static /* synthetic */ CrossbowAttackGoal.Stage[] method_36622() {
        return new CrossbowAttackGoal.Stage[]{UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK};
    }

    static {
        field_16531 = CrossbowAttackGoal.Stage.method_36622();
    }
}
