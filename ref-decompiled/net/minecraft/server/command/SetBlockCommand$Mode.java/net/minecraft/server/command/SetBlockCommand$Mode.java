/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

public static final class SetBlockCommand.Mode
extends Enum<SetBlockCommand.Mode> {
    public static final /* enum */ SetBlockCommand.Mode REPLACE = new SetBlockCommand.Mode();
    public static final /* enum */ SetBlockCommand.Mode DESTROY = new SetBlockCommand.Mode();
    private static final /* synthetic */ SetBlockCommand.Mode[] field_13720;

    public static SetBlockCommand.Mode[] values() {
        return (SetBlockCommand.Mode[])field_13720.clone();
    }

    public static SetBlockCommand.Mode valueOf(String string) {
        return Enum.valueOf(SetBlockCommand.Mode.class, string);
    }

    private static /* synthetic */ SetBlockCommand.Mode[] method_36969() {
        return new SetBlockCommand.Mode[]{REPLACE, DESTROY};
    }

    static {
        field_13720 = SetBlockCommand.Mode.method_36969();
    }
}
