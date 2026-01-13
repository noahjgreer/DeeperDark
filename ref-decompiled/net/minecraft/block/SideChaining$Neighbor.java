/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.SideChaining;
import net.minecraft.block.enums.SideChainPart;
import net.minecraft.util.math.BlockPos;

public static sealed interface SideChaining.Neighbor
permits SideChaining.EmptyNeighbor, SideChaining.SideChainNeighbor {
    public BlockPos pos();

    public boolean isChained();

    public boolean isNotCenter();

    public boolean isCenterOr(SideChainPart var1);

    default public void connectToRight() {
    }

    default public void connectToLeft() {
    }

    default public void disconnectFromRight() {
    }

    default public void disconnectFromLeft() {
    }
}
