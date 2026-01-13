/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.AbstractRailBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.EntityShapeContext
 *  net.minecraft.block.ExperimentalMinecartShapeContext
 *  net.minecraft.block.ExperimentalMinecartShapeContext$1
 *  net.minecraft.block.enums.RailShape
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.vehicle.AbstractMinecartEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.CollisionView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ExperimentalMinecartShapeContext;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.CollisionView;
import org.jspecify.annotations.Nullable;

public class ExperimentalMinecartShapeContext
extends EntityShapeContext {
    private @Nullable BlockPos belowPos;
    private @Nullable BlockPos ascendingPos;

    protected ExperimentalMinecartShapeContext(AbstractMinecartEntity minecart, boolean collidesWithFluid) {
        super((Entity)minecart, collidesWithFluid, false);
        this.setIgnoredPositions(minecart);
    }

    private void setIgnoredPositions(AbstractMinecartEntity minecart) {
        BlockPos blockPos = minecart.getRailOrMinecartPos();
        BlockState blockState = minecart.getEntityWorld().getBlockState(blockPos);
        boolean bl = AbstractRailBlock.isRail((BlockState)blockState);
        if (bl) {
            this.belowPos = blockPos.down();
            RailShape railShape = (RailShape)blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
            if (railShape.isAscending()) {
                this.ascendingPos = switch (1.field_53826[railShape.ordinal()]) {
                    case 1 -> blockPos.east();
                    case 2 -> blockPos.west();
                    case 3 -> blockPos.north();
                    case 4 -> blockPos.south();
                    default -> null;
                };
            }
        }
    }

    public VoxelShape getCollisionShape(BlockState state, CollisionView world, BlockPos pos) {
        if (pos.equals((Object)this.belowPos) || pos.equals((Object)this.ascendingPos)) {
            return VoxelShapes.empty();
        }
        return super.getCollisionShape(state, world, pos);
    }
}

