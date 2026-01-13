/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.control;

protected static final class MoveControl.State
extends Enum<MoveControl.State> {
    public static final /* enum */ MoveControl.State WAIT = new MoveControl.State();
    public static final /* enum */ MoveControl.State MOVE_TO = new MoveControl.State();
    public static final /* enum */ MoveControl.State STRAFE = new MoveControl.State();
    public static final /* enum */ MoveControl.State JUMPING = new MoveControl.State();
    private static final /* synthetic */ MoveControl.State[] field_6375;

    public static MoveControl.State[] values() {
        return (MoveControl.State[])field_6375.clone();
    }

    public static MoveControl.State valueOf(String string) {
        return Enum.valueOf(MoveControl.State.class, string);
    }

    private static /* synthetic */ MoveControl.State[] method_36619() {
        return new MoveControl.State[]{WAIT, MOVE_TO, STRAFE, JUMPING};
    }

    static {
        field_6375 = MoveControl.State.method_36619();
    }
}
