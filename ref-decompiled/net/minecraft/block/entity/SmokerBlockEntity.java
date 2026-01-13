/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.AbstractFurnaceBlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.SmokerBlockEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.item.FuelRegistry
 *  net.minecraft.item.ItemStack
 *  net.minecraft.recipe.RecipeType
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.SmokerScreenHandler
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SmokerBlockEntity
extends AbstractFurnaceBlockEntity {
    private static final Text CONTAINER_NAME_TEXT = Text.translatable((String)"container.smoker");

    public SmokerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SMOKER, pos, state, RecipeType.SMOKING);
    }

    protected Text getContainerName() {
        return CONTAINER_NAME_TEXT;
    }

    protected int getFuelTime(FuelRegistry fuelRegistry, ItemStack stack) {
        return super.getFuelTime(fuelRegistry, stack) / 2;
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new SmokerScreenHandler(syncId, playerInventory, (Inventory)this, this.propertyDelegate);
    }
}

