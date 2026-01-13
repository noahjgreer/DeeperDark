/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.WireConnection
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class WireConnection
extends Enum<WireConnection>
implements StringIdentifiable {
    public static final /* enum */ WireConnection UP = new WireConnection("UP", 0, "up");
    public static final /* enum */ WireConnection SIDE = new WireConnection("SIDE", 1, "side");
    public static final /* enum */ WireConnection NONE = new WireConnection("NONE", 2, "none");
    private final String name;
    private static final /* synthetic */ WireConnection[] field_12688;

    public static WireConnection[] values() {
        return (WireConnection[])field_12688.clone();
    }

    public static WireConnection valueOf(String string) {
        return Enum.valueOf(WireConnection.class, string);
    }

    private WireConnection(String name) {
        this.name = name;
    }

    public String toString() {
        return this.asString();
    }

    public String asString() {
        return this.name;
    }

    public boolean isConnected() {
        return this != NONE;
    }

    private static /* synthetic */ WireConnection[] method_36733() {
        return new WireConnection[]{UP, SIDE, NONE};
    }

    static {
        field_12688 = WireConnection.method_36733();
    }
}

