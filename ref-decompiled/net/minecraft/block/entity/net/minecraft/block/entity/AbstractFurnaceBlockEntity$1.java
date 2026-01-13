/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.screen.PropertyDelegate;

class AbstractFurnaceBlockEntity.1
implements PropertyDelegate {
    AbstractFurnaceBlockEntity.1() {
    }

    @Override
    public int get(int index) {
        switch (index) {
            case 0: {
                return AbstractFurnaceBlockEntity.this.litTimeRemaining;
            }
            case 1: {
                return AbstractFurnaceBlockEntity.this.litTotalTime;
            }
            case 2: {
                return AbstractFurnaceBlockEntity.this.cookingTimeSpent;
            }
            case 3: {
                return AbstractFurnaceBlockEntity.this.cookingTotalTime;
            }
        }
        return 0;
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case 0: {
                AbstractFurnaceBlockEntity.this.litTimeRemaining = value;
                break;
            }
            case 1: {
                AbstractFurnaceBlockEntity.this.litTotalTime = value;
                break;
            }
            case 2: {
                AbstractFurnaceBlockEntity.this.cookingTimeSpent = value;
                break;
            }
            case 3: {
                AbstractFurnaceBlockEntity.this.cookingTotalTime = value;
                break;
            }
        }
    }

    @Override
    public int size() {
        return 4;
    }
}
