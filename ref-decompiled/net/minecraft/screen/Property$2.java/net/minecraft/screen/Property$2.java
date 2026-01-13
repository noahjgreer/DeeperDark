/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.screen.Property;

static class Property.2
extends Property {
    final /* synthetic */ int[] field_17310;
    final /* synthetic */ int field_17311;

    Property.2(int[] is, int i) {
        this.field_17310 = is;
        this.field_17311 = i;
    }

    @Override
    public int get() {
        return this.field_17310[this.field_17311];
    }

    @Override
    public void set(int value) {
        this.field_17310[this.field_17311] = value;
    }
}
