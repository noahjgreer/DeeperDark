/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.DispenserBlock
 *  net.minecraft.block.dispenser.DispenserBehavior
 *  net.minecraft.block.dispenser.ItemDispenserBehavior
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPointer
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.Position
 *  net.minecraft.world.World
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public class ItemDispenserBehavior
implements DispenserBehavior {
    private static final int field_51916 = 6;

    public final ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
        ItemStack itemStack2 = this.dispenseSilently(blockPointer, itemStack);
        this.playSound(blockPointer);
        this.spawnParticles(blockPointer, (Direction)blockPointer.state().get((Property)DispenserBlock.FACING));
        return itemStack2;
    }

    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction direction = (Direction)pointer.state().get((Property)DispenserBlock.FACING);
        Position position = DispenserBlock.getOutputLocation((BlockPointer)pointer);
        ItemStack itemStack = stack.split(1);
        ItemDispenserBehavior.spawnItem((World)pointer.world(), (ItemStack)itemStack, (int)6, (Direction)direction, (Position)position);
        return stack;
    }

    public static void spawnItem(World world, ItemStack stack, int speed, Direction side, Position pos) {
        double d = pos.getX();
        double e = pos.getY();
        double f = pos.getZ();
        e = side.getAxis() == Direction.Axis.Y ? (e -= 0.125) : (e -= 0.15625);
        ItemEntity itemEntity = new ItemEntity(world, d, e, f, stack);
        double g = world.random.nextDouble() * 0.1 + 0.2;
        itemEntity.setVelocity(world.random.nextTriangular((double)side.getOffsetX() * g, 0.0172275 * (double)speed), world.random.nextTriangular(0.2, 0.0172275 * (double)speed), world.random.nextTriangular((double)side.getOffsetZ() * g, 0.0172275 * (double)speed));
        world.spawnEntity((Entity)itemEntity);
    }

    protected void playSound(BlockPointer pointer) {
        ItemDispenserBehavior.syncDispensesEvent((BlockPointer)pointer);
    }

    protected void spawnParticles(BlockPointer pointer, Direction side) {
        ItemDispenserBehavior.syncActivatesEvent((BlockPointer)pointer, (Direction)side);
    }

    private static void syncDispensesEvent(BlockPointer pointer) {
        pointer.world().syncWorldEvent(1000, pointer.pos(), 0);
    }

    private static void syncActivatesEvent(BlockPointer pointer, Direction side) {
        pointer.world().syncWorldEvent(2000, pointer.pos(), side.getIndex());
    }

    protected ItemStack decrementStackWithRemainder(BlockPointer pointer, ItemStack stack, ItemStack remainder) {
        stack.decrement(1);
        if (stack.isEmpty()) {
            return remainder;
        }
        this.addStackOrSpawn(pointer, remainder);
        return stack;
    }

    private void addStackOrSpawn(BlockPointer pointer, ItemStack stack) {
        ItemStack itemStack = pointer.blockEntity().addToFirstFreeSlot(stack);
        if (itemStack.isEmpty()) {
            return;
        }
        Direction direction = (Direction)pointer.state().get((Property)DispenserBlock.FACING);
        ItemDispenserBehavior.spawnItem((World)pointer.world(), (ItemStack)itemStack, (int)6, (Direction)direction, (Position)DispenserBlock.getOutputLocation((BlockPointer)pointer));
        ItemDispenserBehavior.syncDispensesEvent((BlockPointer)pointer);
        ItemDispenserBehavior.syncActivatesEvent((BlockPointer)pointer, (Direction)direction);
    }
}

