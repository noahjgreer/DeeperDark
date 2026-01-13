/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.Nullable;

public static final class ActionResult.ItemContext
extends Record {
    final boolean incrementStat;
    final @Nullable ItemStack newHandStack;
    static ActionResult.ItemContext KEEP_HAND_STACK_NO_INCREMENT_STAT = new ActionResult.ItemContext(false, null);
    static ActionResult.ItemContext KEEP_HAND_STACK = new ActionResult.ItemContext(true, null);

    public ActionResult.ItemContext(boolean incrementStat, @Nullable ItemStack newHandStack) {
        this.incrementStat = incrementStat;
        this.newHandStack = newHandStack;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ActionResult.ItemContext.class, "wasItemInteraction;heldItemTransformedTo", "incrementStat", "newHandStack"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ActionResult.ItemContext.class, "wasItemInteraction;heldItemTransformedTo", "incrementStat", "newHandStack"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ActionResult.ItemContext.class, "wasItemInteraction;heldItemTransformedTo", "incrementStat", "newHandStack"}, this, object);
    }

    public boolean incrementStat() {
        return this.incrementStat;
    }

    public @Nullable ItemStack newHandStack() {
        return this.newHandStack;
    }
}
