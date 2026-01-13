/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

protected static class EntityShapeContext.Absent
extends EntityShapeContext {
    protected static final ShapeContext INSTANCE = new EntityShapeContext.Absent(false);
    protected static final ShapeContext TREAT_FLUID_AS_CUBE = new EntityShapeContext.Absent(true);

    public EntityShapeContext.Absent(boolean shouldTreatFluidAsCube) {
        super(false, false, -1.7976931348623157E308, ItemStack.EMPTY, shouldTreatFluidAsCube, null);
    }

    @Override
    public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
        return defaultValue;
    }
}
