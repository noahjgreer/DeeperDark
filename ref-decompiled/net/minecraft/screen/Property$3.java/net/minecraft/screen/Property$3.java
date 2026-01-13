/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.screen.Property;

static class Property.3
extends Property {
    private int value;

    Property.3() {
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }
}
