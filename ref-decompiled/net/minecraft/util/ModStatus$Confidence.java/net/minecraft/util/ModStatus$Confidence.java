/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public static final class ModStatus.Confidence
extends Enum<ModStatus.Confidence> {
    public static final /* enum */ ModStatus.Confidence PROBABLY_NOT = new ModStatus.Confidence("Probably not.", false);
    public static final /* enum */ ModStatus.Confidence VERY_LIKELY = new ModStatus.Confidence("Very likely;", true);
    public static final /* enum */ ModStatus.Confidence DEFINITELY = new ModStatus.Confidence("Definitely;", true);
    final String description;
    final boolean modded;
    private static final /* synthetic */ ModStatus.Confidence[] field_35179;

    public static ModStatus.Confidence[] values() {
        return (ModStatus.Confidence[])field_35179.clone();
    }

    public static ModStatus.Confidence valueOf(String string) {
        return Enum.valueOf(ModStatus.Confidence.class, string);
    }

    private ModStatus.Confidence(String description, boolean modded) {
        this.description = description;
        this.modded = modded;
    }

    private static /* synthetic */ ModStatus.Confidence[] method_39033() {
        return new ModStatus.Confidence[]{PROBABLY_NOT, VERY_LIKELY, DEFINITELY};
    }

    static {
        field_35179 = ModStatus.Confidence.method_39033();
    }
}
