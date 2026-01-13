/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.family;

public static final class BlockFamily.Variant
extends Enum<BlockFamily.Variant> {
    public static final /* enum */ BlockFamily.Variant BUTTON = new BlockFamily.Variant("button");
    public static final /* enum */ BlockFamily.Variant CHISELED = new BlockFamily.Variant("chiseled");
    public static final /* enum */ BlockFamily.Variant CRACKED = new BlockFamily.Variant("cracked");
    public static final /* enum */ BlockFamily.Variant CUT = new BlockFamily.Variant("cut");
    public static final /* enum */ BlockFamily.Variant DOOR = new BlockFamily.Variant("door");
    public static final /* enum */ BlockFamily.Variant CUSTOM_FENCE = new BlockFamily.Variant("fence");
    public static final /* enum */ BlockFamily.Variant FENCE = new BlockFamily.Variant("fence");
    public static final /* enum */ BlockFamily.Variant CUSTOM_FENCE_GATE = new BlockFamily.Variant("fence_gate");
    public static final /* enum */ BlockFamily.Variant FENCE_GATE = new BlockFamily.Variant("fence_gate");
    public static final /* enum */ BlockFamily.Variant MOSAIC = new BlockFamily.Variant("mosaic");
    public static final /* enum */ BlockFamily.Variant SIGN = new BlockFamily.Variant("sign");
    public static final /* enum */ BlockFamily.Variant SLAB = new BlockFamily.Variant("slab");
    public static final /* enum */ BlockFamily.Variant STAIRS = new BlockFamily.Variant("stairs");
    public static final /* enum */ BlockFamily.Variant PRESSURE_PLATE = new BlockFamily.Variant("pressure_plate");
    public static final /* enum */ BlockFamily.Variant POLISHED = new BlockFamily.Variant("polished");
    public static final /* enum */ BlockFamily.Variant TRAPDOOR = new BlockFamily.Variant("trapdoor");
    public static final /* enum */ BlockFamily.Variant WALL = new BlockFamily.Variant("wall");
    public static final /* enum */ BlockFamily.Variant WALL_SIGN = new BlockFamily.Variant("wall_sign");
    private final String name;
    private static final /* synthetic */ BlockFamily.Variant[] field_28547;

    public static BlockFamily.Variant[] values() {
        return (BlockFamily.Variant[])field_28547.clone();
    }

    public static BlockFamily.Variant valueOf(String string) {
        return Enum.valueOf(BlockFamily.Variant.class, string);
    }

    private BlockFamily.Variant(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private static /* synthetic */ BlockFamily.Variant[] method_36938() {
        return new BlockFamily.Variant[]{BUTTON, CHISELED, CRACKED, CUT, DOOR, CUSTOM_FENCE, FENCE, CUSTOM_FENCE_GATE, FENCE_GATE, MOSAIC, SIGN, SLAB, STAIRS, PRESSURE_PLATE, POLISHED, TRAPDOOR, WALL, WALL_SIGN};
    }

    static {
        field_28547 = BlockFamily.Variant.method_36938();
    }
}
