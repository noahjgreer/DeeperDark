/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.SculkSensorPhase
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class SculkSensorPhase
extends Enum<SculkSensorPhase>
implements StringIdentifiable {
    public static final /* enum */ SculkSensorPhase INACTIVE = new SculkSensorPhase("INACTIVE", 0, "inactive");
    public static final /* enum */ SculkSensorPhase ACTIVE = new SculkSensorPhase("ACTIVE", 1, "active");
    public static final /* enum */ SculkSensorPhase COOLDOWN = new SculkSensorPhase("COOLDOWN", 2, "cooldown");
    private final String name;
    private static final /* synthetic */ SculkSensorPhase[] field_28125;

    public static SculkSensorPhase[] values() {
        return (SculkSensorPhase[])field_28125.clone();
    }

    public static SculkSensorPhase valueOf(String string) {
        return Enum.valueOf(SculkSensorPhase.class, string);
    }

    private SculkSensorPhase(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }

    private static /* synthetic */ SculkSensorPhase[] method_36734() {
        return new SculkSensorPhase[]{INACTIVE, ACTIVE, COOLDOWN};
    }

    static {
        field_28125 = SculkSensorPhase.method_36734();
    }
}

