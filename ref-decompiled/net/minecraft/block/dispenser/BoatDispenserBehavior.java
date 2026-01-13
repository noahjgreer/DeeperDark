/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.DispenserBlock
 *  net.minecraft.block.dispenser.BoatDispenserBehavior
 *  net.minecraft.block.dispenser.ItemDispenserBehavior
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.vehicle.AbstractBoatEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPointer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoatDispenserBehavior
extends ItemDispenserBehavior {
    private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();
    private final EntityType<? extends AbstractBoatEntity> boatType;

    public BoatDispenserBehavior(EntityType<? extends AbstractBoatEntity> boatType) {
        this.boatType = boatType;
    }

    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        double h;
        Direction direction = (Direction)pointer.state().get((Property)DispenserBlock.FACING);
        ServerWorld serverWorld = pointer.world();
        Vec3d vec3d = pointer.centerPos();
        double d = 0.5625 + (double)this.boatType.getWidth() / 2.0;
        double e = vec3d.getX() + (double)direction.getOffsetX() * d;
        double f = vec3d.getY() + (double)((float)direction.getOffsetY() * 1.125f);
        double g = vec3d.getZ() + (double)direction.getOffsetZ() * d;
        BlockPos blockPos = pointer.pos().offset(direction);
        if (serverWorld.getFluidState(blockPos).isIn(FluidTags.WATER)) {
            h = 1.0;
        } else if (serverWorld.getBlockState(blockPos).isAir() && serverWorld.getFluidState(blockPos.down()).isIn(FluidTags.WATER)) {
            h = 0.0;
        } else {
            return this.fallbackBehavior.dispense(pointer, stack);
        }
        AbstractBoatEntity abstractBoatEntity = (AbstractBoatEntity)this.boatType.create((World)serverWorld, SpawnReason.DISPENSER);
        if (abstractBoatEntity != null) {
            abstractBoatEntity.initPosition(e, f + h, g);
            EntityType.copier((World)serverWorld, (ItemStack)stack, null).accept(abstractBoatEntity);
            abstractBoatEntity.setYaw(direction.getPositiveHorizontalDegrees());
            serverWorld.spawnEntity((Entity)abstractBoatEntity);
            stack.decrement(1);
        }
        return stack;
    }

    protected void playSound(BlockPointer pointer) {
        pointer.world().syncWorldEvent(1000, pointer.pos(), 0);
    }
}

