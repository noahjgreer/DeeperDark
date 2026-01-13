/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.Tilt
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class Tilt
extends Enum<Tilt>
implements StringIdentifiable {
    public static final /* enum */ Tilt NONE = new Tilt("NONE", 0, "none", true);
    public static final /* enum */ Tilt UNSTABLE = new Tilt("UNSTABLE", 1, "unstable", false);
    public static final /* enum */ Tilt PARTIAL = new Tilt("PARTIAL", 2, "partial", true);
    public static final /* enum */ Tilt FULL = new Tilt("FULL", 3, "full", true);
    private final String name;
    private final boolean stable;
    private static final /* synthetic */ Tilt[] field_28724;

    public static Tilt[] values() {
        return (Tilt[])field_28724.clone();
    }

    public static Tilt valueOf(String string) {
        return Enum.valueOf(Tilt.class, string);
    }

    private Tilt(String name, boolean stable) {
        this.name = name;
        this.stable = stable;
    }

    public String asString() {
        return this.name;
    }

    public boolean isStable() {
        return this.stable;
    }

    private static /* synthetic */ Tilt[] method_36738() {
        return new Tilt[]{NONE, UNSTABLE, PARTIAL, FULL};
    }

    static {
        field_28724 = Tilt.method_36738();
    }
}

