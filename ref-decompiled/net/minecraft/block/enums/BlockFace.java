/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.BlockFace
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class BlockFace
extends Enum<BlockFace>
implements StringIdentifiable {
    public static final /* enum */ BlockFace FLOOR = new BlockFace("FLOOR", 0, "floor");
    public static final /* enum */ BlockFace WALL = new BlockFace("WALL", 1, "wall");
    public static final /* enum */ BlockFace CEILING = new BlockFace("CEILING", 2, "ceiling");
    private final String name;
    private static final /* synthetic */ BlockFace[] field_12474;

    public static BlockFace[] values() {
        return (BlockFace[])field_12474.clone();
    }

    public static BlockFace valueOf(String string) {
        return Enum.valueOf(BlockFace.class, string);
    }

    private BlockFace(String name) {
        this.name = name;
    }

    public String asString() {
        return this.name;
    }

    private static /* synthetic */ BlockFace[] method_36720() {
        return new BlockFace[]{FLOOR, WALL, CEILING};
    }

    static {
        field_12474 = BlockFace.method_36720();
    }
}

