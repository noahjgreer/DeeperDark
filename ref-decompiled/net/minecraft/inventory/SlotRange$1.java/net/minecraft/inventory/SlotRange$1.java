/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.inventory;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.SlotRange;

static class SlotRange.1
implements SlotRange {
    final /* synthetic */ IntList field_49742;
    final /* synthetic */ String field_49743;

    SlotRange.1() {
        this.field_49742 = intList;
        this.field_49743 = string;
    }

    @Override
    public IntList getSlotIds() {
        return this.field_49742;
    }

    @Override
    public String asString() {
        return this.field_49743;
    }

    public String toString() {
        return this.field_49743;
    }
}
