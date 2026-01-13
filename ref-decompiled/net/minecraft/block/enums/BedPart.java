/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.BedPart
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class BedPart
extends Enum<BedPart>
implements StringIdentifiable {
    public static final /* enum */ BedPart HEAD = new BedPart("HEAD", 0, "head");
    public static final /* enum */ BedPart FOOT = new BedPart("FOOT", 1, "foot");
    private final String name;
    private static final /* synthetic */ BedPart[] field_12558;

    public static BedPart[] values() {
        return (BedPart[])field_12558.clone();
    }

    public static BedPart valueOf(String string) {
        return Enum.valueOf(BedPart.class, string);
    }

    private BedPart(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }

    private static /* synthetic */ BedPart[] method_36722() {
        return new BedPart[]{HEAD, FOOT};
    }

    static {
        field_12558 = BedPart.method_36722();
    }
}

