/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
protected static final class BlockModelRenderer.NeighborOrientation
extends Enum<BlockModelRenderer.NeighborOrientation> {
    public static final /* enum */ BlockModelRenderer.NeighborOrientation DOWN = new BlockModelRenderer.NeighborOrientation(0);
    public static final /* enum */ BlockModelRenderer.NeighborOrientation UP = new BlockModelRenderer.NeighborOrientation(1);
    public static final /* enum */ BlockModelRenderer.NeighborOrientation NORTH = new BlockModelRenderer.NeighborOrientation(2);
    public static final /* enum */ BlockModelRenderer.NeighborOrientation SOUTH = new BlockModelRenderer.NeighborOrientation(3);
    public static final /* enum */ BlockModelRenderer.NeighborOrientation WEST = new BlockModelRenderer.NeighborOrientation(4);
    public static final /* enum */ BlockModelRenderer.NeighborOrientation EAST = new BlockModelRenderer.NeighborOrientation(5);
    public static final /* enum */ BlockModelRenderer.NeighborOrientation FLIP_DOWN = new BlockModelRenderer.NeighborOrientation(6);
    public static final /* enum */ BlockModelRenderer.NeighborOrientation FLIP_UP = new BlockModelRenderer.NeighborOrientation(7);
    public static final /* enum */ BlockModelRenderer.NeighborOrientation FLIP_NORTH = new BlockModelRenderer.NeighborOrientation(8);
    public static final /* enum */ BlockModelRenderer.NeighborOrientation FLIP_SOUTH = new BlockModelRenderer.NeighborOrientation(9);
    public static final /* enum */ BlockModelRenderer.NeighborOrientation FLIP_WEST = new BlockModelRenderer.NeighborOrientation(10);
    public static final /* enum */ BlockModelRenderer.NeighborOrientation FLIP_EAST = new BlockModelRenderer.NeighborOrientation(11);
    public static final int SIZE;
    final int index;
    private static final /* synthetic */ BlockModelRenderer.NeighborOrientation[] field_4223;

    public static BlockModelRenderer.NeighborOrientation[] values() {
        return (BlockModelRenderer.NeighborOrientation[])field_4223.clone();
    }

    public static BlockModelRenderer.NeighborOrientation valueOf(String string) {
        return Enum.valueOf(BlockModelRenderer.NeighborOrientation.class, string);
    }

    private BlockModelRenderer.NeighborOrientation(int index) {
        this.index = index;
    }

    private static /* synthetic */ BlockModelRenderer.NeighborOrientation[] method_36919() {
        return new BlockModelRenderer.NeighborOrientation[]{DOWN, UP, NORTH, SOUTH, WEST, EAST, FLIP_DOWN, FLIP_UP, FLIP_NORTH, FLIP_SOUTH, FLIP_WEST, FLIP_EAST};
    }

    static {
        field_4223 = BlockModelRenderer.NeighborOrientation.method_36919();
        SIZE = BlockModelRenderer.NeighborOrientation.values().length;
    }
}
