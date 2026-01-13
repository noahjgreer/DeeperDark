/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.dispenser.FallibleItemDispenserBehavior
 *  net.minecraft.block.dispenser.ItemDispenserBehavior
 *  net.minecraft.util.math.BlockPointer
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.util.math.BlockPointer;

public abstract class FallibleItemDispenserBehavior
extends ItemDispenserBehavior {
    private boolean success = true;

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    protected void playSound(BlockPointer pointer) {
        pointer.world().syncWorldEvent(this.isSuccess() ? 1000 : 1001, pointer.pos(), 0);
    }
}

