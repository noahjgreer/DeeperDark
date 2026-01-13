/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class Octree.AxisOrder
extends Enum<Octree.AxisOrder> {
    public static final /* enum */ Octree.AxisOrder XYZ = new Octree.AxisOrder(4, 2, 1);
    public static final /* enum */ Octree.AxisOrder XZY = new Octree.AxisOrder(4, 1, 2);
    public static final /* enum */ Octree.AxisOrder YXZ = new Octree.AxisOrder(2, 4, 1);
    public static final /* enum */ Octree.AxisOrder YZX = new Octree.AxisOrder(1, 4, 2);
    public static final /* enum */ Octree.AxisOrder ZXY = new Octree.AxisOrder(2, 1, 4);
    public static final /* enum */ Octree.AxisOrder ZYX = new Octree.AxisOrder(1, 2, 4);
    final int x;
    final int y;
    final int z;
    private static final /* synthetic */ Octree.AxisOrder[] field_53915;

    public static Octree.AxisOrder[] values() {
        return (Octree.AxisOrder[])field_53915.clone();
    }

    public static Octree.AxisOrder valueOf(String string) {
        return Enum.valueOf(Octree.AxisOrder.class, string);
    }

    private Octree.AxisOrder(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Octree.AxisOrder fromPos(int x, int y, int z) {
        if (x > y && x > z) {
            if (y > z) {
                return XYZ;
            }
            return XZY;
        }
        if (y > x && y > z) {
            if (x > z) {
                return YXZ;
            }
            return YZX;
        }
        if (x > y) {
            return ZXY;
        }
        return ZYX;
    }

    private static /* synthetic */ Octree.AxisOrder[] method_62913() {
        return new Octree.AxisOrder[]{XYZ, XZY, YXZ, YZX, ZXY, ZYX};
    }

    static {
        field_53915 = Octree.AxisOrder.method_62913();
    }
}
