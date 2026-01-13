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
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
static final class BlockModelRenderer.Translation
extends Enum<BlockModelRenderer.Translation> {
    public static final /* enum */ BlockModelRenderer.Translation DOWN = new BlockModelRenderer.Translation(0, 1, 2, 3);
    public static final /* enum */ BlockModelRenderer.Translation UP = new BlockModelRenderer.Translation(2, 3, 0, 1);
    public static final /* enum */ BlockModelRenderer.Translation NORTH = new BlockModelRenderer.Translation(3, 0, 1, 2);
    public static final /* enum */ BlockModelRenderer.Translation SOUTH = new BlockModelRenderer.Translation(0, 1, 2, 3);
    public static final /* enum */ BlockModelRenderer.Translation WEST = new BlockModelRenderer.Translation(3, 0, 1, 2);
    public static final /* enum */ BlockModelRenderer.Translation EAST = new BlockModelRenderer.Translation(1, 2, 3, 0);
    final int firstCorner;
    final int secondCorner;
    final int thirdCorner;
    final int fourthCorner;
    private static final BlockModelRenderer.Translation[] VALUES;
    private static final /* synthetic */ BlockModelRenderer.Translation[] field_4208;

    public static BlockModelRenderer.Translation[] values() {
        return (BlockModelRenderer.Translation[])field_4208.clone();
    }

    public static BlockModelRenderer.Translation valueOf(String string) {
        return Enum.valueOf(BlockModelRenderer.Translation.class, string);
    }

    private BlockModelRenderer.Translation(int firstCorner, int secondCorner, int thirdCorner, int fourthCorner) {
        this.firstCorner = firstCorner;
        this.secondCorner = secondCorner;
        this.thirdCorner = thirdCorner;
        this.fourthCorner = fourthCorner;
    }

    public static BlockModelRenderer.Translation getTranslations(Direction direction) {
        return VALUES[direction.getIndex()];
    }

    private static /* synthetic */ BlockModelRenderer.Translation[] method_36918() {
        return new BlockModelRenderer.Translation[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
    }

    static {
        field_4208 = BlockModelRenderer.Translation.method_36918();
        VALUES = Util.make(new BlockModelRenderer.Translation[6], values -> {
            values[Direction.DOWN.getIndex()] = DOWN;
            values[Direction.UP.getIndex()] = UP;
            values[Direction.NORTH.getIndex()] = NORTH;
            values[Direction.SOUTH.getIndex()] = SOUTH;
            values[Direction.WEST.getIndex()] = WEST;
            values[Direction.EAST.getIndex()] = EAST;
        });
    }
}
