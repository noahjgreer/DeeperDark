/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public static final class ActionResult.SwingSource
extends Enum<ActionResult.SwingSource> {
    public static final /* enum */ ActionResult.SwingSource NONE = new ActionResult.SwingSource();
    public static final /* enum */ ActionResult.SwingSource CLIENT = new ActionResult.SwingSource();
    public static final /* enum */ ActionResult.SwingSource SERVER = new ActionResult.SwingSource();
    private static final /* synthetic */ ActionResult.SwingSource[] field_52429;

    public static ActionResult.SwingSource[] values() {
        return (ActionResult.SwingSource[])field_52429.clone();
    }

    public static ActionResult.SwingSource valueOf(String string) {
        return Enum.valueOf(ActionResult.SwingSource.class, string);
    }

    private static /* synthetic */ ActionResult.SwingSource[] method_61397() {
        return new ActionResult.SwingSource[]{NONE, CLIENT, SERVER};
    }

    static {
        field_52429 = ActionResult.SwingSource.method_61397();
    }
}
