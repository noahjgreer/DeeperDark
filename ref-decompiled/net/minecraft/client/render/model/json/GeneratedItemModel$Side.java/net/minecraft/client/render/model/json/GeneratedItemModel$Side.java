/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
static final class GeneratedItemModel.Side
extends Enum<GeneratedItemModel.Side> {
    public static final /* enum */ GeneratedItemModel.Side UP = new GeneratedItemModel.Side(Direction.UP);
    public static final /* enum */ GeneratedItemModel.Side DOWN = new GeneratedItemModel.Side(Direction.DOWN);
    public static final /* enum */ GeneratedItemModel.Side LEFT = new GeneratedItemModel.Side(Direction.EAST);
    public static final /* enum */ GeneratedItemModel.Side RIGHT = new GeneratedItemModel.Side(Direction.WEST);
    final Direction direction;
    private static final /* synthetic */ GeneratedItemModel.Side[] field_4282;

    public static GeneratedItemModel.Side[] values() {
        return (GeneratedItemModel.Side[])field_4282.clone();
    }

    public static GeneratedItemModel.Side valueOf(String string) {
        return Enum.valueOf(GeneratedItemModel.Side.class, string);
    }

    private GeneratedItemModel.Side(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return this.direction;
    }

    boolean isVertical() {
        return this == DOWN || this == UP;
    }

    private static /* synthetic */ GeneratedItemModel.Side[] method_36921() {
        return new GeneratedItemModel.Side[]{UP, DOWN, LEFT, RIGHT};
    }

    static {
        field_4282 = GeneratedItemModel.Side.method_36921();
    }
}
