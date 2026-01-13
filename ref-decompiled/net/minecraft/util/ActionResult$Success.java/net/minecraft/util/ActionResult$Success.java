/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.jspecify.annotations.Nullable;

public record ActionResult.Success(ActionResult.SwingSource swingSource, ActionResult.ItemContext itemContext) implements ActionResult
{
    @Override
    public boolean isAccepted() {
        return true;
    }

    public ActionResult.Success withNewHandStack(ItemStack newHandStack) {
        return new ActionResult.Success(this.swingSource, new ActionResult.ItemContext(true, newHandStack));
    }

    public ActionResult.Success noIncrementStat() {
        return new ActionResult.Success(this.swingSource, ActionResult.ItemContext.KEEP_HAND_STACK_NO_INCREMENT_STAT);
    }

    public boolean shouldIncrementStat() {
        return this.itemContext.incrementStat;
    }

    public @Nullable ItemStack getNewHandStack() {
        return this.itemContext.newHandStack;
    }
}
