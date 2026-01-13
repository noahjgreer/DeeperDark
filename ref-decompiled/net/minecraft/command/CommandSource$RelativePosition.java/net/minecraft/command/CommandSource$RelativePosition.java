/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

public static class CommandSource.RelativePosition {
    public static final CommandSource.RelativePosition ZERO_LOCAL = new CommandSource.RelativePosition("^", "^", "^");
    public static final CommandSource.RelativePosition ZERO_WORLD = new CommandSource.RelativePosition("~", "~", "~");
    public final String x;
    public final String y;
    public final String z;

    public CommandSource.RelativePosition(String x, String y, String z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
