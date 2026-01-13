/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

public static final class Goal.Control
extends Enum<Goal.Control> {
    public static final /* enum */ Goal.Control MOVE = new Goal.Control();
    public static final /* enum */ Goal.Control LOOK = new Goal.Control();
    public static final /* enum */ Goal.Control JUMP = new Goal.Control();
    public static final /* enum */ Goal.Control TARGET = new Goal.Control();
    private static final /* synthetic */ Goal.Control[] field_18409;

    public static Goal.Control[] values() {
        return (Goal.Control[])field_18409.clone();
    }

    public static Goal.Control valueOf(String string) {
        return Enum.valueOf(Goal.Control.class, string);
    }

    private static /* synthetic */ Goal.Control[] method_36621() {
        return new Goal.Control[]{MOVE, LOOK, JUMP, TARGET};
    }

    static {
        field_18409 = Goal.Control.method_36621();
    }
}
