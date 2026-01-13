/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

static final class CloneCommand.Mode
extends Enum<CloneCommand.Mode> {
    public static final /* enum */ CloneCommand.Mode FORCE = new CloneCommand.Mode(true);
    public static final /* enum */ CloneCommand.Mode MOVE = new CloneCommand.Mode(true);
    public static final /* enum */ CloneCommand.Mode NORMAL = new CloneCommand.Mode(false);
    private final boolean allowsOverlap;
    private static final /* synthetic */ CloneCommand.Mode[] field_13501;

    public static CloneCommand.Mode[] values() {
        return (CloneCommand.Mode[])field_13501.clone();
    }

    public static CloneCommand.Mode valueOf(String string) {
        return Enum.valueOf(CloneCommand.Mode.class, string);
    }

    private CloneCommand.Mode(boolean allowsOverlap) {
        this.allowsOverlap = allowsOverlap;
    }

    public boolean allowsOverlap() {
        return this.allowsOverlap;
    }

    private static /* synthetic */ CloneCommand.Mode[] method_36966() {
        return new CloneCommand.Mode[]{FORCE, MOVE, NORMAL};
    }

    static {
        field_13501 = CloneCommand.Mode.method_36966();
    }
}
