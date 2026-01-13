/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.Thickness
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class Thickness
extends Enum<Thickness>
implements StringIdentifiable {
    public static final /* enum */ Thickness TIP_MERGE = new Thickness("TIP_MERGE", 0, "tip_merge");
    public static final /* enum */ Thickness TIP = new Thickness("TIP", 1, "tip");
    public static final /* enum */ Thickness FRUSTUM = new Thickness("FRUSTUM", 2, "frustum");
    public static final /* enum */ Thickness MIDDLE = new Thickness("MIDDLE", 3, "middle");
    public static final /* enum */ Thickness BASE = new Thickness("BASE", 4, "base");
    private final String name;
    private static final /* synthetic */ Thickness[] field_28070;

    public static Thickness[] values() {
        return (Thickness[])field_28070.clone();
    }

    public static Thickness valueOf(String string) {
        return Enum.valueOf(Thickness.class, string);
    }

    private Thickness(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }

    private static /* synthetic */ Thickness[] method_36728() {
        return new Thickness[]{TIP_MERGE, TIP, FRUSTUM, MIDDLE, BASE};
    }

    static {
        field_28070 = Thickness.method_36728();
    }
}

