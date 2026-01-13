/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.AbstractFurnaceBlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.FurnaceBlockEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.recipe.RecipeType
 *  net.minecraft.screen.FurnaceScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class FurnaceBlockEntity
extends AbstractFurnaceBlockEntity {
    private static final Text CONTAINER_NAME_TEXT = Text.translatable((String)"container.furnace");

    public FurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.FURNACE, pos, state, RecipeType.SMELTING);
    }

    protected Text getContainerName() {
        return CONTAINER_NAME_TEXT;
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new FurnaceScreenHandler(syncId, playerInventory, (Inventory)this, this.propertyDelegate);
    }
}

