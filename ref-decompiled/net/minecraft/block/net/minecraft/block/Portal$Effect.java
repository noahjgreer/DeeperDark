/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

public static final class Portal.Effect
extends Enum<Portal.Effect> {
    public static final /* enum */ Portal.Effect CONFUSION = new Portal.Effect();
    public static final /* enum */ Portal.Effect NONE = new Portal.Effect();
    private static final /* synthetic */ Portal.Effect[] field_52063;

    public static Portal.Effect[] values() {
        return (Portal.Effect[])field_52063.clone();
    }

    public static Portal.Effect valueOf(String string) {
        return Enum.valueOf(Portal.Effect.class, string);
    }

    private static /* synthetic */ Portal.Effect[] method_60779() {
        return new Portal.Effect[]{CONFUSION, NONE};
    }

    static {
        field_52063 = Portal.Effect.method_60779();
    }
}
