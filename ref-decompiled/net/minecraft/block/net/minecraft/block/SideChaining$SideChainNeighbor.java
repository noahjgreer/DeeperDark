/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.SideChaining;
import net.minecraft.block.enums.SideChainPart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public record SideChaining.SideChainNeighbor(WorldAccess level, SideChaining block, BlockPos pos, SideChainPart part) implements SideChaining.Neighbor
{
    @Override
    public boolean isChained() {
        return true;
    }

    @Override
    public boolean isNotCenter() {
        return this.part.isNotCenter();
    }

    @Override
    public boolean isCenterOr(SideChainPart part) {
        return this.part.isCenterOr(part);
    }

    @Override
    public void connectToRight() {
        this.block.setSideChainPart(this.level, this.pos, this.part.connectToRight());
    }

    @Override
    public void connectToLeft() {
        this.block.setSideChainPart(this.level, this.pos, this.part.connectToLeft());
    }

    @Override
    public void disconnectFromRight() {
        this.block.setSideChainPart(this.level, this.pos, this.part.disconnectFromRight());
    }

    @Override
    public void disconnectFromLeft() {
        this.block.setSideChainPart(this.level, this.pos, this.part.disconnectFromLeft());
    }
}
