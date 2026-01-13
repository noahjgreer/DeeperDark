/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.Attachment
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class Attachment
extends Enum<Attachment>
implements StringIdentifiable {
    public static final /* enum */ Attachment FLOOR = new Attachment("FLOOR", 0, "floor");
    public static final /* enum */ Attachment CEILING = new Attachment("CEILING", 1, "ceiling");
    public static final /* enum */ Attachment SINGLE_WALL = new Attachment("SINGLE_WALL", 2, "single_wall");
    public static final /* enum */ Attachment DOUBLE_WALL = new Attachment("DOUBLE_WALL", 3, "double_wall");
    private final String name;
    private static final /* synthetic */ Attachment[] field_17103;

    public static Attachment[] values() {
        return (Attachment[])field_17103.clone();
    }

    public static Attachment valueOf(String string) {
        return Enum.valueOf(Attachment.class, string);
    }

    private Attachment(String name) {
        this.name = name;
    }

    public String asString() {
        return this.name;
    }

    private static /* synthetic */ Attachment[] method_36723() {
        return new Attachment[]{FLOOR, CEILING, SINGLE_WALL, DOUBLE_WALL};
    }

    static {
        field_17103 = Attachment.method_36723();
    }
}

