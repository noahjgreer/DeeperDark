/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.DoorHinge
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class DoorHinge
extends Enum<DoorHinge>
implements StringIdentifiable {
    public static final /* enum */ DoorHinge LEFT = new DoorHinge("LEFT", 0);
    public static final /* enum */ DoorHinge RIGHT = new DoorHinge("RIGHT", 1);
    private static final /* synthetic */ DoorHinge[] field_12587;

    public static DoorHinge[] values() {
        return (DoorHinge[])field_12587.clone();
    }

    public static DoorHinge valueOf(String string) {
        return Enum.valueOf(DoorHinge.class, string);
    }

    public String toString() {
        return this.asString();
    }

    public String asString() {
        return this == LEFT ? "left" : "right";
    }

    private static /* synthetic */ DoorHinge[] method_36726() {
        return new DoorHinge[]{LEFT, RIGHT};
    }

    static {
        field_12587 = DoorHinge.method_36726();
    }
}

