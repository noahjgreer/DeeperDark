/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

static final class AdvancementCommand.Selection
extends Enum<AdvancementCommand.Selection> {
    public static final /* enum */ AdvancementCommand.Selection ONLY = new AdvancementCommand.Selection(false, false);
    public static final /* enum */ AdvancementCommand.Selection THROUGH = new AdvancementCommand.Selection(true, true);
    public static final /* enum */ AdvancementCommand.Selection FROM = new AdvancementCommand.Selection(false, true);
    public static final /* enum */ AdvancementCommand.Selection UNTIL = new AdvancementCommand.Selection(true, false);
    public static final /* enum */ AdvancementCommand.Selection EVERYTHING = new AdvancementCommand.Selection(true, true);
    final boolean before;
    final boolean after;
    private static final /* synthetic */ AdvancementCommand.Selection[] field_13463;

    public static AdvancementCommand.Selection[] values() {
        return (AdvancementCommand.Selection[])field_13463.clone();
    }

    public static AdvancementCommand.Selection valueOf(String string) {
        return Enum.valueOf(AdvancementCommand.Selection.class, string);
    }

    private AdvancementCommand.Selection(boolean before, boolean after) {
        this.before = before;
        this.after = after;
    }

    private static /* synthetic */ AdvancementCommand.Selection[] method_36965() {
        return new AdvancementCommand.Selection[]{ONLY, THROUGH, FROM, UNTIL, EVERYTHING};
    }

    static {
        field_13463 = AdvancementCommand.Selection.method_36965();
    }
}
