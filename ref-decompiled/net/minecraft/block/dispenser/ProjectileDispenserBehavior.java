/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.DispenserBlock
 *  net.minecraft.block.dispenser.ItemDispenserBehavior
 *  net.minecraft.block.dispenser.ProjectileDispenserBehavior
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ProjectileItem
 *  net.minecraft.item.ProjectileItem$Settings
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPointer
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Position
 *  net.minecraft.world.World
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class ProjectileDispenserBehavior
extends ItemDispenserBehavior {
    private final ProjectileItem projectile;
    private final ProjectileItem.Settings projectileSettings;

    public ProjectileDispenserBehavior(Item item) {
        if (!(item instanceof ProjectileItem)) {
            throw new IllegalArgumentException(String.valueOf(item) + " not instance of " + ProjectileItem.class.getSimpleName());
        }
        ProjectileItem projectileItem = (ProjectileItem)item;
        this.projectile = projectileItem;
        this.projectileSettings = projectileItem.getProjectileSettings();
    }

    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld serverWorld = pointer.world();
        Direction direction = (Direction)pointer.state().get((Property)DispenserBlock.FACING);
        Position position = this.projectileSettings.positionFunction().getDispensePosition(pointer, direction);
        ProjectileEntity.spawnWithVelocity((ProjectileEntity)this.projectile.createEntity((World)serverWorld, position, stack, direction), (ServerWorld)serverWorld, (ItemStack)stack, (double)direction.getOffsetX(), (double)direction.getOffsetY(), (double)direction.getOffsetZ(), (float)this.projectileSettings.power(), (float)this.projectileSettings.uncertainty());
        stack.decrement(1);
        return stack;
    }

    protected void playSound(BlockPointer pointer) {
        pointer.world().syncWorldEvent(this.projectileSettings.overrideDispenseEvent().orElse(1002), pointer.pos(), 0);
    }
}

