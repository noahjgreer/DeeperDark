/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.advancement;

static final class AdvancementDisplays.Status
extends Enum<AdvancementDisplays.Status> {
    public static final /* enum */ AdvancementDisplays.Status SHOW = new AdvancementDisplays.Status();
    public static final /* enum */ AdvancementDisplays.Status HIDE = new AdvancementDisplays.Status();
    public static final /* enum */ AdvancementDisplays.Status NO_CHANGE = new AdvancementDisplays.Status();
    private static final /* synthetic */ AdvancementDisplays.Status[] field_41741;

    public static AdvancementDisplays.Status[] values() {
        return (AdvancementDisplays.Status[])field_41741.clone();
    }

    public static AdvancementDisplays.Status valueOf(String string) {
        return Enum.valueOf(AdvancementDisplays.Status.class, string);
    }

    private static /* synthetic */ AdvancementDisplays.Status[] method_48034() {
        return new AdvancementDisplays.Status[]{SHOW, HIDE, NO_CHANGE};
    }

    static {
        field_41741 = AdvancementDisplays.Status.method_48034();
    }
}
