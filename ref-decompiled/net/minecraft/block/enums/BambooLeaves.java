/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.BambooLeaves
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class BambooLeaves
extends Enum<BambooLeaves>
implements StringIdentifiable {
    public static final /* enum */ BambooLeaves NONE = new BambooLeaves("NONE", 0, "none");
    public static final /* enum */ BambooLeaves SMALL = new BambooLeaves("SMALL", 1, "small");
    public static final /* enum */ BambooLeaves LARGE = new BambooLeaves("LARGE", 2, "large");
    private final String name;
    private static final /* synthetic */ BambooLeaves[] field_12470;

    public static BambooLeaves[] values() {
        return (BambooLeaves[])field_12470.clone();
    }

    public static BambooLeaves valueOf(String string) {
        return Enum.valueOf(BambooLeaves.class, string);
    }

    private BambooLeaves(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }

    private static /* synthetic */ BambooLeaves[] method_36721() {
        return new BambooLeaves[]{NONE, SMALL, LARGE};
    }

    static {
        field_12470 = BambooLeaves.method_36721();
    }
}

