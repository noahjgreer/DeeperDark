/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

public static final class Cracks.CrackLevel
extends Enum<Cracks.CrackLevel> {
    public static final /* enum */ Cracks.CrackLevel NONE = new Cracks.CrackLevel();
    public static final /* enum */ Cracks.CrackLevel LOW = new Cracks.CrackLevel();
    public static final /* enum */ Cracks.CrackLevel MEDIUM = new Cracks.CrackLevel();
    public static final /* enum */ Cracks.CrackLevel HIGH = new Cracks.CrackLevel();
    private static final /* synthetic */ Cracks.CrackLevel[] field_21085;

    public static Cracks.CrackLevel[] values() {
        return (Cracks.CrackLevel[])field_21085.clone();
    }

    public static Cracks.CrackLevel valueOf(String string) {
        return Enum.valueOf(Cracks.CrackLevel.class, string);
    }

    private static /* synthetic */ Cracks.CrackLevel[] method_36638() {
        return new Cracks.CrackLevel[]{NONE, LOW, MEDIUM, HIGH};
    }

    static {
        field_21085 = Cracks.CrackLevel.method_36638();
    }
}
