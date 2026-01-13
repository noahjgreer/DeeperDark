/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.screen.PropertyDelegate;

class BrewingStandBlockEntity.1
implements PropertyDelegate {
    BrewingStandBlockEntity.1() {
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> BrewingStandBlockEntity.this.brewTime;
            case 1 -> BrewingStandBlockEntity.this.fuel;
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case 0: {
                BrewingStandBlockEntity.this.brewTime = value;
                break;
            }
            case 1: {
                BrewingStandBlockEntity.this.fuel = value;
            }
        }
    }

    @Override
    public int size() {
        return 2;
    }
}
