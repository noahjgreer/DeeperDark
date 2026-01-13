/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.datafixer.fix;

public static final class ChunkPalettedStorageFix.Facing.Axis
extends Enum<ChunkPalettedStorageFix.Facing.Axis> {
    public static final /* enum */ ChunkPalettedStorageFix.Facing.Axis X = new ChunkPalettedStorageFix.Facing.Axis();
    public static final /* enum */ ChunkPalettedStorageFix.Facing.Axis Y = new ChunkPalettedStorageFix.Facing.Axis();
    public static final /* enum */ ChunkPalettedStorageFix.Facing.Axis Z = new ChunkPalettedStorageFix.Facing.Axis();
    private static final /* synthetic */ ChunkPalettedStorageFix.Facing.Axis[] field_15868;

    public static ChunkPalettedStorageFix.Facing.Axis[] values() {
        return (ChunkPalettedStorageFix.Facing.Axis[])field_15868.clone();
    }

    public static ChunkPalettedStorageFix.Facing.Axis valueOf(String string) {
        return Enum.valueOf(ChunkPalettedStorageFix.Facing.Axis.class, string);
    }

    private static /* synthetic */ ChunkPalettedStorageFix.Facing.Axis[] method_36591() {
        return new ChunkPalettedStorageFix.Facing.Axis[]{X, Y, Z};
    }

    static {
        field_15868 = ChunkPalettedStorageFix.Facing.Axis.method_36591();
    }
}
