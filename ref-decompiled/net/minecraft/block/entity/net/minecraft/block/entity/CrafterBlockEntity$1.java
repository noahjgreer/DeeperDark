/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.screen.PropertyDelegate;

class CrafterBlockEntity.1
implements PropertyDelegate {
    private final int[] disabledSlots = new int[9];
    private int triggered = 0;

    CrafterBlockEntity.1(CrafterBlockEntity crafterBlockEntity) {
    }

    @Override
    public int get(int index) {
        return index == 9 ? this.triggered : this.disabledSlots[index];
    }

    @Override
    public void set(int index, int value) {
        if (index == 9) {
            this.triggered = value;
        } else {
            this.disabledSlots[index] = value;
        }
    }

    @Override
    public int size() {
        return 10;
    }
}
