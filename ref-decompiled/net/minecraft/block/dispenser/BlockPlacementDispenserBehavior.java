/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.block.DispenserBlock
 *  net.minecraft.block.dispenser.BlockPlacementDispenserBehavior
 *  net.minecraft.block.dispenser.FallibleItemDispenserBehavior
 *  net.minecraft.item.AutomaticItemPlacementContext
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPointer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.World
 *  org.slf4j.Logger
 */
package net.minecraft.block.dispenser;

import com.mojang.logging.LogUtils;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class BlockPlacementDispenserBehavior
extends FallibleItemDispenserBehavior {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        this.setSuccess(false);
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Direction direction = (Direction)pointer.state().get((Property)DispenserBlock.FACING);
            BlockPos blockPos = pointer.pos().offset(direction);
            Direction direction2 = pointer.world().isAir(blockPos.down()) ? direction : Direction.UP;
            try {
                this.setSuccess(((BlockItem)item).place((ItemPlacementContext)new AutomaticItemPlacementContext((World)pointer.world(), blockPos, direction, stack, direction2)).isAccepted());
            }
            catch (Exception exception) {
                LOGGER.error("Error trying to place shulker box at {}", (Object)blockPos, (Object)exception);
            }
        }
        return stack;
    }
}

