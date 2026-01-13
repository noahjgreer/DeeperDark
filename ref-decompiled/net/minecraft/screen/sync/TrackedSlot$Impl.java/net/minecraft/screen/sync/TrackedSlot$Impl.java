/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.screen.sync;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.screen.sync.TrackedSlot;
import org.jspecify.annotations.Nullable;

public static class TrackedSlot.Impl
implements TrackedSlot {
    private final ComponentChangesHash.ComponentHasher hasher;
    private @Nullable ItemStack receivedStack = null;
    private @Nullable ItemStackHash receivedHash = null;

    public TrackedSlot.Impl(ComponentChangesHash.ComponentHasher hasher) {
        this.hasher = hasher;
    }

    @Override
    public void setReceivedStack(ItemStack receivedStack) {
        this.receivedStack = receivedStack.copy();
        this.receivedHash = null;
    }

    @Override
    public void setReceivedHash(ItemStackHash receivedHash) {
        this.receivedStack = null;
        this.receivedHash = receivedHash;
    }

    @Override
    public boolean isInSync(ItemStack actualStack) {
        if (this.receivedStack != null) {
            return ItemStack.areEqual(this.receivedStack, actualStack);
        }
        if (this.receivedHash != null && this.receivedHash.hashEquals(actualStack, this.hasher)) {
            this.receivedStack = actualStack.copy();
            return true;
        }
        return false;
    }

    public void copyFrom(TrackedSlot.Impl slot) {
        this.receivedStack = slot.receivedStack;
        this.receivedHash = slot.receivedHash;
    }
}
