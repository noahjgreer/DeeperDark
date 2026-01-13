/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.EntityShapeContext
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.CollisionView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import org.jspecify.annotations.Nullable;

public class EntityShapeContext
implements ShapeContext {
    private final boolean descending;
    private final double minY;
    private final boolean placement;
    private final ItemStack heldItem;
    private final boolean shouldTreatFluidAsCube;
    private final @Nullable Entity entity;

    protected EntityShapeContext(boolean descending, boolean placement, double minY, ItemStack heldItem, boolean shouldTreatFluidAsCube, @Nullable Entity entity) {
        this.descending = descending;
        this.placement = placement;
        this.minY = minY;
        this.heldItem = heldItem;
        this.shouldTreatFluidAsCube = shouldTreatFluidAsCube;
        this.entity = entity;
    }

    @Deprecated
    protected EntityShapeContext(Entity entity, boolean shouldTreatFluidAsCube, boolean placement) {
        ItemStack itemStack;
        boolean bl = entity.isDescending();
        double d = entity.getY();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            itemStack = livingEntity.getMainHandStack();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        this(bl, placement, d, itemStack, shouldTreatFluidAsCube, entity);
    }

    public boolean isHolding(Item item) {
        return this.heldItem.isOf(item);
    }

    public boolean shouldTreatFluidAsCube() {
        return this.shouldTreatFluidAsCube;
    }

    public boolean canWalkOnFluid(FluidState stateAbove, FluidState state) {
        Entity entity = this.entity;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            return livingEntity.canWalkOnFluid(state) && !stateAbove.getFluid().matchesType(state.getFluid());
        }
        return false;
    }

    public VoxelShape getCollisionShape(BlockState state, CollisionView world, BlockPos pos) {
        return state.getCollisionShape((BlockView)world, pos, (ShapeContext)this);
    }

    public boolean isDescending() {
        return this.descending;
    }

    public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
        return this.minY > (double)pos.getY() + shape.getMax(Direction.Axis.Y) - (double)1.0E-5f;
    }

    public @Nullable Entity getEntity() {
        return this.entity;
    }

    public boolean isPlacement() {
        return this.placement;
    }
}

