/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.ComparatorBlockEntity
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

public class ComparatorBlockEntity
extends BlockEntity {
    private static final int DEFAULT_OUTPUT_SIGNAL = 0;
    private int outputSignal = 0;

    public ComparatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.COMPARATOR, pos, state);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("OutputSignal", this.outputSignal);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.outputSignal = view.getInt("OutputSignal", 0);
    }

    public int getOutputSignal() {
        return this.outputSignal;
    }

    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }
}

