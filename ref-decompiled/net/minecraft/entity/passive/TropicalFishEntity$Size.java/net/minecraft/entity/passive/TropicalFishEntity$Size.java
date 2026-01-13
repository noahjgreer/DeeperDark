/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

public static final class TropicalFishEntity.Size
extends Enum<TropicalFishEntity.Size> {
    public static final /* enum */ TropicalFishEntity.Size SMALL = new TropicalFishEntity.Size(0);
    public static final /* enum */ TropicalFishEntity.Size LARGE = new TropicalFishEntity.Size(1);
    final int index;
    private static final /* synthetic */ TropicalFishEntity.Size[] field_41577;

    public static TropicalFishEntity.Size[] values() {
        return (TropicalFishEntity.Size[])field_41577.clone();
    }

    public static TropicalFishEntity.Size valueOf(String string) {
        return Enum.valueOf(TropicalFishEntity.Size.class, string);
    }

    private TropicalFishEntity.Size(int index) {
        this.index = index;
    }

    private static /* synthetic */ TropicalFishEntity.Size[] method_47866() {
        return new TropicalFishEntity.Size[]{SMALL, LARGE};
    }

    static {
        field_41577 = TropicalFishEntity.Size.method_47866();
    }
}
