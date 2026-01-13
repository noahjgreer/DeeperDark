/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.datafixer.fix;

public static final class ChunkPalettedStorageFix.Facing.Direction
extends Enum<ChunkPalettedStorageFix.Facing.Direction> {
    public static final /* enum */ ChunkPalettedStorageFix.Facing.Direction POSITIVE = new ChunkPalettedStorageFix.Facing.Direction(1);
    public static final /* enum */ ChunkPalettedStorageFix.Facing.Direction NEGATIVE = new ChunkPalettedStorageFix.Facing.Direction(-1);
    private final int offset;
    private static final /* synthetic */ ChunkPalettedStorageFix.Facing.Direction[] field_15871;

    public static ChunkPalettedStorageFix.Facing.Direction[] values() {
        return (ChunkPalettedStorageFix.Facing.Direction[])field_15871.clone();
    }

    public static ChunkPalettedStorageFix.Facing.Direction valueOf(String string) {
        return Enum.valueOf(ChunkPalettedStorageFix.Facing.Direction.class, string);
    }

    private ChunkPalettedStorageFix.Facing.Direction(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return this.offset;
    }

    private static /* synthetic */ ChunkPalettedStorageFix.Facing.Direction[] method_36592() {
        return new ChunkPalettedStorageFix.Facing.Direction[]{POSITIVE, NEGATIVE};
    }

    static {
        field_15871 = ChunkPalettedStorageFix.Facing.Direction.method_36592();
    }
}
