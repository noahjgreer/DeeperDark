/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;

@FunctionalInterface
public static interface ProjectileItem.PositionFunction {
    public Position getDispensePosition(BlockPointer var1, Direction var2);
}
