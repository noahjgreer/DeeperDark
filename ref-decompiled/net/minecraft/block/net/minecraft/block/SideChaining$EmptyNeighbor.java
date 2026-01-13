/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.SideChaining;
import net.minecraft.block.enums.SideChainPart;
import net.minecraft.util.math.BlockPos;

public record SideChaining.EmptyNeighbor(BlockPos pos) implements SideChaining.Neighbor
{
    @Override
    public boolean isChained() {
        return false;
    }

    @Override
    public boolean isNotCenter() {
        return true;
    }

    @Override
    public boolean isCenterOr(SideChainPart part) {
        return false;
    }
}
