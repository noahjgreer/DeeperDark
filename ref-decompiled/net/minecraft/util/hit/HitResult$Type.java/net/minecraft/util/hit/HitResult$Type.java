/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.hit;

public static final class HitResult.Type
extends Enum<HitResult.Type> {
    public static final /* enum */ HitResult.Type MISS = new HitResult.Type();
    public static final /* enum */ HitResult.Type BLOCK = new HitResult.Type();
    public static final /* enum */ HitResult.Type ENTITY = new HitResult.Type();
    private static final /* synthetic */ HitResult.Type[] field_1334;

    public static HitResult.Type[] values() {
        return (HitResult.Type[])field_1334.clone();
    }

    public static HitResult.Type valueOf(String string) {
        return Enum.valueOf(HitResult.Type.class, string);
    }

    private static /* synthetic */ HitResult.Type[] method_36796() {
        return new HitResult.Type[]{MISS, BLOCK, ENTITY};
    }

    static {
        field_1334 = HitResult.Type.method_36796();
    }
}
