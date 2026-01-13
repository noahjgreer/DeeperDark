/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.entity.Hopper
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.util.math.Box
 */
package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Box;

public interface Hopper
extends Inventory {
    public static final Box INPUT_AREA_SHAPE = (Box)Block.createColumnShape((double)16.0, (double)11.0, (double)32.0).getBoundingBoxes().get(0);

    default public Box getInputAreaShape() {
        return INPUT_AREA_SHAPE;
    }

    public double getHopperX();

    public double getHopperY();

    public double getHopperZ();

    public boolean canBlockFromAbove();
}

