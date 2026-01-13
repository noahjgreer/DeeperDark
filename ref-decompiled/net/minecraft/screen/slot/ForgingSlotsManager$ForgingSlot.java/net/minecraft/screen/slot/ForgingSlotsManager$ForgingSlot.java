/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen.slot;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;

public static final class ForgingSlotsManager.ForgingSlot
extends Record {
    final int slotId;
    private final int x;
    private final int y;
    private final Predicate<ItemStack> mayPlace;
    static final ForgingSlotsManager.ForgingSlot DEFAULT = new ForgingSlotsManager.ForgingSlot(0, 0, 0, stack -> true);

    public ForgingSlotsManager.ForgingSlot(int slotId, int x, int y, Predicate<ItemStack> mayPlace) {
        this.slotId = slotId;
        this.x = x;
        this.y = y;
        this.mayPlace = mayPlace;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ForgingSlotsManager.ForgingSlot.class, "slotIndex;x;y;mayPlace", "slotId", "x", "y", "mayPlace"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ForgingSlotsManager.ForgingSlot.class, "slotIndex;x;y;mayPlace", "slotId", "x", "y", "mayPlace"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ForgingSlotsManager.ForgingSlot.class, "slotIndex;x;y;mayPlace", "slotId", "x", "y", "mayPlace"}, this, object);
    }

    public int slotId() {
        return this.slotId;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public Predicate<ItemStack> mayPlace() {
        return this.mayPlace;
    }
}
