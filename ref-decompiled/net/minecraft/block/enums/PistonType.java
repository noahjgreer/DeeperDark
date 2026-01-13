/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.PistonType
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class PistonType
extends Enum<PistonType>
implements StringIdentifiable {
    public static final /* enum */ PistonType DEFAULT = new PistonType("DEFAULT", 0, "normal");
    public static final /* enum */ PistonType STICKY = new PistonType("STICKY", 1, "sticky");
    private final String name;
    private static final /* synthetic */ PistonType[] field_12636;

    public static PistonType[] values() {
        return (PistonType[])field_12636.clone();
    }

    public static PistonType valueOf(String string) {
        return Enum.valueOf(PistonType.class, string);
    }

    private PistonType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }

    private static /* synthetic */ PistonType[] method_36731() {
        return new PistonType[]{DEFAULT, STICKY};
    }

    static {
        field_12636 = PistonType.method_36731();
    }
}

