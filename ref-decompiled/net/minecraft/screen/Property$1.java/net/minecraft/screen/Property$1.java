/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.screen.Property;
import net.minecraft.screen.PropertyDelegate;

static class Property.1
extends Property {
    final /* synthetic */ PropertyDelegate field_17308;
    final /* synthetic */ int field_17309;

    Property.1(PropertyDelegate propertyDelegate, int i) {
        this.field_17308 = propertyDelegate;
        this.field_17309 = i;
    }

    @Override
    public int get() {
        return this.field_17308.get(this.field_17309);
    }

    @Override
    public void set(int value) {
        this.field_17308.set(this.field_17309, value);
    }
}
