/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

static final class VaultState.Light
extends Enum<VaultState.Light> {
    public static final /* enum */ VaultState.Light HALF_LIT = new VaultState.Light(6);
    public static final /* enum */ VaultState.Light LIT = new VaultState.Light(12);
    final int luminance;
    private static final /* synthetic */ VaultState.Light[] field_48914;

    public static VaultState.Light[] values() {
        return (VaultState.Light[])field_48914.clone();
    }

    public static VaultState.Light valueOf(String string) {
        return Enum.valueOf(VaultState.Light.class, string);
    }

    private VaultState.Light(int luminance) {
        this.luminance = luminance;
    }

    private static /* synthetic */ VaultState.Light[] method_56809() {
        return new VaultState.Light[]{HALF_LIT, LIT};
    }

    static {
        field_48914 = VaultState.Light.method_56809();
    }
}
