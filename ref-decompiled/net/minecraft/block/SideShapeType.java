/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.SideShapeType
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.BlockView
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

/*
 * Exception performing whole class analysis ignored.
 */
public abstract sealed class SideShapeType
extends Enum<SideShapeType> {
    public static final /* enum */ SideShapeType FULL = new /* Unavailable Anonymous Inner Class!! */;
    public static final /* enum */ SideShapeType CENTER = new /* Unavailable Anonymous Inner Class!! */;
    public static final /* enum */ SideShapeType RIGID = new /* Unavailable Anonymous Inner Class!! */;
    private static final /* synthetic */ SideShapeType[] field_25825;

    public static SideShapeType[] values() {
        return (SideShapeType[])field_25825.clone();
    }

    public static SideShapeType valueOf(String string) {
        return Enum.valueOf(SideShapeType.class, string);
    }

    public abstract boolean matches(BlockState var1, BlockView var2, BlockPos var3, Direction var4);

    private static /* synthetic */ SideShapeType[] method_36711() {
        return new SideShapeType[]{FULL, CENTER, RIGID};
    }

    static {
        field_25825 = SideShapeType.method_36711();
    }
}

